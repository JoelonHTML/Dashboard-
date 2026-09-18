#Requires -RunAsAdministrator
<#
.SYNOPSIS
  Registreert twee Taakplanner-taken zodat de Stream Deck-knoppen de backend-service
  en de FS25-server kunnen herstarten zonder dat er bij elke druk een UAC-scherm
  verschijnt (de taken zelf zijn al met verhoogde rechten geregistreerd).

.USAGE
  Eenmalig als Administrator uitvoeren, bijvoorbeeld:
  .\setup-streamdeck-tasks.ps1 -Fs25ExePath "C:\pad\naar\FarmingSimulator2025DedicatedServer.exe"

  Pas -Fs25ExePath aan naar waar dat programma op deze laptop staat
  (rechtermuisklik op de snelkoppeling > Eigenschappen > "Doel" laat het zien).
#>

param(
    [string]$Fs25ExePath = "C:\Program Files (x86)\Farming Simulator 2025 Dedicated Server\FarmingSimulator2025DedicatedServer.exe"
)

$ErrorActionPreference = "Stop"
$fs25ProcessName = [System.IO.Path]::GetFileNameWithoutExtension($Fs25ExePath)

if (-not (Test-Path $Fs25ExePath)) {
    Write-Host "Let op: '$Fs25ExePath' bestaat niet op deze laptop. Pas -Fs25ExePath aan naar het echte pad." -ForegroundColor Yellow
}

# --- Taak 1: backend-service herstarten. Draait op de achtergrond (geen GUI nodig). ---
$backendAction = New-ScheduledTaskAction -Execute "powershell.exe" `
    -Argument "-NoProfile -WindowStyle Hidden -Command `"Restart-Service -Name DashboardBackend -Force`""
$backendPrincipal = New-ScheduledTaskPrincipal -UserId "SYSTEM" -RunLevel Highest
Register-ScheduledTask -TaskName "RestartDashboardBackend" -Action $backendAction -Principal $backendPrincipal -Force | Out-Null
Write-Host "Taak 'RestartDashboardBackend' geregistreerd."

# --- Taak 2: FS25-server herstarten. Moet in de ingelogde sessie draaien, anders geen zichtbaar venster. ---
$fs25Command = "taskkill /IM `"$fs25ProcessName.exe`" /F & timeout /t 3 /nobreak >nul & start `"`" `"$Fs25ExePath`""
$fs25Action = New-ScheduledTaskAction -Execute "cmd.exe" -Argument "/c $fs25Command"
$fs25Principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -RunLevel Highest -LogonType Interactive
Register-ScheduledTask -TaskName "RestartFs25Server" -Action $fs25Action -Principal $fs25Principal -Force | Out-Null
Write-Host "Taak 'RestartFs25Server' geregistreerd (herstart: $Fs25ExePath)."

Write-Host "`nTest met:"
Write-Host "  schtasks /run /tn RestartDashboardBackend"
Write-Host "  schtasks /run /tn RestartFs25Server"
Write-Host "`nWijs in de Stream Deck-app nu de drie .bat-bestanden in deze map toe aan je knoppen (actie 'Systeem' > 'Openen')."
