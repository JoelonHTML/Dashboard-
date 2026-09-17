const express = require("express");
const { fetchOpenReminders } = require("../services/icalService");
const { getCached } = require("../cache");

function createRemindersRouter(cacheTtlSeconds) {
  const router = express.Router();

  router.get("/", async (req, res) => {
    try {
      const reminders = await getCached("reminders", cacheTtlSeconds, () =>
        fetchOpenReminders(process.env.REMINDERS_ICS_URL)
      );
      res.json({ reminders, updatedAt: new Date().toISOString() });
    } catch (err) {
      res.status(500).json({ error: err.message });
    }
  });

  return router;
}

module.exports = { createRemindersRouter };
