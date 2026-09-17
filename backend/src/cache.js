const stores = new Map();

/**
 * Simpele in-memory cache met TTL, zodat elk endpoint zijn bron
 * (FS25, iCloud, systeem) niet bij elke tablet-poll opnieuw bevraagt.
 */
async function getCached(key, ttlSeconds, fetcher) {
  const now = Date.now();
  const entry = stores.get(key);

  if (entry && now - entry.fetchedAt < ttlSeconds * 1000) {
    return entry.data;
  }

  const data = await fetcher();
  stores.set(key, { data, fetchedAt: now });
  return data;
}

module.exports = { getCached };
