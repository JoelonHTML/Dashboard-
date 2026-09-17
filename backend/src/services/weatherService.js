const BASE_URL = "https://api.open-meteo.com/v1/forecast";

// WMO weather-interpretation codes -> korte NL-omschrijving.
const WEATHER_DESCRIPTIONS = {
  0: "Helder",
  1: "Overwegend helder",
  2: "Half bewolkt",
  3: "Bewolkt",
  45: "Mist",
  48: "Aanvriezende mist",
  51: "Lichte motregen",
  53: "Motregen",
  55: "Zware motregen",
  61: "Lichte regen",
  63: "Regen",
  65: "Zware regen",
  66: "IJzel",
  67: "Zware ijzel",
  71: "Lichte sneeuw",
  73: "Sneeuw",
  75: "Zware sneeuw",
  77: "Sneeuwkorrels",
  80: "Lichte buien",
  81: "Buien",
  82: "Zware buien",
  85: "Sneeuwbuien",
  86: "Zware sneeuwbuien",
  95: "Onweer",
  96: "Onweer met hagel",
  99: "Zwaar onweer met hagel",
};

function describe(code) {
  return WEATHER_DESCRIPTIONS[code] ?? "Onbekend";
}

/** Open-Meteo vereist geen API-key — alleen coördinaten (default: Lage Zwaluwe). */
async function fetchWeather(lat, lon) {
  const url = `${BASE_URL}?latitude=${lat}&longitude=${lon}&current=temperature_2m,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,weather_code&timezone=auto&forecast_days=4`;

  let response;
  try {
    response = await fetch(url, { signal: AbortSignal.timeout(8000) });
  } catch (err) {
    return { online: false, error: err.message, updatedAt: new Date().toISOString() };
  }

  if (!response.ok) {
    return { online: false, error: `Weer-API antwoordde met status ${response.status}`, updatedAt: new Date().toISOString() };
  }

  try {
    const body = await response.json();
    const current = body.current ?? {};
    const daily = body.daily ?? {};
    const days = (daily.time ?? []).length;

    const forecast = [];
    for (let i = 0; i < days; i += 1) {
      forecast.push({
        date: daily.time[i],
        maxTempCelsius: daily.temperature_2m_max?.[i] ?? null,
        minTempCelsius: daily.temperature_2m_min?.[i] ?? null,
        description: describe(daily.weather_code?.[i]),
      });
    }

    return {
      online: true,
      currentTempCelsius: current.temperature_2m ?? null,
      currentDescription: describe(current.weather_code),
      windSpeedKmh: current.wind_speed_10m ?? null,
      forecast,
      updatedAt: new Date().toISOString(),
    };
  } catch (err) {
    return { online: false, error: `Kon weer-response niet verwerken: ${err.message}`, updatedAt: new Date().toISOString() };
  }
}

module.exports = { fetchWeather };
