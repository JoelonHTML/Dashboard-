const BASE_URL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/departures";

/**
 * NS Reisinformatie API — vertrekkenstabel voor één station.
 * Key aanvragen (gratis) via https://apiportal.ns.nl. Zonder key of stationscode
 * geven we een duidelijke offline-status terug in plaats van te crashen.
 */
async function fetchNsDepartures(apiKey, stationCode) {
  if (!apiKey) {
    return { online: false, error: "NS_API_KEY is niet ingesteld", departures: [], updatedAt: new Date().toISOString() };
  }
  if (!stationCode) {
    return { online: false, error: "NS_STATION_CODE is niet ingesteld", departures: [], updatedAt: new Date().toISOString() };
  }

  let response;
  try {
    response = await fetch(`${BASE_URL}?station=${encodeURIComponent(stationCode)}&maxJourneys=10`, {
      headers: { "Ocp-Apim-Subscription-Key": apiKey },
      signal: AbortSignal.timeout(8000),
    });
  } catch (err) {
    return { online: false, error: err.message, departures: [], updatedAt: new Date().toISOString() };
  }

  if (!response.ok) {
    return {
      online: false,
      error: `NS-API antwoordde met status ${response.status}`,
      departures: [],
      updatedAt: new Date().toISOString(),
    };
  }

  try {
    const body = await response.json();
    const rawDepartures = body?.payload?.departures ?? [];

    const departures = rawDepartures.map((d) => {
      const planned = d.plannedDateTime ?? null;
      const actual = d.actualDateTime ?? planned;
      const delayMinutes = planned && actual
        ? Math.round((new Date(actual).getTime() - new Date(planned).getTime()) / 60000)
        : 0;

      return {
        destination: d.direction ?? "Onbekend",
        trainType: d.product?.shortCategoryName ?? d.name ?? "Trein",
        plannedTime: planned,
        actualTime: actual,
        delayMinutes,
        platform: d.actualTrack ?? d.plannedTrack ?? null,
        platformChanged: Boolean(d.actualTrack && d.plannedTrack && d.actualTrack !== d.plannedTrack),
        cancelled: Boolean(d.cancelled),
      };
    });

    return { online: true, stationCode, departures, updatedAt: new Date().toISOString() };
  } catch (err) {
    return { online: false, error: `Kon NS-response niet verwerken: ${err.message}`, departures: [], updatedAt: new Date().toISOString() };
  }
}

module.exports = { fetchNsDepartures };
