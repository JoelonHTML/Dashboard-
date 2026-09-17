const express = require("express");
const { fetchFs25Stats } = require("../services/fs25Service");
const { getCached } = require("../cache");

function createFs25Router(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    const stats = await getCached("fs25", cacheTtlSeconds, () =>
      fetchFs25Stats(process.env.FS25_STATS_URL)
    );
    res.json(stats);
  });

  return router;
}

module.exports = { createFs25Router };
