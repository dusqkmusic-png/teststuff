"use strict";

// The same map geometry, with new painted terrain and dimensional sprites.
function regionSprite(col, row, x, y, w, h) {
  sprite("regions", col, row, x, y, w, h);
}
function drawNatureTree(t) {
  if (t.x < camera.x - 90 || t.x > camera.x + W + 90) return;
  const height = t.variant === 0 ? 151 : 140,
    width = t.variant === 0 ? 112 : 135;
  ctx.save();
  ctx.translate(t.x, t.y + 3);
  ctx.transform(
    1,
    0,
    Math.sin(time * 1.4 + t.x * 0.017) * windState().strength * 0.015,
    1,
    0,
    0,
  );
  regionSprite(t.variant, 1, -width / 2, -height, width, height);
  ctx.restore();
}
function bakeAreaTerrain(id) {
  areaState(id).terrain = paintedGround(id);
}
function drawAreaTerrain() {
  const state = areaState();
  if (!state.terrain) bakeAreaTerrain(currentArea);
  ctx.drawImage(state.terrain, 0, 0);
  if (currentArea === "lake") drawLake();
}
function drawLake() {
  const w = AREA_DATA.lake.water,
    p = AREA_DATA.lake.pier;
  ctx.save();
  ctx.beginPath();
  ctx.ellipse(w.x, w.y, w.rx + 7, w.ry + 7, 0, 0, Math.PI * 2);
  ctx.fillStyle = "#dbc991";
  ctx.fill();
  ctx.beginPath();
  ctx.ellipse(w.x, w.y, w.rx, w.ry, 0, 0, Math.PI * 2);
  ctx.clip();
  fillPaintedWater(
    { x: w.x - w.rx, y: w.y - w.ry, w: w.rx * 2, h: w.ry * 2 },
    2.2,
  );
  ctx.restore();
  for (let i = 0; i < 26; i++) {
    const a = (i / 26) * Math.PI * 2,
      x = w.x + Math.cos(a) * (w.rx - 5),
      y = w.y + Math.sin(a) * (w.ry - 5);
    ctx.strokeStyle = "#e1ebcd65";
    ctx.lineWidth = 1.2;
    ctx.beginPath();
    ctx.ellipse(x, y, 6 + Math.sin(time + i) * 2, 1.6, 0, 0, Math.PI);
    ctx.stroke();
    if (i % 3 === 0)
      regionSprite(
        2,
        1,
        w.x + Math.cos(a) * (w.rx + 17) - 20,
        w.y + Math.sin(a) * (w.ry + 17) - 37,
        40,
        42,
      );
  }
  sprite("village", 0, 3, p.x, p.y, p.w, p.h);
}
