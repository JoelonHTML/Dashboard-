const express = require("express");
const { exec } = require("child_process");

/**
 * Deze routes voeren systeemcommando's uit op de laptop (herstarten, processen
 * killen) — altijd achter een gedeeld geheim, ook al is dit alleen LAN-bereikbaar.
 * Zonder ACTIONS_SECRET in .env staan alle routes hieronder uit.
 */
function requireActionsSecret(req, res, next) {
  const configured = process.env.ACTIONS_SECRET;
  if (!configured) {
    return res.status(503).json({ error: "ACTIONS_SECRET is niet ingesteld in .env — acties staan uit" });
  }
  if (req.get("X-Actions-Secret") !== configured) {
    return res.status(401).json({ error: "Ontbrekende of onjuiste X-Actions-Secret header" });
  }
  next();
}

function createActionsRouter() {
  const router = express.Router();
  router.use(requireActionsSecret);

  router.post("/restart-laptop", (req, res) => {
    res.json({ status: "ok", message: "Laptop herstart over 10 seconden" });
    exec('shutdown /r /t 10 /c "Dashboard: herstart aangevraagd via Stream Deck"', (err) => {
      if (err) console.error("restart-laptop mislukt:", err.message);
    });
  });

  // Geen taskkill/relaunch hier: de backend draait als service (sessie 0) en kan geen
  // GUI-app in de ingelogde sessie tonen — dat blijft de geregistreerde Taakplanner-taak
  // (zie setup-streamdeck-tasks.ps1) die wél in de juiste sessie draait.
  router.post("/restart-fs25", (req, res) => {
    exec('schtasks /run /tn "RestartFs25Server"', (err) => {
      if (err) {
        res.status(500).json({ error: `Kon RestartFs25Server-taak niet starten: ${err.message}` });
      } else {
        res.json({ status: "ok", message: "FS25-server wordt herstart" });
      }
    });
  });

  // NSSM herstart de service automatisch na een process-exit (AppRestartDelay,
  // zie install-windows-service.ps1) — geen los herstart-commando nodig.
  router.post("/restart-backend", (req, res) => {
    res.json({ status: "ok", message: "Backend herstart nu (NSSM start 'm automatisch weer op)" });
    setTimeout(() => process.exit(0), 300);
  });

  return router;
}

module.exports = { createActionsRouter };
