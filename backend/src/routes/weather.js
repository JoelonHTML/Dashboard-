const express = require("express");
const { fetchWeather } = require("../services/weatherService");
const { getCached } = require("../cache");

// Lage Zwaluwe, Noord-Brabant — pas WEATHER_LAT/WEATHER_LON in .env aan voor een andere locatie.
const DEFAULT_LAT = "51.686";
const DEFAULT_LON = "4.685";

function createWeatherRouter(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    const lat = process.env.WEATHER_LAT ?? DEFAULT_LAT;
    const lon = process.env.WEATHER_LON ?? DEFAULT_LON;
    const weather = await getCached("weather", cacheTtlSeconds, () => fetchWeather(lat, lon));
    res.json(weather);
  });

  return router;
}

module.exports = { createWeatherRouter };
