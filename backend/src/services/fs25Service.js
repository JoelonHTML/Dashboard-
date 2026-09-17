const { parseStringPromise } = require("xml2js");

/**
 * Gebaseerd op het standaard FS-dedicated-server stats.xml-schema
 * (Server > Slots > Player, met numUsed/numMaxUsers en mapName als attributen).
 * Veldnamen kunnen per FS25-versie licht afwijken — pas hier aan zodra je
 * eigen server-response afwijkt van de aannames hieronder.
 */
async function fetchFs25Stats(statsUrl) {
  if (!statsUrl) {
    return { online: false, error: "FS25_STATS_URL is niet ingesteld", updatedAt: new Date().toISOString() };
  }

  let xml;
  try {
    const response = await fetch(statsUrl, { signal: AbortSignal.timeout(5000) });
    if (!response.ok) {
      throw new Error(`FS25-server antwoordde met status ${response.status}`);
    }
    xml = await response.text();
  } catch (err) {
    return { online: false, error: err.message, updatedAt: new Date().toISOString() };
  }

  try {
    const parsed = await parseStringPromise(xml, { explicitArray: false, mergeAttrs: true });
    const server = parsed.Server ?? {};

    const rawPlayers = server.Slots?.Player ?? [];
    const playerList = Array.isArray(rawPlayers) ? rawPlayers : [rawPlayers];

    const players = playerList
      .filter((p) => p && (p.isUsed === "true" || p.isUsed === true))
      .map((p) => ({
        name: p.name ?? "Onbekend",
        isAdmin: p.isAdmin === "true" || p.isAdmin === true,
        uptimeMinutes: p.uptime ? Math.round(Number(p.uptime) / 60000) : null,
      }));

    return {
      online: true,
      serverName: server.name ?? null,
      mapName: server.mapName ?? null,
      playerCount: Number(server.Slots?.numUsed ?? players.length),
      maxPlayers: Number(server.Slots?.capacity ?? 0),
      players,
      dayTime: {
        day: server.onlineDay != null ? Number(server.onlineDay) : null,
        minute: server.onlineMinute != null ? Number(server.onlineMinute) : null,
      },
      version: server.version ?? null,
      updatedAt: new Date().toISOString(),
    };
  } catch (err) {
    return { online: false, error: `Kon FS25-stats niet parsen: ${err.message}`, updatedAt: new Date().toISOString() };
  }
}

module.exports = { fetchFs25Stats };
