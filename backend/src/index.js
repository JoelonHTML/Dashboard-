require("dotenv").config();
const express = require("express");

const { createSystemRouter } = require("./routes/system");
const { createFs25Router } = require("./routes/fs25");
const { createCalendarRouter } = require("./routes/calendar");
const { createRemindersRouter } = require("./routes/reminders");
const { createNsRouter } = require("./routes/ns");
const { createWeatherRouter } = require("./routes/weather");

const PORT = Number(process.env.PORT ?? 4000);
const LAN_HOST = process.env.LAN_HOST ?? "0.0.0.0";

// Snel-veranderende bronnen (systeemstats, FS25) mogen kort gecachet worden zodat
// een snelle app-ververssnelheid (tot 1s) niet meteen de laptop/FS25-server belast.
const CACHE_TTL_SECONDS = Number(process.env.CACHE_TTL_SECONDS ?? 3);
// Agenda/herinneringen/NS-vertrektijden veranderen niet elke seconde en de bronnen
// (iCloud, NS) zijn extern — die cachen we bewust langer.
const SLOW_CACHE_TTL_SECONDS = Number(process.env.SLOW_CACHE_TTL_SECONDS ?? 60);

const app = express();

app.get("/api/health", (req, res) => res.json({ status: "ok" }));
app.use("/api/system", createSystemRouter(CACHE_TTL_SECONDS));
app.use("/api/fs25", createFs25Router(CACHE_TTL_SECONDS));
app.use("/api/calendar", createCalendarRouter(SLOW_CACHE_TTL_SECONDS));
app.use("/api/reminders", createRemindersRouter(SLOW_CACHE_TTL_SECONDS));
app.use("/api/ns", createNsRouter(SLOW_CACHE_TTL_SECONDS));
app.use("/api/weather", createWeatherRouter(SLOW_CACHE_TTL_SECONDS));

app.listen(PORT, LAN_HOST, () => {
  console.log(`Dashboard-backend luistert op http://${LAN_HOST}:${PORT}`);
});
