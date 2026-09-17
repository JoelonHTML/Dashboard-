const si = require("systeminformation");

async function getSystemStats() {
  const [cpu, mem, fsSize, networkStats, time] = await Promise.all([
    si.currentLoad(),
    si.mem(),
    si.fsSize(),
    si.networkStats(),
    Promise.resolve(si.time()),
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
    uptimeSeconds: time.uptime,
    updatedAt: new Date().toISOString(),
  };
}

module.exports = { getSystemStats };
