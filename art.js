"use strict";

// Storybook renderer. Gameplay and collision still live in game.js / combat.js.
// Every visible object comes from the new local painted atlases.
const art = {};
function loadArt() {
  return Promise.all(
    Object.entries(ART_FILES).map(
      ([name, path]) =>
        new Promise((resolve, reject) => {
          const image = new Image();
          image.onload = () => {
            art[name] = image;
            resolve();
          };
          image.onerror = () =>
            reject(
              new Error(
                `Could not load ${path}. Keep the assets folder with index.html.`,
              ),
            );
          image.src = path;
        }),
    ),
  );
}
function grain(x, y, s = 0) {
  let n =
    Math.imul(x + 173 + s * 43, 374761393) ^ Math.imul(y + 937, 668265263);
  n = Math.imul(n ^ (n >>> 13), 1274126177);
  return ((n ^ (n >>> 16)) >>> 0) / 4294967295;
}
function sprite(sheet, col, row, x, y, w, h) {
  const [sx, sy, sw, sh] = PAINTED_RECTS[sheet][row][col];
  ctx.drawImage(art[sheet], sx, sy, sw, sh, x, y, w, h);
}
function windState() {
  const mph =
    weather.live && Number.isFinite(weather.wind)
      ? Math.max(0, Math.min(75, weather.wind))
      : GAME_SETTINGS.weather.fallbackWindMph;
  return {
    mph,
    strength:
      Math.min(1.8, mph / 12) *
      (0.72 + 0.18 * Math.sin(time * 0.47) + 0.1 * Math.sin(time * 1.13)),
  };
}
const textureCache = new Map();
function paintedPattern(sheet, col, row, size = 384) {
  const key = [sheet, col, row, size].join(":");
  if (textureCache.has(key)) return textureCache.get(key);
  const [sx, sy, sw, sh] = PAINTED_RECTS[sheet][row][col],
    tile = document.createElement("canvas");
  tile.width = tile.height = size * 2;
  const c = tile.getContext("2d");
  c.imageSmoothingEnabled = true;
  // Mirrored repeats meet at their own edges instead of showing a tile-grid seam.
  for (let y = 0; y < 2; y++)
    for (let x = 0; x < 2; x++) {
      c.save();
      c.translate(x ? size * 2 : 0, y ? size * 2 : 0);
      c.scale(x ? -1 : 1, y ? -1 : 1);
      c.drawImage(art[sheet], sx, sy, sw, sh, 0, 0, size, size);
      c.restore();
    }
  const pattern = ctx.createPattern(tile, "repeat");
  textureCache.set(key, pattern);
  return pattern;
}
function softShadow(x, y, width, height, opacity = 0.2) {
  ctx.save();
  ctx.translate(x, y);
  ctx.scale(width, height);
  const g = ctx.createRadialGradient(0, 0, 0.08, 0, 0, 1);
  g.addColorStop(0, `rgba(26,48,31,${opacity})`);
  g.addColorStop(0.55, `rgba(26,48,31,${opacity * 0.65})`);
  g.addColorStop(1, "rgba(26,48,31,0)");
  ctx.fillStyle = g;
  ctx.beginPath();
  ctx.arc(0, 0, 1, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();
}
function smoothTrail(c, points, width) {
  c.beginPath();
  c.moveTo(...points[0]);
  for (let i = 1; i < points.length - 1; i++) {
    const before = points[i - 1],
      p = points[i],
      after = points[i + 1],
      inLen = Math.hypot(p[0] - before[0], p[1] - before[1]),
      outLen = Math.hypot(after[0] - p[0], after[1] - p[1]),
      round = Math.min(24, inLen / 3, outLen / 3),
      a = [
        p[0] - ((p[0] - before[0]) / inLen) * round,
        p[1] - ((p[1] - before[1]) / inLen) * round,
      ],
      b = [
        p[0] + ((after[0] - p[0]) / outLen) * round,
        p[1] + ((after[1] - p[1]) / outLen) * round,
      ];
    c.lineTo(...a);
    c.quadraticCurveTo(...p, ...b);
  }
  c.lineTo(...points.at(-1));
  c.lineWidth = width;
  c.lineCap = "round";
  c.lineJoin = "round";
  c.stroke();
}
function paintedGround(id) {
  const ground = document.createElement("canvas");
  ground.width = 1600;
  ground.height = 1216;
  const main = ctx;
  ctx = ground.getContext("2d");
  ctx.imageSmoothingEnabled = true;
  const kind = id === "woods" ? 2 : id === "highlands" ? 3 : 0;
  ctx.fillStyle =
    id === "woods" ? "#63794b" : id === "highlands" ? "#93a568" : "#9ab267";
  ctx.fillRect(0, 0, 1600, 1216);
  ctx.globalAlpha = id === "woods" ? 0.72 : 0.58;
  ctx.fillStyle = paintedPattern("ground", kind % 2, Math.floor(kind / 2));
  ctx.fillRect(0, 0, 1600, 1216);
  ctx.globalAlpha = 1;
  const mask = document.createElement("canvas");
  mask.width = 1600;
  mask.height = 1216;
  const m = mask.getContext("2d");
  m.fillStyle = m.strokeStyle = "#fff";
  if (id === "town") {
    // A softly irregular sandy square replaces the former rectangular cobblestone grid.
    m.beginPath();
    for (let i = 0; i <= 40; i++) {
      const a = (i / 40) * Math.PI * 2,
        r = 1 + 0.035 * Math.sin(a * 5) + 0.02 * Math.cos(a * 3),
        x = 755 + Math.cos(a) * 292 * r,
        y = 594 + Math.sin(a) * 240 * r;
      if (i === 0) m.moveTo(x, y);
      else m.lineTo(x, y);
    }
    m.closePath();
    m.fill();
    smoothTrail(
      m,
      [
        [760, 40],
        [760, 1180],
      ],
      78,
    );
    smoothTrail(
      m,
      [
        [90, 650],
        [1510, 650],
      ],
      86,
    );
    smoothTrail(
      m,
      [
        [450, 390],
        [800, 390],
      ],
      67,
    );
  } else
    for (const path of AREA_DATA[id].paths)
      smoothTrail(m, path, id === "woods" ? 67 : 72);
  m.globalCompositeOperation = "source-in";
  m.fillStyle = "#e6cf8c";
  m.fillRect(0, 0, 1600, 1216);
  m.globalAlpha = 0.8;
  m.fillStyle = paintedPattern("ground", 1, 0);
  m.fillRect(0, 0, 1600, 1216);
  ctx.save();
  ctx.filter = "blur(1.6px)";
  ctx.drawImage(mask, 0, 0);
  ctx.restore();
  if (id === "town") {
    for (const [start, end, base, height] of [
      [40, 430, 294, 56],
      [80, 660, 920, 50],
    ])
      for (let x = start; x < end; x += 48) {
        const y =
          base + Math.floor(Math.sin(x / (base === 294 ? 85 : 90)) * 2) * 4;
        regionSprite(3, 3, x, y - 7, 49, height + 8);
      }
    for (const t of trees) softShadow(t.x + 28, t.y + 10, 75, 24, 0.2);
    for (const h of houses)
      softShadow(h.x + h.w * 0.65, h.y + h.h + 8, h.w * 0.8, 28, 0.25);
    for (const r of rocks) {
      if (
        inTown(r.x, r.y) ||
        isPath(r.x, r.y) ||
        Math.abs(r.x - river(r.y)) < 90
      )
        continue;
      softShadow(r.x + 4, r.y + 3, 19, 7, 0.15);
      sprite("village", 0, 2, r.x - 17, r.y - 27, 34, 32);
    }
    for (
      let i = 0;
      i < flowers.length;
      i += GAME_SETTINGS.world.flowerSpacing
    ) {
      const f = flowers[i];
      if (
        !inTown(f.x, f.y) &&
        !isPath(f.x, f.y) &&
        Math.abs(f.x - river(f.y)) > 90
      )
        sprite("village", 2, 3, f.x - 12, f.y - 12, 24, 24);
    }
  } else {
    const state = areaState(id);
    for (const t of state.trees)
      softShadow(t.x + 29, t.y + 10, 75, 26, id === "woods" ? 0.26 : 0.18);
    if (id === "highlands")
      for (const r of AREA_DATA.highlands.ridges) {
        for (let x = r.x; x < r.x + r.w; x += 48) {
          if (Math.abs(x + 24 - r.gapX) < r.gapWidth / 2) continue;
          regionSprite(3, 3, x, r.y, 49, r.h);
        }
        for (let y = r.y; y < r.y + r.h; y += 9) {
          ctx.fillStyle = "#baa67666";
          ctx.fillRect(r.gapX - r.gapWidth / 2 + 7, y, r.gapWidth - 14, 2);
        }
      }
  }
  ctx = main;
  return ground;
}
let terrainArt;
function drawTerrain() {
  if (currentArea !== "town") {
    drawAreaTerrain();
    return;
  }
  if (!terrainArt) terrainArt = paintedGround("town");
  ctx.drawImage(terrainArt, 0, 0);
  drawRiver();
  drawWindGrass();
}
function drawTree(t) {
  if (t.x < camera.x - 85 || t.x > camera.x + W + 85) return;
  const variant = Math.floor(grain(Math.round(t.x), Math.round(t.y)) * 4),
    height = (variant === 1 ? 155 : 137) * (0.92 + grain(t.x, t.y) * 0.14),
    width = variant === 1 ? 117 : 133,
    sway =
      Math.sin(time * 1.4 + t.x * 0.013 + t.y * 0.007) *
      windState().strength *
      0.015;
  ctx.save();
  ctx.translate(t.x, t.y + 5);
  ctx.transform(1, 0, sway, 1, 0, 0);
  sprite("village", variant, 0, -width / 2, -height, width, height);
  ctx.restore();
}
function drawHouse(h, dark) {
  sprite("town", houses.indexOf(h), 1, h.x - 18, h.y - 44, h.w + 36, h.h + 49);
  if (dark > 0.12) {
    ctx.save();
    ctx.globalCompositeOperation = "screen";
    const g = ctx.createRadialGradient(
      h.x + h.w / 2,
      h.y + h.h - 16,
      2,
      h.x + h.w / 2,
      h.y + h.h - 16,
      33,
    );
    g.addColorStop(0, "#ffce79a0");
    g.addColorStop(1, "#ffb44a00");
    ctx.fillStyle = g;
    ctx.fillRect(h.x + h.w / 2 - 33, h.y + h.h - 49, 66, 66);
    ctx.restore();
  }
}
function playerAnimation() {
  if (battle.swing > 0)
    return {
      sheet: "hero-attack",
      dir: battle.dir,
      frame: Math.min(7, Math.floor((1 - battle.swing / ATTACK_DURATION) * 8)),
    };
  if (player.dir === 1 || player.dir === 3) {
    const running = player.moving && player.dashing,
      stride = running
        ? GAME_SETTINGS.player.runStridePixels
        : GAME_SETTINGS.player.walkStridePixels;
    return {
      sheet: running ? "hero-side-run" : "hero-side-walk",
      dir: 0,
      frame: player.moving
        ? Math.floor((player.sideDistance / stride) * 8) % 8
        : 2,
      flip: player.dir === 3,
    };
  }
  return {
    sheet: player.moving && player.dashing ? "hero-run" : "hero-walk",
    dir: player.dir,
    frame: player.moving ? Math.floor(player.walk) % 8 : 0,
  };
}
function drawPlayer() {
  const action = playerAnimation(),
    image = art[action.sheet],
    [sx, sy, sw, sh, anchorX, anchorY] =
      heroFrames[action.sheet][action.dir][action.frame],
    scale = heroScales[action.sheet];
  softShadow(player.x + 2, player.y + 2, 14, 5, 0.23);
  ctx.save();
  ctx.translate(player.x, player.y);
  if (action.flip) ctx.scale(-1, 1);
  ctx.drawImage(
    image,
    sx,
    sy,
    sw,
    sh,
    -(anchorX - sx) * scale,
    -(anchorY - sy) * scale,
    sw * scale,
    sh * scale,
  );
  ctx.restore();
  if (player.charge > 0 && !player.dashing) {
    ctx.fillStyle = "#463b2b";
    ctx.fillRect(player.x - 15, player.y - 65, 30, 4);
    ctx.fillStyle = "#eac978";
    ctx.fillRect(
      player.x - 14,
      player.y - 64,
      28 * Math.min(1, player.charge / GAME_SETTINGS.player.dashChargeSeconds),
      2,
    );
  }
}
function drawVillageDetails() {
  if (currentArea !== "town") return;
  ctx.save();
  ctx.translate(1118, 650);
  ctx.rotate(Math.PI / 2);
  sprite("village", 0, 3, -27, -93, 54, 186);
  ctx.restore();
  sprite("town", 0, 3, 646, 706, 59, 47);
  sprite("town", 1, 3, 677, 614, 49, 48);
  softShadow(866, 738, 43, 12, 0.18);
  sprite("town", Math.floor(time * 3) % 4, 2, 819, 650, 94, 94);
  for (let x = 534; x < 633; x += 36) sprite("town", 2, 0, x, 561, 36, 38);
  for (let x = 850; x < 977; x += 36) sprite("town", 2, 0, x, 561, 36, 38);
  for (const h of houses) {
    sprite("village", 2, 1, h.x + h.w - 8, h.y + h.h - 17, 33, 32);
    sprite("village", 2, 3, h.x - 28, h.y + h.h - 8, 31, 26);
  }
}
function waterSurfacePath() {
  ctx.beginPath();
  for (let y = camera.y - 12; y <= camera.y + H + 12; y += 6) {
    const x = river(y) - 57;
    if (y === camera.y - 12) ctx.moveTo(x, y);
    else ctx.lineTo(x, y);
  }
  for (let y = camera.y + H + 12; y >= camera.y - 12; y -= 6)
    ctx.lineTo(river(y) + 57, y);
  ctx.closePath();
}
function fillPaintedWater(bounds, speed = 3) {
  const frame = 1,
    next = 3,
    mix = 0.12 + 0.08 * Math.sin(time * 0.7);
  ctx.save();
  ctx.translate((speed * time) % 512, 0);
  ctx.fillStyle = paintedPattern("regions", frame, 2, 256);
  ctx.fillRect(bounds.x - ((speed * time) % 512), bounds.y, bounds.w, bounds.h);
  ctx.globalAlpha = mix;
  ctx.fillStyle = paintedPattern("regions", next, 2, 256);
  ctx.fillRect(bounds.x - ((speed * time) % 512), bounds.y, bounds.w, bounds.h);
  ctx.restore();
}
function drawRiver() {
  ctx.save();
  waterSurfacePath();
  ctx.lineWidth = 17;
  ctx.strokeStyle = "#cab980";
  ctx.stroke();
  ctx.save();
  ctx.clip();
  fillPaintedWater({ x: 1000, y: camera.y - 20, w: 260, h: H + 40 }, 8);
  for (let i = 0; i < 32; i++) {
    const y = (i * 43 + time * (15 + grain(i, 13) * 8)) % (MH * T),
      x = river(y) + (grain(i, 44) * 2 - 1) * 44;
    if (y < camera.y || y > camera.y + H) continue;
    ctx.strokeStyle = "#e0edcc40";
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(x - 4, y);
    ctx.quadraticCurveTo(x, y + 2, x + 8, y + 1);
    ctx.stroke();
  }
  ctx.restore();
  ctx.restore();
}
function drawWindGrass() {
  for (let y = Math.floor(camera.y / 100) * 100; y < camera.y + H; y += 100)
    for (const side of [-1, 1]) {
      const x = river(y) + side * 75;
      ctx.save();
      ctx.translate(x, y);
      ctx.transform(
        1,
        0,
        Math.sin(time * 1.5 + y * 0.03) * windState().strength * 0.025,
        1,
        0,
        0,
      );
      sprite("village", 3, 2, -15, -32, 30, 36);
      ctx.restore();
    }
}
function drawTownObject(o) {
  ctx.save();
  if (o.row === 0 && o.col === 1) {
    ctx.translate(o.x + o.w / 2, o.y + o.h);
    ctx.transform(
      1,
      0,
      Math.sin(time * 2.2 + o.x) * windState().strength * 0.02,
      1,
      0,
      0,
    );
    sprite("town", o.col, o.row, -o.w / 2, -o.h, o.w, o.h);
  } else sprite("town", o.col, o.row, o.x, o.y, o.w, o.h);
  ctx.restore();
}
