#Requires -RunAsAdministrator
<#
.SYNOPSIS
  Verwijdert de Daily Dashboard-backend Windows-service die met
  install-windows-service.ps1 is aangemaakt. Laat .env en de code met rust.
#>

$ErrorActionPreference = "Stop"
$backendDir = Split-Path -Parent $PSScriptRoot
$serviceName = "DashboardBackend"

$arch = if ([Environment]::Is64BitOperatingSystem) { "win64" } else { "win32" }
$nssmExe = Join-Path $backendDir "scripts\.tools\nssm-2.24\$arch\nssm.exe"

$existing = Get-Service -Name $serviceName -ErrorAction SilentlyContinue
if (-not $existing) {
    Write-Host "Service '$serviceName' bestaat niet (meer) — niets te doen."
    exit 0
}

if (Test-Path $nssmExe) {
    & $nssmExe stop $serviceName confirm | Out-Null
    & $nssmExe remove $serviceName confirm | Out-Null
} else {
    Stop-Service -Name $serviceName -Force -ErrorAction SilentlyContinue
    sc.exe delete $serviceName | Out-Null
}

Write-Host "Service '$serviceName' verwijderd." -ForegroundColor Green
