#Requires -RunAsAdministrator
<#
.SYNOPSIS
  Zet deze laptop op als host voor de Daily Dashboard-backend: installeert
  dependencies, vult .env aan, en registreert de service via NSSM zodat hij
  automatisch opstart (ook zonder ingelogde gebruiker) en herstart bij een crash.

.USAGE
  Rechtermuisklik dit bestand -> "Uitvoeren met PowerShell" (of open PowerShell
  als Administrator en run: .\install-windows-service.ps1)
#>

$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$backendDir = Split-Path -Parent $PSScriptRoot
$serviceName = "DashboardBackend"

Write-Host "== Daily Dashboard backend installeren als Windows-service ==" -ForegroundColor Cyan
Write-Host "Backend-map: $backendDir`n"

# 1. Node.js aanwezig?
$node = Get-Command node -ErrorAction SilentlyContinue
if (-not $node) {
    Write-Host "Node.js is niet gevonden. Installeer eerst Node.js (LTS) via https://nodejs.org/ en run dit script opnieuw." -ForegroundColor Red
    exit 1
}
Write-Host "Node.js gevonden: $($node.Source) ($(node --version))"

# 2. npm install
Write-Host "`nDependencies installeren (npm install)..."
Push-Location $backendDir
npm install
Pop-Location

# 3. .env aanmaken/aanvullen
$envPath = Join-Path $backendDir ".env"
if (-not (Test-Path $envPath)) {
    Write-Host "`nGeen .env gevonden — die maken we nu aan. Druk Enter om een veld leeg/standaard te laten." -ForegroundColor Yellow

    $port = Read-Host "Poort voor de backend [4000]"
    if ([string]::IsNullOrWhiteSpace($port)) { $port = "4000" }

    $fs25Url = Read-Host "FS25 stats-feed URL (leeg = later zelf invullen in .env)"
    $calendarUrl = Read-Host "Publieke iCloud-agenda .ics-link (leeg = later invullen)"
    $remindersUrl = Read-Host "Publieke iCloud-Herinneringen .ics-link (leeg = later invullen)"

    @"
LAN_HOST=0.0.0.0
PORT=$port
FS25_STATS_URL=$fs25Url
CALENDAR_ICS_URL=$calendarUrl
REMINDERS_ICS_URL=$remindersUrl
CACHE_TTL_SECONDS=30
"@ | Set-Content -Path $envPath -Encoding UTF8

    Write-Host ".env aangemaakt op $envPath. Pas 'm later aan met Kladblok als je nog links moet toevoegen."
} else {
    Write-Host "`n.env bestaat al, die laat ik ongemoeid."
}

$envPort = "4000"
$portLine = Select-String -Path $envPath -Pattern "^PORT=(.+)$" -ErrorAction SilentlyContinue
if ($portLine) { $envPort = $portLine.Matches[0].Groups[1].Value.Trim() }

# 4. Firewall openzetten, anders kan de tablet niet binnenkomen ook al draait alles
$fwRuleName = "Daily Dashboard backend ($envPort)"
if (-not (Get-NetFirewallRule -DisplayName $fwRuleName -ErrorAction SilentlyContinue)) {
    Write-Host "`nFirewall-regel toevoegen voor poort $envPort (inkomend, TCP)..."
    New-NetFirewallRule -DisplayName $fwRuleName -Direction Inbound -LocalPort $envPort -Protocol TCP -Action Allow | Out-Null
} else {
    Write-Host "`nFirewall-regel voor poort $envPort bestaat al."
}

# 5. NSSM downloaden (indien nodig)
$toolsDir = Join-Path $backendDir "scripts\.tools"
New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
$arch = if ([Environment]::Is64BitOperatingSystem) { "win64" } else { "win32" }
$nssmExe = Join-Path $toolsDir "nssm-2.24\$arch\nssm.exe"

if (-not (Test-Path $nssmExe)) {
    Write-Host "`nNSSM downloaden..."
    $zipPath = Join-Path $toolsDir "nssm.zip"
    Invoke-WebRequest -Uri "https://nssm.cc/release/nssm-2.24.zip" -OutFile $zipPath -UseBasicParsing
    Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
    Remove-Item $zipPath
}
if (-not (Test-Path $nssmExe)) {
    Write-Host "NSSM-download/uitpakken is mislukt. Download 'm handmatig van https://nssm.cc/download en pas dit script aan." -ForegroundColor Red
    exit 1
}
Write-Host "NSSM gereed: $nssmExe"

# 6. Service (opnieuw) registreren
$existing = Get-Service -Name $serviceName -ErrorAction SilentlyContinue
if ($existing) {
    Write-Host "`nService '$serviceName' bestaat al — stoppen en verwijderen voor een schone herinstallatie..."
    & $nssmExe stop $serviceName confirm | Out-Null
    & $nssmExe remove $serviceName confirm | Out-Null
}

Write-Host "`nService '$serviceName' installeren..."
& $nssmExe install $serviceName $node.Source "$backendDir\src\index.js"
& $nssmExe set $serviceName AppDirectory $backendDir
& $nssmExe set $serviceName Start SERVICE_AUTO_START
& $nssmExe set $serviceName AppRestartDelay 5000
& $nssmExe set $serviceName AppStdout (Join-Path $backendDir "service.log")
& $nssmExe set $serviceName AppStderr (Join-Path $backendDir "service.log")

& $nssmExe start $serviceName

Start-Sleep -Seconds 2
$status = Get-Service -Name $serviceName

$lanIp = (Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notlike "127.*" -and $_.IPAddress -notlike "169.254.*" } |
    Select-Object -First 1 -ExpandProperty IPAddress)

Write-Host "`n== Klaar ==" -ForegroundColor Green
Write-Host "Service '$serviceName' status: $($status.Status)"
Write-Host "Logbestand: $backendDir\service.log"
Write-Host "Test in de browser (op deze laptop): http://localhost:$envPort/api/health"
if ($lanIp) {
    Write-Host "Adres voor de tablet-app (instellingen -> backend-adres): http://${lanIp}:$envPort" -ForegroundColor Cyan
}
Write-Host "Beheer de service via services.msc, of met 'nssm' vanaf $nssmExe."
