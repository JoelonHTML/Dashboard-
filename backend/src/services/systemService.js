const si = require("systeminformation");

async function getSystemStats() {
  const [cpu, mem, fsSize, networkStats, time, battery, cpuTemp] = await Promise.all([
    si.currentLoad(),
    si.mem(),
    si.fsSize(),
    si.networkStats(),
    Promise.resolve(si.time()),
    si.battery().catch(() => null),
    si.cpuTemperature().catch(() => null),
  ]);

  const primaryDisk = fsSize[0] ?? null;
  const primaryNetwork = networkStats[0] ?? null;

  return {
    cpu: {
      loadPercent: Math.round(cpu.currentLoad * 10) / 10,
    },
    memory: {
      usedPercent: Math.round((mem.active / mem.total) * 1000) / 10,
      totalBytes: mem.total,
      usedBytes: mem.active,
    },
    disk: primaryDisk
      ? {
          usedPercent: Math.round(primaryDisk.use * 10) / 10,
          totalBytes: primaryDisk.size,
          usedBytes: primaryDisk.used,
        }
      : null,
    network: primaryNetwork
      ? {
          rxSec: Math.round(primaryNetwork.rx_sec ?? 0),
          txSec: Math.round(primaryNetwork.tx_sec ?? 0),
        }
      : null,
    // Laptop-batterij: alleen relevant/aanwezig als de laptop 'm daadwerkelijk heeft.
    battery: battery && battery.hasBattery
      ? {
          percent: Math.round(battery.percent),
          isCharging: Boolean(battery.isCharging),
        }
      : null,
    // Niet elke laptop/OS geeft een bruikbare temperatuursensor terug (main = -1 dan).
    cpuTempCelsius: cpuTemp && cpuTemp.main > 0 ? Math.round(cpuTemp.main * 10) / 10 : null,
    uptimeSeconds: time.uptime,
    updatedAt: new Date().toISOString(),
  };
}

module.exports = { getSystemStats };
