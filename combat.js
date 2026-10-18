"use strict";

// Enemy behavior and combat. Edit settings.js for difficulty and world-data.js for fields.

function fieldAt(x, y, margin = 0) {
  if (currentArea !== "town") return -1;
  return fields.findIndex(
    (f) =>
      x >= f.x - margin &&
      x <= f.x + f.w + margin &&
      y >= f.y - margin &&
      y <= f.y + f.h + margin,
  );
}
const ATTACK_DURATION = GAME_SETTINGS.sword.swingSeconds,
  ATTACK_COOLDOWN = GAME_SETTINGS.sword.cooldownSeconds;
const battle = {
  hp: GAME_SETTINGS.player.maxHealth,
  maxHp: GAME_SETTINGS.player.maxHealth,
  kills: 0,
  invulnerable: 0,
  cooldown: 0,
  swing: 0,
  swingId: 0,
  dir: 0,
};
const enemies = [],
  sparks = [];
function spawnEnemy(enemy) {
  const f = fields[enemy.field];
  for (let n = 0; n < 80; n++) {
    const x = f.x + 25 + rand() * (f.w - 50),
      y = f.y + 30 + rand() * (f.h - 60);
    if (!blocked(x, y) && Math.hypot(player.x - x, player.y - y) > 96) {
      Object.assign(enemy, {
        x,
        y,
        hp: GAME_SETTINGS.enemies.health,
        active: true,
        respawn: 0,
        hit: 0,
        lastSwing: -1,
      });
      return true;
    }
  }
  enemy.respawn = 1;
  return false;
}
function initCombat() {
  if (!GAME_SETTINGS.enemies.enabled) {
    syncBattleHud();
    return;
  }
  for (let field = 0; field < fields.length; field++)
    for (let i = 0; i < GAME_SETTINGS.enemies.perField; i++) {
      const e = { field, active: false, respawn: 0, phase: rand() * 6.28 };
      enemies.push(e);
      spawnEnemy(e);
    }
  syncBattleHud();
}
function attack() {
  if (battle.cooldown > 0 || !art.combat) return;
  keys.delete("shift");
  keys.delete(" ");
  player.dashing = false;
  player.charge = 0;
  battle.cooldown = ATTACK_COOLDOWN;
  battle.swing = ATTACK_DURATION;
  battle.swingId++;
  battle.dir = player.dir;
}
function syncBattleHud() {
  document.querySelector("#health").textContent =
    "♥".repeat(battle.hp) + "♡".repeat(battle.maxHp - battle.hp);
  document
    .querySelector("#health")
    .setAttribute("aria-label", `${battle.hp} of ${battle.maxHp} health`);
  document.querySelector("#kills").textContent = String(battle.kills);
  document.querySelector("#zone-status").textContent =
    currentArea !== "town"
      ? "EXPLORING · M FOR MAP"
      : fieldAt(player.x, player.y) >= 0
        ? GAME_SETTINGS.enemies.enabled
          ? "ENEMY FIELD · J / Z TO SWING"
          : "PEACEFUL FIELD"
        : "VILLAGE · SAFE HAVEN";
}
function updateCombat(dt) {
  battle.cooldown = Math.max(0, battle.cooldown - dt);
  battle.invulnerable = Math.max(0, battle.invulnerable - dt);
  const previousProgress = 1 - battle.swing / ATTACK_DURATION;
  const wasSwinging = battle.swing > 0;
  battle.swing = Math.max(0, battle.swing - dt);
  const progress = 1 - battle.swing / ATTACK_DURATION;
  const swinging =
    wasSwinging &&
    progress >= GAME_SETTINGS.sword.hitStartsAt &&
    previousProgress <= GAME_SETTINGS.sword.hitEndsAt;
  const facing = [
    [0, 1],
    [1, 0],
    [0, -1],
    [-1, 0],
  ][battle.dir];
  if (currentArea !== "town") {
    for (let i = sparks.length - 1; i >= 0; i--) {
      sparks[i].life -= dt;
      if (sparks[i].life <= 0) sparks.splice(i, 1);
    }
    syncBattleHud();
    return;
  }
  for (const e of enemies) {
    if (!e.active) {
      e.respawn -= dt;
      if (e.respawn <= 0) spawnEnemy(e);
      continue;
    }
    e.hit = Math.max(0, e.hit - dt);
    const dx = e.x - player.x,
      dy = e.y - player.y,
      distance = Math.hypot(dx, dy),
      same = fieldAt(player.x, player.y) === e.field;
    // One hit per target per swing; direction and reach agree with the visible arc.
    if (
      swinging &&
      e.lastSwing !== battle.swingId &&
      distance < GAME_SETTINGS.sword.reach &&
      (distance < 15 || (dx * facing[0] + dy * facing[1]) / distance > 0.12)
    ) {
      e.lastSwing = battle.swingId;
      e.hp -= GAME_SETTINGS.sword.damage;
      e.hit = 0.22;
      const nx = distance ? dx / distance : facing[0],
        ny = distance ? dy / distance : facing[1];
      const f = fields[e.field],
        kx = Math.max(f.x + 15, Math.min(f.x + f.w - 15, e.x + nx * 18)),
        ky = Math.max(f.y + 15, Math.min(f.y + f.h - 15, e.y + ny * 18));
      if (!blocked(kx, ky)) {
        e.x = kx;
        e.y = ky;
      }
      sparks.push({ x: e.x, y: e.y, life: 0.3 });
      if (e.hp <= 0) {
        e.active = false;
        e.respawn = GAME_SETTINGS.enemies.respawnSeconds;
        battle.kills++;
      }
      continue;
    }
    const f = fields[e.field];
    let mx, my;
    if (
      same &&
      distance < GAME_SETTINGS.enemies.noticeDistance &&
      distance > 18
    ) {
      mx = -dx / distance;
      my = -dy / distance;
    } else {
      mx = Math.sin(time * 0.7 + e.phase);
      my = Math.cos(time * 0.55 + e.phase);
    }
    const speed =
        e.hit > 0
          ? 0
          : same
            ? GAME_SETTINGS.enemies.chaseSpeed
            : GAME_SETTINGS.enemies.roamSpeed,
      nx = e.x + mx * speed * dt,
      ny = e.y + my * speed * dt;
    if (nx > f.x + 14 && nx < f.x + f.w - 14 && !blocked(nx, e.y)) e.x = nx;
    if (ny > f.y + 14 && ny < f.y + f.h - 14 && !blocked(e.x, ny)) e.y = ny;
    if (
      same &&
      Math.hypot(e.x - player.x, e.y - player.y) < 21 &&
      battle.invulnerable <= 0 &&
      e.hit <= 0
    ) {
      battle.hp = Math.max(0, battle.hp - GAME_SETTINGS.enemies.contactDamage);
      battle.invulnerable = GAME_SETTINGS.player.damageProtectionSeconds;
      const len = Math.hypot(dx, dy) || 1;
      let px = player.x - (dx / len) * 20,
        py = player.y - (dy / len) * 20;
      if (!blocked(px, py)) {
        player.x = px;
        player.y = py;
      }
      if (battle.hp <= 0) {
        Object.assign(player, {
          x: GAME_SETTINGS.player.startX,
          y: GAME_SETTINGS.player.startY,
          dashing: false,
          charge: 0,
        });
        keys.clear();
        battle.hp = battle.maxHp;
        battle.invulnerable = 2;
        battle.swing = 0;
        msg(GAME_SETTINGS.text.recovery);
      }
    }
  }
  for (let i = sparks.length - 1; i >= 0; i--) {
    sparks[i].life -= dt;
    if (sparks[i].life <= 0) sparks.splice(i, 1);
  }
  syncBattleHud();
}
function drawEnemy(e) {
  if (!e.active) return;
  ctx.save();
  if (e.hit > 0) ctx.globalAlpha = 0.45 + Math.sin(time * 50) * 0.25;
  sprite(
    "combat",
    Math.floor(time * 5 + e.phase) % 4,
    e.field,
    e.x - 19,
    e.y - 29 + Math.sin(time * 6 + e.phase) * 2,
    38,
    36,
  );
  ctx.restore();
  if (e.hp < GAME_SETTINGS.enemies.health) {
    rect(e.x - 12, e.y - 33, 24, 3, "#352c31");
    rect(
      e.x - 11,
      e.y - 32,
      (22 * e.hp) / GAME_SETTINGS.enemies.health,
      1,
      "#f1c080",
    );
  }
}
function drawBattlePlayer() {
  ctx.save();
  if (battle.invulnerable > 0)
    ctx.globalAlpha = Math.floor(time * 12) % 2 ? 0.45 : 1;
  drawPlayer();
  ctx.restore();
}
function drawBattleEffects() {
  for (const s of sparks)
    sprite(
      "combat",
      Math.min(3, Math.floor(((0.3 - s.life) / 0.3) * 4)),
      2,
      s.x - 22,
      s.y - 30,
      44,
      44,
    );
}
function drawFieldSigns() {
  for (const f of fields) {
    sprite("village", 2, 2, f.x + f.w / 2 - 21, 865, 42, 38);
  }
}
