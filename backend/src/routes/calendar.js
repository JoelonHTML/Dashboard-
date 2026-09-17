const express = require("express");
const { fetchUpcomingEvents } = require("../services/icalService");
const { getCached } = require("../cache");

function createCalendarRouter(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    try {
      const events = await getCached("calendar", cacheTtlSeconds, () =>
        fetchUpcomingEvents(process.env.CALENDAR_ICS_URL)
      );
      res.json({ events, updatedAt: new Date().toISOString() });
    } catch (err) {
      res.status(500).json({ error: err.message });
    }
  });

  return router;
}

module.exports = { createCalendarRouter };
