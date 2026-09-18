#Requires -RunAsAdministrator
<#
.SYNOPSIS
  Registreert één Taakplanner-taak op déze laptop, zodat de backend de FS25-server
  (een GUI-app) kan herstarten in de ingelogde sessie — een Windows-service zelf
  kan geen zichtbaar venster openen (sessie-0-isolatie), Taakplanner met
  LogonType Interactive wel.

  Draai dit EENMALIG op de laptop zelf (niet op de PC met de Stream Deck).

.USAGE
  .\setup-streamdeck-tasks.ps1 -Fs25ExePath "C:\pad\naar\FarmingSimulator2025DedicatedServer.exe"

  Pas -Fs25ExePath aan naar waar dat programma echt staat op deze laptop
  (rechtermuisklik op de snelkoppeling > Eigenschappen > "Doel").
#>

param(
    [string]$Fs25ExePath = "C:\Program Files (x86)\Farming Simulator 2025 Dedicated Server\FarmingSimulator2025DedicatedServer.exe"
)

$ErrorActionPreference = "Stop"
$fs25ProcessName = [System.IO.Path]::GetFileNameWithoutExtension($Fs25ExePath)

if (-not (Test-Path $Fs25ExePath)) {
    Write-Host "Let op: '$Fs25ExePath' bestaat niet op deze laptop. Pas -Fs25ExePath aan naar het echte pad." -ForegroundColor Yellow
}

$fs25Command = "taskkill /IM `"$fs25ProcessName.exe`" /F & timeout /t 3 /nobreak >nul & start `"`" `"$Fs25ExePath`""
$fs25Action = New-ScheduledTaskAction -Execute "cmd.exe" -Argument "/c $fs25Command"
$fs25Principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -RunLevel Highest -LogonType Interactive
Register-ScheduledTask -TaskName "RestartFs25Server" -Action $fs25Action -Principal $fs25Principal -Force | Out-Null

Write-Host "Taak 'RestartFs25Server' geregistreerd (herstart: $Fs25ExePath)."
Write-Host "Test met: schtasks /run /tn RestartFs25Server"
Write-Host "`nZorg ook dat ACTIONS_SECRET in backend\.env is ingevuld — daarachter zitten"
Write-Host "de /api/actions-routes die de Stream Deck (op de PC) straks aanroept."
