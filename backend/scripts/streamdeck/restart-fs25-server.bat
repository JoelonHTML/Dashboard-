@echo off
REM Stream Deck-knop: sluit de FS25 dedicated server af en start 'm weer op.
REM Vereist dat setup-streamdeck-tasks.ps1 eenmalig als Administrator is gedraaid
REM (met het juiste -Fs25ExePath voor deze laptop).
schtasks /run /tn "RestartFs25Server"
