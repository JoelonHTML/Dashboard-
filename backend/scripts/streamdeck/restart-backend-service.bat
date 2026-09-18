@echo off
REM Stream Deck-knop: herstart de "DashboardBackend"-Windows-service.
REM Vereist dat setup-streamdeck-tasks.ps1 eenmalig als Administrator is gedraaid.
schtasks /run /tn "RestartDashboardBackend"
