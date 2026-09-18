@echo off
REM Stream Deck-knop op de PC: herstart de FS25-server op de laptop.
REM Vereist dat setup-streamdeck-tasks.ps1 eenmalig op de LAPTOP is gedraaid.
set BACKEND_URL=http://192.168.1.43:4000
set ACTIONS_SECRET=VUL_HIER_HETZELFDE_GEHEIM_IN

curl -s -X POST -H "X-Actions-Secret: %ACTIONS_SECRET%" %BACKEND_URL%/api/actions/restart-fs25
