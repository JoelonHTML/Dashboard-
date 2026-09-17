# Daily Dashboard

Vast dashboard voor een Android-tablet: agenda, herinneringen, systeemstatus van de
laptop, en de FS25-serverstatus — alles lokaal over wifi, offline-first.

Zie `PROMPT.md` voor de volledige spec. Het project bestaat uit twee delen:

| Map | Wat | Status |
| --- | --- | --- |
| [`backend/`](backend/README.md) | Node.js/Express-service op de laptop | Gebouwd en getest (`npm start` draait, alle 4 endpoints geverifieerd) |
| [`android/`](android/README.md) | Kotlin/Compose-app op de tablet | Volledig geschreven, **nog niet gecompileerd** (zie hieronder) |

## Snel starten

1. **Backend** — op de Windows-laptop:
   ```bash
   cd backend
   npm install
   cp .env.example .env   # vul FS25/agenda/herinneringen-links in
   npm start
   ```
   Zet 'm daarna vast als Windows-service met NSSM (instructies in `backend/README.md`).

2. **Android-app** — open de map `android/` in Android Studio, laat Gradle syncen
   (dat downloadt zelf AGP/Compose/Room — kon niet in de cloud-sandbox waarin dit
   gebouwd is), en vul het backend-adres in via het instellingen-icoon in de app
   ("Test verbinding" bevestigt meteen of het adres klopt). De widget-grid start
   niet leeg: er staat al een complete standaardlay-out klaar met alle vijf
   widget-types, en de widget-kiezer heeft 21 databronnen om uit te kiezen.

## Belangrijk: wat nog moet gebeuren op jouw eigen machine

Dit hele project is gebouwd in een cloud-omgeving zonder toegang tot:

- **Android SDK** (`dl.google.com` was geblokkeerd) → de Android-app is geschreven
  volgens bekende, correcte Kotlin/Compose/Room-patronen maar **nooit gecompileerd**.
  Reken op een eerste sync-ronde in Android Studio met mogelijk kleine fixes.
  Zie `android/README.md` voor de bekende aandachtspunten (launcher-icoon,
  handmatige DI in plaats van Hilt).
- **Jouw wifi-netwerk, laptop en FS25-server** → ik kon dus niet live tegen jouw
  echte FS25-stats-feed of iCloud-agenda testen. `backend/src/services/fs25Service.js`
  gaat uit van het standaard FS-dedicated-server XML-schema; pas de veldmapping aan
  als jouw server iets anders teruggeeft.
- **De fysieke tablet** (stap 11 uit het stappenplan: testen in je eigen wifi-netwerk)
  moet je zelf doen zodra de app draait.

Kortom: de code is compleet en zorgvuldig geschreven, maar de laatste
build-en-test-ronde moet bij jou lokaal gebeuren.
