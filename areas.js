"use strict";

// Each area has its own cached terrain, collision and scenery; town stays unchanged.
let currentArea = "town";
const areaStates = new Map();
let areasReady = false;
const mapDialog = document.querySelector("#world-map");
function distanceToTrail(x, y, paths) {
  let best = Infinity;
  for (const points of paths)
    for (let i = 1; i < points.length; i++) {
      const [ax, ay] = points[i - 1],
        [bx, by] = points[i],
        vx = bx - ax,
        vy = by - ay,
        t = Math.max(
          0,
          Math.min(1, ((x - ax) * vx + (y - ay) * vy) / (vx * vx + vy * vy)),
        );
      best = Math.min(best, Math.hypot(x - ax - t * vx, y - ay - t * vy));
    }
  return best;
}
function lakeWater(x, y) {
  const a = AREA_DATA.lake.water;
  return ((x - a.x) / a.rx) ** 2 + ((y - a.y) / a.ry) ** 2 < 1;
}
function onPier(x, y) {
  const p = AREA_DATA.lake.pier;
  return x > p.x + 6 && x < p.x + p.w - 6 && y > p.y + 10 && y < p.y + p.h;
}
function regionGroundBlocked(id, x, y) {
  if (x < 28 || y < 28 || x > 1572 || y > 1188) return true;
  if (id === "lake" && lakeWater(x, y) && !onPier(x, y)) return true;
  if (id === "highlands")
    for (const r of AREA_DATA.highlands.ridges) {
      if (
        x > r.x &&
        x < r.x + r.w &&
        y > r.y - 3 &&
        y < r.y + r.h + 3 &&
        Math.abs(x - r.gapX) > r.gapWidth / 2 - 5
      )
        return true;
    }
  return false;
}
function areaState(id = currentArea) {
  if (areaStates.has(id)) return areaStates.get(id);
  const data = AREA_DATA[id],
    state = { trees: [], rocks: [], props: [], terrain: null };
  for (let y = 90; y < 1110; y += data.treeSpacing)
    for (let x = 75; x < 1530; x += data.treeSpacing) {
      const xx = x + (grain(x, y, data.seed) - 0.5) * 30,
        yy = y + (grain(y, x, data.seed) - 0.5) * 28;
      if (
        distanceToTrail(xx, yy, data.paths) < 62 ||
        data.clearings.some((c) => Math.hypot(xx - c.x, yy - c.y) < c.r) ||
        regionGroundBlocked(id, xx, yy)
      )
        continue;
      if (id === "lake") {
        const w = data.water;
        if (
          ((xx - w.x) / (w.rx + 65)) ** 2 + ((yy - w.y) / (w.ry + 65)) ** 2 <
          1.16
        )
          continue;
      }
      state.trees.push({
        x: xx,
        y: yy,
        nature: true,
        variant: id === "woods" ? (grain(x, y) > 0.55 ? 0 : 1) : 0,
      });
    }
  for (let i = 0; i < (id === "highlands" ? 45 : 18); i++) {
    const x = 65 + grain(i, 17, data.seed) * 1470,
      y = 90 + grain(i, 63, data.seed) * 1000;
    if (
      distanceToTrail(x, y, data.paths) < 60 ||
      regionGroundBlocked(id, x, y) ||
      data.clearings.some((c) => Math.hypot(x - c.x, y - c.y) < c.r)
    )
      continue;
    state.rocks.push({ x, y });
  }
  if (id === "woods")
    state.props.push(
      { col: 0, row: 3, x: 963, y: 229, w: 70, h: 83 },
      { col: 2, row: 3, x: 271, y: 509, w: 58, h: 35 },
    );
  if (id === "lake")
    state.props.push({ col: 2, row: 3, x: 1293, y: 570, w: 58, h: 35 });
  if (id === "highlands")
    state.props.push({ col: 1, row: 3, x: 1215, y: 140, w: 36, h: 48 });
  areaStates.set(id, state);
  return state;
}
function areaBlocked(x, y) {
  if (regionGroundBlocked(currentArea, x, y)) return true;
  const state = areaState();
  return (
    state.trees.some((t) => Math.abs(x - t.x) < 13 && Math.abs(y - t.y) < 10) ||
    state.rocks.some((r) => Math.abs(x - r.x) < 13 && Math.abs(y - r.y) < 9)
  );
}
function enterArea(id, arrival) {
  if (!areasReady || (id !== "town" && !AREA_DATA[id])) return false;
  const old = currentArea;
  currentArea = id;
  const destination =
    arrival ??
    (id === "town"
      ? { x: GAME_SETTINGS.player.startX, y: GAME_SETTINGS.player.startY }
      : AREA_DATA[id].spawn);
  if (blocked(destination.x, destination.y)) {
    currentArea = old;
    msg(
      "That trail entrance is blocked. Check its coordinates in area-data.js.",
    );
    return false;
  }
  Object.assign(player, {
    x: destination.x,
    y: destination.y,
    dir: 2,
    walk: 0,
    sideDistance: 0,
    moving: false,
    dashing: false,
    charge: 0,
    dashBlocked: false,
  });
  keys.clear();
  battle.swing = 0;
  battle.cooldown = 0;
  battle.invulnerable = 1;
  dust.length = 0;
  sparks.length = 0;
  snapCamera();
  if (mapDialog.open) mapDialog.close();
  syncBattleHud();
  msg(
    id === "town"
      ? "Back in Willowmere."
      : `${AREA_DATA[id].name} · Follow the trails. M opens the world map.`,
  );
  return true;
}
function currentInteractions() {
  return currentArea === "town" ? objects : AREA_DATA[currentArea].interactions;
}
function nearbyRoute() {
  return AREA_ROUTES.find(
    (r) =>
      r.from === currentArea && Math.hypot(r.x - player.x, r.y - player.y) < 58,
  );
}
function handleAreaInteract() {
  const route = nearbyRoute();
  if (!route) return false;
  enterArea(route.to, route.arrival);
  return true;
}
function drawRoutes() {
  for (const r of AREA_ROUTES) {
    if (r.from !== currentArea) continue;
    regionSprite(1, 3, r.x - 27, r.y - 47, 30, 47);
  }
}
function drawRegionProp(o) {
  regionSprite(o.col, o.row, o.x, o.y, o.w, o.h);
}
function sceneObjects(dark) {
  const markers = AREA_ROUTES.filter((r) => r.from === currentArea).map(
    (r) => ({
      y: r.y,
      fn: () => regionSprite(1, 3, r.x - 27, r.y - 47, 30, 47),
    }),
  );
  if (currentArea !== "town") {
    const state = areaState();
    return state.trees
      .map((t) => ({
        y: t.y,
        x: t.x,
        width: 125,
        height: 150,
        occludes: true,
        fn: () => drawNatureTree(t),
      }))
      .concat(
        state.rocks.map((r) => ({
          y: r.y,
          fn: () => regionSprite(3, 1, r.x - 22, r.y - 32, 44, 38),
        })),
        state.props.map((o) => ({ y: o.y + o.h, fn: () => drawRegionProp(o) })),
        markers,
        [{ y: player.y, fn: drawBattlePlayer }],
      )
      .sort((a, b) => a.y - b.y);
  }
  return trees
    .map((t) => ({
      y: t.y,
      x: t.x,
      width: 130,
      height: 155,
      occludes: true,
      fn: () => drawTree(t),
    }))
    .concat(
      villageSceneObjects(),
      fields.map((f) => ({
        y: 903,
        fn: () => sprite("village", 2, 2, f.x + f.w / 2 - 21, 865, 42, 38),
      })),
      houses.map((h) => ({
        y: h.y + h.h,
        x: h.x + h.w / 2,
        width: h.w + 64,
        height: VIEW.buildingHeight,
        occludes: true,
        fn: () => drawHouse(h, dark),
      })),
      townScenery.map((o) => ({ y: o.y + o.h, fn: () => drawTownObject(o) })),
      enemies
        .filter((e) => e.active)
        .map((e) => ({ y: e.y, fn: () => drawEnemy(e) })),
      markers,
      [{ y: player.y, fn: drawBattlePlayer }],
    )
    .sort((a, b) => a.y - b.y);
}
// Guard the editable map data, especially spacing values used in placement loops.
function validateAreas() {
  const point = (p) =>
    p &&
    Number.isFinite(p.x) &&
    Number.isFinite(p.y) &&
    p.x >= 28 &&
    p.x <= 1572 &&
    p.y >= 28 &&
    p.y <= 1188;
  for (const id of ["woods", "lake", "highlands"]) {
    const data = AREA_DATA[id];
    if (!data || typeof data.name !== "string" || !point(data.spawn))
      throw new Error(
        `Check area-data.js: ${id} needs a name and an in-bounds spawn point.`,
      );
    if (
      !Number.isFinite(data.treeSpacing) ||
      data.treeSpacing < 50 ||
      data.treeSpacing > 300
    )
      throw new Error(
        `Check area-data.js: ${id}.treeSpacing must be from 50 to 300.`,
      );
    if (!Array.isArray(data.paths) || !data.paths.length)
      throw new Error(`Check area-data.js: ${id} needs a path.`);
    for (const path of data.paths) {
      if (!Array.isArray(path) || path.length < 2)
        throw new Error(
          `Check area-data.js: each ${id} path needs two or more points.`,
        );
      for (let i = 0; i < path.length; i++) {
        const p = path[i];
        if (
          !Array.isArray(p) ||
          !point({ x: p[0], y: p[1] }) ||
          (i && p[0] === path[i - 1][0] && p[1] === path[i - 1][1])
        )
          throw new Error(
            `Check area-data.js: ${id} path points must be in bounds and adjacent points must differ.`,
          );
      }
    }
  }
}
function initAreas() {
  validateAreas();
  areasReady = true;
  for (const [id, title, description] of [
    ["town", GAME_SETTINGS.text.title, "Bell square & the two enemy fields"],
    ...Object.entries(AREA_DATA).map(([id, data]) => [
      id,
      data.name,
      data.subtitle,
    ]),
  ]) {
    const button = document.createElement("button");
    button.type = "button";
    const strong = document.createElement("strong"),
      small = document.createElement("small");
    strong.textContent = title;
    small.textContent = description;
    button.append(strong, small);
    button.addEventListener("click", () => enterArea(id));
    document.querySelector("#area-choices").append(button);
  }
}
function toggleWorldMap() {
  if (!areasReady) return;
  keys.clear();
  if (mapDialog.open) mapDialog.close();
  else mapDialog.showModal();
}
document.querySelector("#map-button").addEventListener("click", toggleWorldMap);
document
  .querySelector("#close-map")
  .addEventListener("click", () => mapDialog.close());
mapDialog.addEventListener("close", () => {
  if (typeof keys !== "undefined") keys.clear();
});
window.addEventListener("keydown", (e) => {
  if (e.key.toLowerCase() === "m" && !e.repeat) {
    e.preventDefault();
    toggleWorldMap();
  }
});
