const express = require("express");
const { fetchNsDepartures } = require("../services/nsService");
const { getCached } = require("../cache");

function createNsRouter(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    const stats = await getCached("ns", cacheTtlSeconds, () =>
      fetchNsDepartures(process.env.NS_API_KEY, process.env.NS_STATION_CODE)
    );
    res.json(stats);
  });

  return router;
}

module.exports = { createNsRouter };
