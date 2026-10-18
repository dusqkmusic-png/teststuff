"use strict";

// DUSQK'S SETTINGS
// Change a number or quoted sentence, save this file, then refresh the game.
// Distances and speeds use game pixels. Times use seconds unless labeled otherwise.
// Keep commas between entries. Start with EDITING.md for examples and safe ranges.
const GAME_SETTINGS = {
  player: {
    walkSpeed: 112, // Higher = faster walking. Try 140.
    runSpeed: 280, // Speed after charging a dash.
    dashChargeSeconds: 0.32, // Lower = dash starts sooner.
    maxHealth: 5, // Number of hearts.
    startX: 768, // Safe starting / recovery point in the village.
    startY: 660,
    damageProtectionSeconds: 1.15,
    walkFramesPerSecond: 10,
    runFramesPerSecond: 20,
  },

  sword: {
    damage: 1, // Slimes normally have 2 health; set to 2 for one-hit kills.
    reach: 58,
    swingSeconds: 0.48,
    cooldownSeconds: 0.56, // Must be at least as long as swingSeconds.
    hitStartsAt: 0.25, // Fraction of the swing: 0 = start, 1 = finish.
    hitEndsAt: 0.75,
  },

  enemies: {
    enabled: true, // Set to false for a peaceful world with no enemies.
    perField: 4, // Try 2 for a quieter field.
    health: 2,
    chaseSpeed: 38,
    roamSpeed: 15,
    noticeDistance: 230,
    respawnSeconds: 9,
    contactDamage: 1,
  },

  world: {
    seed: 71, // Same seed = same scenery positions after each reload.
    treeAttempts: 330, // Placement tries; blocked locations are skipped.
    rockCount: 65,
    flowerCount: 520,
    flowerSpacing: 56, // Draw every 56th flower position. Higher = fewer flowers.
  },

  lighting: {
    maxNightDarkness: 0.52, // 0 = no night shading, 1 = almost black. Try 0.40 or 0.60.
    nightColor: "#081236",
    dawnStartsAt: 5, // Local hours on a 24-hour clock.
    dawnEndsAt: 7,
    duskStartsAt: 18,
    duskEndsAt: 21,
  },

  clock: {
    timeZone: "America/New_York", // Handles EST and EDT automatically.
    locationLabel: "New York",
  },

  weather: {
    enabled: true, // false = labeled ambient weather, without network requests.
    latitude: 40.7654,
    longitude: -73.8174,
    locationLabel: "Flushing",
    panelLabel: "Flushing, Queens",
    refreshMinutes: 10,
    fallbackWindMph: 4,
  },

  text: {
    title: "Willowmere",
    subtitle: "A LITTLE WORLD, ON YOUR TIME",
    headline: "Somewhere to simply be.",
    description: "A quiet village. A winding river. A sky that follows yours.",
    welcome:
      "Village is safe. Explore the fields west or east · J / Z swings your sword.",
    recovery: "Back at the village. Your health is restored.",
    healed: " Your health is restored.",
    idleInteraction: "The breeze brushes past. There’s nowhere you need to be.",
    closedDoor: "A warm light, a quiet home. The door is closed for now.",
    dashBlocked: "Oof! Release dash to catch your breath.",
    messageSeconds: 6.5,
  },
};

// The engine checks these values before loading. You normally do not edit this function.
function validateSettings() {
  const errors = [];
  function number(section, key, min, max, integer = false) {
    const value = GAME_SETTINGS[section][key];
    if (
      !Number.isFinite(value) ||
      value < min ||
      value > max ||
      (integer && !Number.isInteger(value))
    ) {
      errors.push(
        `${section}.${key} must be ${integer ? "a whole number" : "a number"} from ${min} to ${max}.`,
      );
    }
  }
  const checks = [
    ["player", "walkSpeed", 1, 500],
    ["player", "runSpeed", 1, 800],
    ["player", "dashChargeSeconds", 0.05, 3],
    ["player", "maxHealth", 1, 20, true],
    ["player", "startX", 24, 1576],
    ["player", "startY", 24, 1192],
    ["player", "damageProtectionSeconds", 0.1, 10],
    ["player", "walkFramesPerSecond", 1, 40],
    ["player", "runFramesPerSecond", 1, 60],
    ["sword", "damage", 1, 100, true],
    ["sword", "reach", 15, 150],
    ["sword", "swingSeconds", 0.15, 3],
    ["sword", "cooldownSeconds", 0.15, 5],
    ["sword", "hitStartsAt", 0, 1],
    ["sword", "hitEndsAt", 0, 1],
    ["enemies", "perField", 0, 20, true],
    ["enemies", "health", 1, 100, true],
    ["enemies", "chaseSpeed", 0, 300],
    ["enemies", "roamSpeed", 0, 150],
    ["enemies", "noticeDistance", 20, 600],
    ["enemies", "respawnSeconds", 1, 300],
    ["enemies", "contactDamage", 1, 20, true],
    ["world", "seed", 0, 4294967295, true],
    ["world", "treeAttempts", 0, 1000, true],
    ["world", "rockCount", 0, 300, true],
    ["world", "flowerCount", 0, 2000, true],
    ["world", "flowerSpacing", 1, 2000, true],
    ["lighting", "maxNightDarkness", 0, 0.9],
    ["lighting", "dawnStartsAt", 0, 24],
    ["lighting", "dawnEndsAt", 0, 24],
    ["lighting", "duskStartsAt", 0, 24],
    ["lighting", "duskEndsAt", 0, 24],
    ["weather", "latitude", -90, 90],
    ["weather", "longitude", -180, 180],
    ["weather", "refreshMinutes", 1, 120],
    ["weather", "fallbackWindMph", 0, 75],
    ["text", "messageSeconds", 1, 30],
  ];
  for (const check of checks) number(...check);
  for (const key of ["enemies", "weather"]) {
    if (typeof GAME_SETTINGS[key].enabled !== "boolean")
      errors.push(`${key}.enabled must be true or false (without quotes).`);
  }
  if (GAME_SETTINGS.sword.cooldownSeconds < GAME_SETTINGS.sword.swingSeconds)
    errors.push("sword.cooldownSeconds must be at least sword.swingSeconds.");
  if (GAME_SETTINGS.sword.hitStartsAt >= GAME_SETTINGS.sword.hitEndsAt)
    errors.push("sword.hitStartsAt must be less than sword.hitEndsAt.");
  const light = GAME_SETTINGS.lighting;
  if (
    !(
      light.dawnStartsAt < light.dawnEndsAt &&
      light.dawnEndsAt < light.duskStartsAt &&
      light.duskStartsAt < light.duskEndsAt
    )
  )
    errors.push(
      "Lighting hours must go in this order: dawn start, dawn end, dusk start, dusk end.",
    );
  if (!/^#[0-9a-f]{6}$/i.test(light.nightColor))
    errors.push(
      "lighting.nightColor must be a six-digit color such as #081236.",
    );
  try {
    new Intl.DateTimeFormat("en-US", {
      timeZone: GAME_SETTINGS.clock.timeZone,
    });
  } catch {
    errors.push(
      "clock.timeZone must be a valid timezone, such as America/New_York.",
    );
  }
  for (const [key, value] of Object.entries(GAME_SETTINGS.text)) {
    if (key !== "messageSeconds" && typeof value !== "string")
      errors.push(`text.${key} must be text inside quotes.`);
  }
  if (errors.length)
    throw new Error("Check settings.js:\n" + errors.join("\n"));
}
