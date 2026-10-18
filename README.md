# Willowmere — version 5

A peaceful Canvas RPG world with local pixel-art PNG sprite sheets and terrain tiles. No package installation or build step.

## Play

Extract the entire ZIP, keeping `assets/` beside `index.html`, `game.js`, `art.js`, and `style.css`. Open `index.html` in a browser.

For a local server, run `python3 -m http.server 8000 --bind 0.0.0.0` from this directory and open the server in your browser.

- WASD / arrow keys: eight-direction movement.
- Shift / Space: hold to charge a straight-line dash; release to stop.
- E: interact with nearby scenery.
- F / Fullscreen button: enter or leave fullscreen. Escape also exits.
- Sound button: optional synthesized wind ambience.
- Touch controls include direction, dash, and interaction.

## Pixel art

Three bundled image files supply all terrain, scenery, and character artwork:

- `assets/terrain.png`: grass, dirt, water animation tiles, and cliffs.
- `assets/adventurer.png`: sixteen walk frames across four facing directions.
- `assets/village.png`: trees, houses, bridge, well, garden, rocks, and props.

Canvas draws source rectangles with smoothing disabled. Terrain tiles are cached; water tiles animate and scroll downstream. Tree sprite strips sway, and the character's upper sprite moves subtly with wind. Character frame coordinates align the feet across differing source padding. See `assets/README.md` for layout and provenance. The PNGs are original generated art, not ripped game graphics.

## Time and weather

The in-game clock uses `America/New_York`, including EST/EDT automatically. Lighting follows fixed local hours: dawn 5–7 AM and dusk 6–9 PM, with a restrained 32% maximum nighttime tint. There are no star/firefly particles.

Open-Meteo weather for Flushing, Queens (40.7654, -73.8174) refreshes every ten minutes. Temperature is Fahrenheit and wind is mph. Rain and snow are visual effects, and reported wind speed drives foliage and character movement. Gust timing and direction are artistic. If the weather feed fails, the game labels its fallback or last received conditions. A labeled gentle 4 mph ambient breeze is used when live wind is unavailable. Weather data: https://open-meteo.com/ (CC BY 4.0).

All art loads locally; only weather and optional Google Fonts require Internet access. System fonts work when Google Fonts is unavailable. No API key or location permission is needed.

No enemies or combat yet. Houses are exterior scenery. Wind effects are sprite movement rather than separately rigged hair animation.
