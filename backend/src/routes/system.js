const express = require("express");
const { getSystemStats } = require("../services/systemService");
const { getCached } = require("../cache");

function createSystemRouter(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    try {
      const stats = await getCached("system", cacheTtlSeconds, getSystemStats);
      res.json(stats);
    } catch (err) {
      res.status(500).json({ error: err.message });
    }
  });

  return router;
}

module.exports = { createSystemRouter };
