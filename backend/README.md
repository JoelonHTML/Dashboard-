# Dashboard-backend

Node.js/Express-service die op de Windows-laptop draait en systeemstats,
FS25-status en agenda/herinneringen als JSON aanbiedt op het lokale netwerk.

## Setup

```bash
cd backend
npm install
cp .env.example .env
```

Vul in `.env` in:

- `FS25_STATS_URL` — de stats-feed van je FS25 dedicated server (Server Settings → webserver, meestal poort 8080).
- `CALENDAR_ICS_URL` — publieke `.ics`-link van een gedeelde iCloud-agenda (Agenda-app → agenda delen → "Openbare agenda" → link kopiëren).
- `REMINDERS_ICS_URL` — publieke `.ics`-link van een gedeelde iCloud-Herinneringen-lijst (Herinneringen-app → lijst delen → "Openbare lijst" → link kopiëren).

Geen Apple ID of wachtwoord nodig — alleen publieke, leesalleen iCal-links.

```bash
npm start
```

De service luistert standaard op `http://0.0.0.0:4000` (alleen bereikbaar binnen je LAN).

## Endpoints

| Endpoint | Inhoud |
| --- | --- |
| `GET /api/health` | `{ status: "ok" }` |
| `GET /api/system` | CPU%, RAM%, schijfruimte, netwerksnelheid, uptime |
| `GET /api/fs25` | Online status, spelersaantal, spelerslijst, map, uptime |
| `GET /api/calendar` | Aankomende agenda-items (14 dagen vooruit) |
| `GET /api/reminders` | Openstaande herinneringen |

Elk endpoint cachet ~30 seconden (instelbaar via `CACHE_TTL_SECONDS`).

## Als Windows-service draaien (aanbevolen: NSSM)

```powershell
nssm install DashboardBackend
# Application path: C:\Program Files\nodejs\node.exe
# Startup directory: <pad-naar-repo>\backend
# Arguments: src\index.js
# Starttype: Automatic
```

Zo start de service automatisch op, ook zonder ingelogde gebruiker.

## FS25-stats-schema

`src/services/fs25Service.js` gaat uit van het standaard FS-dedicated-server
`stats.xml`-schema (`Server` → `Slots` → `Player`). Wijkt jouw server-response
hiervan af, pas dan de veldmapping in dat bestand aan.
