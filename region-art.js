"use strict";

// Nature atlas source rectangles. Opaque terrain cells are inset to avoid atlas dividers.
const regionFrames = [
  [
    [4, 4, 305, 305],
    [317, 4, 306, 305],
    [631, 4, 305, 305],
    [944, 4, 306, 305],
  ],
  [
    [33, 315, 278, 310],
    [315, 315, 310, 310],
    [629, 315, 309, 305],
    [942, 315, 310, 293],
  ],
  [
    [4, 631, 305, 305],
    [317, 631, 306, 305],
    [631, 631, 305, 305],
    [944, 631, 306, 305],
  ],
  [
    [23, 951, 279, 296],
    [361, 964, 213, 278],
    [639, 1037, 284, 172],
    [944, 944, 306, 306],
  ],
];
function regionSprite(col, row, x, y, w, h) {
  const [sx, sy, sw, sh] = regionFrames[row][col];
  ctx.drawImage(
    art.regions,
    sx,
    sy,
    sw,
    sh,
    Math.round(x),
    Math.round(y),
    w,
    h,
  );
}
function drawNatureTree(t) {
  if (t.x < camera.x - 70 || t.x > camera.x + W + 70) return;
  const [sx, sy, sw, sh] = regionFrames[1][t.variant],
    height = t.variant === 0 ? 145 : 125,
    width = t.variant === 0 ? 94 : 115,
    w = windState(),
    sway = Math.round(Math.sin(time * 1.5 + t.x * 0.017) * w.strength * 2.5);
  for (let i = 0; i < 4; i++)
    ctx.drawImage(
      art.regions,
      sx,
      sy + (i * sh) / 4,
      sw,
      sh / 4,
      Math.round(t.x - width / 2 + sway * (1 - i / 3)),
      Math.round(t.y - height + (i * height) / 4),
      width,
      height / 4,
    );
}
// Sample one patch across several ground cells, rather than shrinking a whole
// detailed forest tile into every 32-pixel cell (which makes the ground noisy).
function forestGround(col, x, y) {
  const [sx, sy, sw, sh] = regionFrames[0][col],
    size = 96;
  ctx.drawImage(
    art.regions,
    sx + ((x % size) / size) * sw,
    sy + ((y % size) / size) * sh,
    sw / 3,
    sh / 3,
    x,
    y,
    32,
    32,
  );
}
function bakeAreaTerrain(id) {
  const data = AREA_DATA[id],
    state = areaState(id),
    main = ctx;
  state.terrain = document.createElement("canvas");
  state.terrain.width = 1600;
  state.terrain.height = 1216;
  ctx = state.terrain.getContext("2d");
  ctx.imageSmoothingEnabled = false;
  for (let y = 0; y < 1216; y += 32)
    for (let x = 0; x < 1600; x += 32) {
      const trail = distanceToTrail(x + 16, y + 16, data.paths) < 25;
      if (id === "woods") forestGround(trail ? 1 : 0, x, y);
      else if (id === "highlands") {
        sprite(
          "grass",
          Math.floor(grain(x, y) * 2),
          Math.floor(grain(y, x) * 2),
          x,
          y,
          32,
          32,
        );
        if (trail) regionSprite(3, 0, x, y, 32, 32);
      } else {
        sprite(
          "grass",
          Math.floor(grain(x, y) * 2),
          Math.floor(grain(y, x) * 2),
          x,
          y,
          32,
          32,
        );
        if (trail) sprite("terrain", 0, 1, x, y, 32, 32);
      }
    }
  if (id === "highlands")
    for (const r of data.ridges)
      for (let x = r.x; x < r.x + r.w; x += 32) {
        if (Math.abs(x + 16 - r.gapX) < r.gapWidth / 2) continue;
        regionSprite(3, 3, x, r.y, 32, r.h);
      }
  if (id === "highlands")
    for (const r of data.ridges)
      for (let y = r.y; y < r.y + r.h; y += 8)
        regionSprite(3, 0, r.gapX - r.gapWidth / 2 + 7, y, r.gapWidth - 14, 7);
  ctx = main;
}
function drawAreaTerrain() {
  const data = AREA_DATA[currentArea],
    state = areaState();
  if (!state.terrain) bakeAreaTerrain(currentArea);
  ctx.drawImage(state.terrain, 0, 0);
  if (currentArea === "lake") drawLake();
  if (currentArea === "woods") {
    ctx.fillStyle = "#153e2412";
    ctx.fillRect(camera.x, camera.y, W, H);
  }
}
function drawLake() {
  const data = AREA_DATA.lake,
    w = data.water,
    p = data.pier;
  ctx.save();
  ctx.beginPath();
  ctx.ellipse(w.x, w.y, w.rx + 6, w.ry + 6, 0, 0, Math.PI * 2);
  ctx.fillStyle = "#b7ad80";
  ctx.fill();
  ctx.beginPath();
  ctx.ellipse(w.x, w.y, w.rx, w.ry, 0, 0, Math.PI * 2);
  ctx.clip();
  const phase = Math.floor(time * 2) % 4,
    [sx, sy, sw, sh] = regionFrames[2][phase],
    drift = (time * 3) % 48;
  for (let y = w.y - w.ry - 48; y < w.y + w.ry + 48; y += 48)
    for (let x = w.x - w.rx - 48; x < w.x + w.rx + 48; x += 48)
      ctx.drawImage(art.regions, sx, sy, sw, sh, x + drift, y, 48, 48);
  ctx.restore();
  // Broken shore ripples and reeds soften the boundary without changing collision.
  for (let i = 0; i < 42; i++) {
    const a = (i / 42) * Math.PI * 2,
      x = w.x + Math.cos(a) * (w.rx - 5),
      y = w.y + Math.sin(a) * (w.ry - 5);
    rect(x, y, 5 + Math.sin(time + i) * 2, 1, "#d5e7cd70");
    if (i % 3 === 0)
      regionSprite(
        2,
        1,
        w.x + Math.cos(a) * (w.rx + 17) - 15,
        w.y + Math.sin(a) * (w.ry + 17) - 32,
        30,
        36,
      );
  }
  for (let y = p.y; y < p.y + p.h; y += 26)
    sprite("village", 0, 3, p.x, y, p.w, 28);
}
