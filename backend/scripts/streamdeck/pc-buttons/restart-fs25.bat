@echo off
REM Stream Deck-knop: herstart de FS25-server op de laptop.
REM Vereist dat setup-streamdeck-tasks.ps1 eenmalig op de LAPTOP is gedraaid.
call "%~dp0config.bat"

if "%ACTIONS_SECRET%"=="VUL_HIER_JE_GEHEIM_IN" (
    echo [Dashboard] Vul eerst je geheim in in config.bat ^(zelfde als ACTIONS_SECRET in backend\.env^).
    timeout /t 5 >nul
    exit /b 1
)

where curl >nul 2>&1
if errorlevel 1 (
    echo [Dashboard] curl.exe niet gevonden. Vereist Windows 10 1803+, of installeer curl handmatig.
    timeout /t 5 >nul
    exit /b 1
)

set RESPONSE_FILE=%TEMP%\dashboard_restart_fs25.json
for /f "tokens=* delims=" %%a in ('curl -s -m 5 -o "%RESPONSE_FILE%" -w "%%{http_code}" -X POST -H "X-Actions-Secret: %ACTIONS_SECRET%" %BACKEND_URL%/api/actions/restart-fs25') do set HTTP_CODE=%%a

if "%HTTP_CODE%"=="200" (
    echo [Dashboard] Gelukt: FS25-server wordt herstart.
) else if "%HTTP_CODE%"=="401" (
    echo [Dashboard] Fout: geheim komt niet overeen. Check ACTIONS_SECRET in config.bat en backend\.env.
) else if "%HTTP_CODE%"=="503" (
    echo [Dashboard] Fout: ACTIONS_SECRET staat niet ingesteld op de backend zelf.
) else if "%HTTP_CODE%"=="000" (
    echo [Dashboard] Fout: kan de laptop niet bereiken. Staat de backend aan? Klopt het IP in config.bat?
) else if "%HTTP_CODE%"=="500" (
    echo [Dashboard] Fout: de Taakplanner-taak RestartFs25Server draaide niet. Is setup-streamdeck-tasks.ps1 al gedraaid op de laptop?
) else (
    echo [Dashboard] Fout: onverwachte status %HTTP_CODE%.
)

type "%RESPONSE_FILE%" 2>nul
echo.
del "%RESPONSE_FILE%" 2>nul
timeout /t 4 >nul
