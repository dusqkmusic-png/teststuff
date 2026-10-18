# Willowmere — version 10

A peaceful Canvas RPG world with local pixel-art PNG sprite sheets and terrain tiles. No package installation or build step.

**Want to edit the game? Start with [EDITING.md](EDITING.md).** Common numbers and messages are in **settings.js**, names and dialogue are in **world-data.js**, and UI colors are in **theme.css**. Save a change and refresh the browser.

## Play

Extract the entire ZIP and keep all files and the assets folder together. Open `index.html` in a browser. Do not remove the settings, world-data, startup, or theme files; they are part of the game.

For a local server, run `python3 -m http.server 8000 --bind 0.0.0.0` from this directory and open the server in your browser.

- WASD / arrow keys: eight-direction movement.
- Shift / Space: hold to charge a straight-line dash; release to stop.
- J / Z or click the game: swing your sword. Touch devices have a sword button.
- E: interact with nearby scenery; the village bench restores health.
- F / Fullscreen button: enter or leave fullscreen. Escape also exits.
- Sound button: optional synthesized wind ambience.
- Touch controls include direction, dash, and interaction.

## Pixel art

Eight bundled image files supply all terrain, scenery, and character artwork:

- `assets/grass.png`: quiet, muted grass tiles with sparse blade details.
- `assets/terrain.png`: dirt, water animation tiles, and cliffs.
- `assets/hero-walk.png`: eight walking frames in each of four directions.
- `assets/hero-run.png`: eight running frames in each of four directions.
- `assets/hero-attack.png`: eight sword-swing frames in each of four directions.
- `assets/town.png`: cobblestones, bell arch, banners, buildings, flower beds, fountain, and stonework.
- `assets/village.png`: trees, houses, bridge, well, garden, rocks, and props.
- `assets/combat.png`: green/purple slimes, hit effects, and swords.

Canvas draws source rectangles with smoothing disabled. Terrain tiles are cached; water tiles animate and scroll downstream. Tree sprite strips sway, and the character's upper sprite moves subtly with wind. Character frame coordinates and foot pivots in hero-frames.js align the action sheets. See `assets/README.md` for layout and provenance. The PNGs are original generated art, not ripped game graphics.

## Time and weather

The in-game clock uses `America/New_York`, including EST/EDT automatically. Lighting follows fixed local hours: dawn 5–7 AM and dusk 6–9 PM, with a distinct blue 52% maximum nighttime tint with warmer village lights. There are no star/firefly particles.

Open-Meteo weather for Flushing, Queens (40.7654, -73.8174) refreshes every ten minutes. Temperature is Fahrenheit and wind is mph. Rain and snow are visual effects, and reported wind speed drives foliage and character movement. Gust timing and direction are artistic. If the weather feed fails, the game labels its fallback or last received conditions. A labeled gentle 4 mph ambient breeze is used when live wind is unavailable. Weather data: https://open-meteo.com/ (CC BY 4.0).

All art loads locally; only weather and optional Google Fonts require Internet access. System fonts work when Google Fonts is unavailable. No API key or location permission is needed.

Westwind Fields lie west of the village; Eastbank Fields are across the river bridge to the east. Each field contains four slimes. They roam, chase nearby players, and stay in their own field. Two sword hits defeat a slime; defeated slimes return after nine seconds, away from the player. You have five hearts, brief protection after taking damage, and recover in the village if defeated. Resting at the bench with E also restores health. The counter tracks defeats during the current session; reloading resets progress.

Houses are exterior scenery. Wind effects are sprite movement rather than separately rigged hair animation.

Version 7 calms the meadow with a separate muted grass atlas and fewer scattered flower patches. Village gardens, trees, weather, nighttime lighting, and field combat remain.

Version 8 uses the supplied Chrono Trigger town-square screenshot as visual inspiration for original art. Willowmere Bell Square has warm cobblestones, cream stonework, scarlet banners that move with wind, flower beds, market buildings, a fountain, and a central bell arch. Larger town objects are sorted by depth with the player. Bell columns, planters, stone rails, fountain, and bench have collision footprints while the main walking routes remain open. The surrounding meadows retain their quiet grass tiles. Town and traveler source rectangles are defined in art.js to align the generated sheets.

## Version 9 — action animation and menu windows

The traveler now has 96 distinct source frames: 8 frames × 4 directions × 3 actions. Walking plays at 10 frames per second; charged running at 20. Attacks play eight frames over 0.48 seconds, hold the character in place, and lock the facing direction. Damage occurs during the middle 25–75% of the swing, once per enemy, followed by recovery before another attack. The sword is drawn into the attack frames, replacing the previous detached rotating sword.

Time and weather use dark textured menu windows, beveled gray borders, white pixel lettering, and explicit LIVE/STALE/OFFLINE weather badges, inspired by the supplied UI reference. The bundled VT323 typeface works offline; its SIL Open Font License is in assets/fonts/OFL.txt. Time continues to follow New York's EST/EDT, and weather continues to use Flushing conditions.

## Version 10 — editable prototype

The engine is formatted for reading. Common difficulty, movement, clock, lighting, weather, scenery-count, and message settings are grouped in settings.js. World names, dialogue, fields, buildings, props, footprints, and image paths are in world-data.js. theme.css exposes UI color variables. No new runtime dependencies are required. Invalid common settings and syntax errors produce visible help. Defaults retain the version 9 gameplay and artwork.

Files load in this order: startup.js → settings.js → world-data.js → hero-frames.js → art.js → combat.js → game.js. The engine validates settings before starting. See EDITING.md for examples, limitations, and troubleshooting.
