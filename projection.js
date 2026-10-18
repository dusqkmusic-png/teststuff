"use strict";

// World coordinates describe the floor. Screen height and object height are separate.
// Change groundScale to tune the oblique camera; upright sprites retain their proportions.
const VIEW = Object.freeze({ groundScale: 0.66, buildingHeight: 208 });
let worldPass = false;
function groundElevation(y, area = currentArea) {
  if (area !== "highlands") return 0;
  // Each ridge has a 46px rise. Collision permits crossing only at its ramp.
  return AREA_DATA.highlands.ridges.reduce(
    (height, ridge) =>
      height + 46 * Math.max(0, Math.min(1, (ridge.y + ridge.h - y) / ridge.h)),
    0,
  );
}
function projectGroundY(y, area = currentArea) {
  return y * VIEW.groundScale - groundElevation(y, area);
}
function screenPoint(x, y) {
  return {
    x: x - camera.x,
    y: projectGroundY(y) - camera.y * VIEW.groundScale,
  };
}
function cameraTarget() {
  return {
    x: Math.max(0, Math.min(MW * T - W, player.x - W / 2)),
    y:
      Math.max(
        projectGroundY(0),
        Math.min(projectGroundY(MH * T) - H, projectGroundY(player.y) - H / 2),
      ) / VIEW.groundScale,
  };
}
function snapCamera() {
  Object.assign(camera, cameraTarget());
}
function visibleGroundHeight() {
  return (H + 120) / VIEW.groundScale;
}
function atElevation(y, draw) {
  ctx.save();
  ctx.translate(0, -groundElevation(y) / VIEW.groundScale);
  draw();
  ctx.restore();
}
function uprightAt(x, y, draw) {
  ctx.save();
  ctx.translate(x, y);
  ctx.scale(1, 1 / VIEW.groundScale);
  draw();
  ctx.restore();
}
function drawGroundCache(image) {
  if (currentArea !== "highlands") {
    ctx.drawImage(image, 0, 0);
    return;
  }
  // Strip projection follows the same continuous height function as actors and ramps.
  for (let y = 0; y < image.height; y += 2) {
    const height = Math.min(2, image.height - y);
    const top = projectGroundY(y) / VIEW.groundScale;
    const bottom = projectGroundY(y + height) / VIEW.groundScale;
    ctx.drawImage(
      image,
      0,
      y,
      image.width,
      height,
      0,
      top,
      image.width,
      bottom - top + 0.3,
    );
  }
}
function groundSprite(sheet, col, row, x, y, w, h) {
  const [sx, sy, sw, sh] = PAINTED_RECTS[sheet][row][col];
  ctx.drawImage(art[sheet], sx, sy, sw, sh, x, y, w, h);
}
