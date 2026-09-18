@echo off
REM Stream Deck-knop op de PC: vraagt de backend op de laptop om de laptop zelf
REM te herstarten. Vul hieronder het geheim in dat ook in backend\.env staat
REM (ACTIONS_SECRET) -- zonder match geeft de backend 401 terug.
set BACKEND_URL=http://192.168.1.43:4000
set ACTIONS_SECRET=VUL_HIER_HETZELFDE_GEHEIM_IN

curl -s -X POST -H "X-Actions-Secret: %ACTIONS_SECRET%" %BACKEND_URL%/api/actions/restart-laptop
