# Asterfall 0.2

A Java 17 desktop action RPG built on libGDX 1.13.1 with the LWJGL 2 backend.
All characters and scenery are actual 3D polygon meshes; no character sprites.

## Build

The source archive includes the exact dependency JARs and ECJ compiler, so a
separate JDK, Gradle, or Maven installation is not needed. Use Java 17 or later
and Python 3, then run `python3 build.py` from the project directory. No network
access is required to build. Build writes `asterfall.jar` and `classes/`.

Run on Windows with Java available:

    java -cp "asterfall.jar;lib/*" game.Desktop

Run on Linux with a full graphical Java runtime, OpenGL, X11, and audio:

    java -cp 'asterfall.jar:lib/*' game.Desktop

The Linux system's Java installation may be headless; that will not work for
an actual game window. On macOS, native jars/backend changes would be required.

A Windows release includes `runtime/` (Temurin JRE 17 x64), so the user only
launches `Asterfall.exe`. The runtime is supplied separately from source ZIP.
`launch4j.xml` is the reproducible launcher config; build it using Launch4j 3.50.
Launch4j and its linker are build tools and are not needed at game runtime.

## Verification

Always isolate saves when testing. `--self-test` intentionally writes a corrupt
fixture to test preservation; never run it with your real user.home.

    java -Duser.home=/tmp/asterfall-unit -jar asterfall.jar --self-test
    java -Duser.home=/tmp/asterfall-render -cp 'asterfall.jar:lib/*' game.Desktop --smoke
    java -Duser.home=/tmp/asterfall-weather -cp 'asterfall.jar:lib/*' game.WeatherCheck

Use a NEW empty test directory for each run. `--smoke` starts an actual window,
renders title/world/inventory/settings, exercises combat, boss progression,
loot, equipment, replay, and saves, writes four screenshots one directory above
the game, then exits. WeatherCheck contacts Open-Meteo for 11355 and validates
cache loading. HTTPS_PROXY is honored when present. The development machine
may need its own CA trust configuration; never disable TLS validation.

## Modules

- `Content`: JSON validation, stable item IDs, weighted loot selection.
- `SaveData`: versioned persistent character, equipment, atomic save + backup.
- `Weather`: daemon HTTP worker, city or coordinate lookup, cache, local clock.
- `Models`: sculpted 3D geometry, painted materials, articulated character transforms.
- `WorldBuilder`: original modular textured masonry, village and garden geometry.
- `paint_materials.py`: reproducible original painted-style texture generation.
- `Asterfall`: world, combat, enemy attack windups, drops, boss, expeditions.
- `UI`: desktop HUD, input, equipment, talents, location/settings, menus.
- `Desktop`: launcher, error reporting, desktop configuration.

Weather fetches never block the render thread. Game state uses the render thread;
location persistence is posted back to it. No backend server or telemetry.

## Safe content updates

Keep existing item IDs stable. New item definitions can be appended to
`content/items.json`. Valid slots: weapon, armor, charm. Preferred conditions:
any, day, night, rain. Every weight must stay positive. A matching condition
multiplies selection weight by 1.25, with no exclusive availability gates.

An item's base weight also sets special identity rarity in this version:
weights <=3 are Rare; weights <0.2 are Mythic. Other definitions roll Common,
Enchanted, or Rare. For future packs, separate these fields explicitly if
rarity and frequency need independent control. Existing rolled item stats stay
in saves. If retiring an ID, migrate saves or keep a legacy definition.

`zones.json` defines encounters within the existing handcrafted world bounds
x=-34..32, z=-51..23. Increasing or adding zones changes encounters, not terrain.
Enemy count should be >=1, centers reachable on land, and IDs unique. Distant
maps, quest content, new enemy behavior, and new mechanics require Java work.

Saves are version 1. Add backward-compatible fields with defaults when possible.
For structural changes, implement an explicit migration, preserve backups,
and exercise it against old saves before releasing. Never silently reset saves.

## Known limits

No navmesh, multiplayer, quest system, dungeon generation, or controller support.
Right-click movement uses direct steering, so WASD is useful around obstacles.
Directional shadow mapping with receiver bias; articulated mesh-part animation. Enemy variety
is slime-based with elite modifiers and a larger boss. The source is kept small
to make future collaborative changes practical.

Windows launcher has been cross-built but requires Windows playtesting.
The initial loop needs human feedback on combat feel and difficulty balance.

The combat loop traverses effects by a reverse index over the original count. Damage callbacks may append level-up effects, which are processed on the next update. Never restore an iterator over this mutating list. `--combat-regression` tests the exact projectile-kill/level-up path and timed melee damage, then 60 repetitions with a mesh-ownership check. Run under X11/Xvfb with an isolated user.home.
