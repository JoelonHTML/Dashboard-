const ical = require("node-ical");

/**
 * Haalt een publieke .ics-feed op (iCloud-gedeelde agenda of Herinneringen-lijst)
 * zonder inloggegevens — precies de link die je krijgt bij "Openbare agenda/lijst" delen.
 */
async function fetchIcsEvents(icsUrl) {
  if (!icsUrl) return [];
  const data = await ical.async.fromURL(icsUrl);
  return Object.values(data);
}

async function fetchUpcomingEvents(icsUrl, { daysAhead = 14, limit = 20 } = {}) {
  const items = await fetchIcsEvents(icsUrl);
  const now = new Date();
  const horizon = new Date(now.getTime() + daysAhead * 24 * 60 * 60 * 1000);

  return items
    .filter((item) => item.type === "VEVENT" && item.start)
    .filter((item) => item.start >= now && item.start <= horizon)
    .sort((a, b) => a.start - b.start)
    .slice(0, limit)
    .map((item) => ({
      id: item.uid,
      title: item.summary ?? "(geen titel)",
      start: item.start.toISOString(),
      end: item.end ? item.end.toISOString() : null,
      location: item.location ?? null,
    }));
}

async function fetchOpenReminders(icsUrl, { limit = 30 } = {}) {
  const items = await fetchIcsEvents(icsUrl);

  return items
    .filter((item) => item.type === "VTODO" && item.status !== "COMPLETED")
    .sort((a, b) => {
      if (!a.due) return 1;
      if (!b.due) return -1;
      return a.due - b.due;
    })
    .slice(0, limit)
    .map((item) => ({
      id: item.uid,
      title: item.summary ?? "(geen titel)",
      deadline: item.due ? new Date(item.due).toISOString() : null,
      list: item.categories?.[0] ?? null,
    }));
}

module.exports = { fetchUpcomingEvents, fetchOpenReminders };
