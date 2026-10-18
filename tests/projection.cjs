// Run a local server first. Requires Playwright and Chromium in the development environment.
const { chromium } = require("playwright");
const assert = require("node:assert/strict");
(async () => {
  const browser = await chromium.launch({
    executablePath: process.env.CHROMIUM_PATH || "/usr/bin/chromium",
    args: ["--no-sandbox"],
  });
  try {
    const page = await browser.newPage({
      viewport: { width: 1280, height: 960 },
    });
    const errors = [];
    page.on("pageerror", (e) => errors.push(e.message));
    await page.route("https://api.open-meteo.com/**", (r) =>
      r.fulfill({
        json: {
          current: { weather_code: 61, temperature_2m: 56, wind_speed_10m: 12 },
        },
      }),
    );
    await page.addInitScript(() => (window.requestAnimationFrame = () => 0));
    await page.goto(process.env.GAME_URL || "http://127.0.0.1:8000");
    await page.waitForFunction(() => areasReady && weather.live);
    console.log(
      await page.evaluate(() => {
        const check = (v, m) => {
          if (!v) throw Error(m);
        };
        check(VIEW.groundScale < 1, "Ground must be foreshortened");
        const floor = projectGroundY(600) - projectGroundY(500);
        check(Math.abs(floor - 66) < 0.001, "Floor projection");
        // Inspect actual draw transforms: floor compresses, upright art does not.
        const draw = ctx.drawImage.bind(ctx);
        let matrix;
        ctx.drawImage = (...args) => {
          matrix = ctx.getTransform();
          draw(...args);
        };
        ctx.save();
        ctx.scale(1, VIEW.groundScale);
        worldPass = true;
        sprite("town", 0, 1, 100, 100, 176, 208);
        check(Math.abs(matrix.d - 1) < 0.001, "Building was squashed");
        groundSprite("village", 0, 3, 100, 100, 90, 208);
        check(
          Math.abs(matrix.d - VIEW.groundScale) < 0.001,
          "Pier must follow floor",
        );
        worldPass = false;
        ctx.restore();
        ctx.drawImage = draw;
        const sorted = sceneObjects(0);
        check(
          sorted.every((o, i) => !i || o.y >= sorted[i - 1].y),
          "Depth queue order",
        );
        check(
          sorted.some((o) => o.y === 737) && sorted.some((o) => o.occludes),
          "Foreground props missing",
        );
        // Same NY time, weather, field combat, and footprint collision.
        const clock = clockHandAngles(new Date("2026-10-18T19:30:00Z"));
        check(clock.hour === 105 && clock.minute === 180, "NY analog clock");
        check(weather.code === 61 && weather.wind === 12, "Live weather");
        const home = houses[0];
        check(
          blocked(home.x + home.w / 2, home.y + home.h - 10),
          "House footprint",
        );
        check(!blocked(home.x + home.w / 2, home.y - 15), "Walk behind house");
        for (const id of ["town", "woods", "lake", "highlands"]) {
          check(enterArea(id), "Enter " + id);
          snapCamera();
          const p = screenPoint(player.x, player.y);
          check(
            p.x >= 0 && p.x <= W && p.y >= 0 && p.y <= H,
            "Camera missed " + id,
          );
          render(0);
          check(!worldPass, "Unbalanced world render");
          if (id !== "town")
            for (const trail of AREA_DATA[id].paths)
              for (let i = 1; i < trail.length; i++) {
                const a = trail[i - 1],
                  b = trail[i],
                  steps = Math.ceil(Math.hypot(b[0] - a[0], b[1] - a[1]) / 8);
                for (let n = 0; n <= steps; n++)
                  check(
                    !blocked(
                      a[0] + ((b[0] - a[0]) * n) / steps,
                      a[1] + ((b[1] - a[1]) * n) / steps,
                    ),
                    "Blocked trail " + id,
                  );
              }
        }
        check(
          groundElevation(100, "highlands") === 92 &&
            groundElevation(1000, "highlands") === 0,
          "Terrace heights",
        );
        for (const ridge of AREA_DATA.highlands.ridges) {
          let previous = projectGroundY(ridge.y - 1);
          for (let y = ridge.y; y <= ridge.y + ridge.h + 1; y++) {
            const next = projectGroundY(y);
            check(next > previous && next - previous < 2, "Discontinuous ramp");
            previous = next;
            check(!blocked(ridge.gapX, y), "Ramp collision");
          }
        }
        enterArea("lake");
        check(blocked(820, 570) && !blocked(800, 800), "Lake / pier collision");
        enterArea("town");
        const e = enemies[0];
        Object.assign(player, {
          x: fields[0].x + 150,
          y: fields[0].y + 150,
          dir: 1,
        });
        Object.assign(e, {
          x: player.x + 28,
          y: player.y,
          hp: 2,
          active: true,
          hit: 0,
          lastSwing: -1,
        });
        battle.cooldown = 0;
        attack();
        updateCombat(0.05);
        check(e.hp === 2, "Sword windup");
        updateCombat(0.08);
        check(e.hp === 1, "Sword reach");
        updateCombat(0.05);
        check(e.hp === 1, "Duplicate hit");
        enterArea("highlands");
        Object.assign(player, { x: 1040, y: 350 });
        snapCamera();
        dayState = () => ({ hour: 23, dark: 0.52, phase: "Moonlit night" });
        render(0.016);
        return "PASS oblique transforms, depth, camera on four maps, continuous elevations, trails, collision, sword, clock and weather";
      }),
    );
    await page.locator("#map-button").click();
    assert(await page.locator("#world-map").isVisible());
    await page.locator("#area-choices button").first().click();
    await page.locator("#fullscreen").click();
    await page.waitForTimeout(100);
    await page.locator("#map-button").click();
    assert(await page.locator("#world-map").isVisible());
    await page.locator("#close-map").click();
    await page.locator("#fullscreen").click();
    await page.setViewportSize({ width: 390, height: 844 });
    assert(
      !(await page.evaluate(
        () => document.documentElement.scrollWidth > innerWidth,
      )),
    );
    assert.deepEqual(errors, []);
    console.log("PASS fullscreen, map travel, mobile and browser errors");
  } finally {
    await browser.close();
  }
})().catch((e) => {
  console.error(e);
  process.exitCode = 1;
});
