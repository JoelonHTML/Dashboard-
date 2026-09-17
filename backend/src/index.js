require("dotenv").config();
const express = require("express");

const { createSystemRouter } = require("./routes/system");
const { createFs25Router } = require("./routes/fs25");
const { createCalendarRouter } = require("./routes/calendar");
const { createRemindersRouter } = require("./routes/reminders");

const PORT = Number(process.env.PORT ?? 4000);
const LAN_HOST = process.env.LAN_HOST ?? "0.0.0.0";
const CACHE_TTL_SECONDS = Number(process.env.CACHE_TTL_SECONDS ?? 30);

const app = express();

app.get("/api/health", (req, res) => res.json({ status: "ok" }));
app.use("/api/system", createSystemRouter(CACHE_TTL_SECONDS));
app.use("/api/fs25", createFs25Router(CACHE_TTL_SECONDS));
app.use("/api/calendar", createCalendarRouter(CACHE_TTL_SECONDS));
app.use("/api/reminders", createRemindersRouter(CACHE_TTL_SECONDS));

app.listen(PORT, LAN_HOST, () => {
  console.log(`Dashboard-backend luistert op http://${LAN_HOST}:${PORT}`);
});
