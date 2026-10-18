# Willowmere — version 12

A Canvas RPG with a completely rebuilt painted storybook world. Rounded cottages, lush trees, sandy paths, sculpted scenery, a new traveler, and parchment menus replace the previous pixel artwork. Gameplay, maps, analog New York clocks, lighting schedule, and Flushing weather remain unchanged.

**Start editing with [EDITING.md](EDITING.md).** Visual direction: [ART-DIRECTION.md](ART-DIRECTION.md). Asset layout and provenance: [assets/README.md](assets/README.md). No installation or build step is needed.

## Play

Extract the entire ZIP and keep all files and the assets folder together. Open `index.html` in a browser. Do not remove the settings, world-data, startup, or theme files; they are part of the game.

For a local server, run `python3 -m http.server 8000 --bind 0.0.0.0` from this directory and open the server in your browser.

- WASD / arrow keys: eight-direction movement.
- Shift / Space: hold to charge a straight-line dash; release to stop.
- J / Z or click the game: swing your sword. Touch devices have a sword button.
- E: interact with nearby scenery; the village bench restores health.
- M / World map button: visit town, the Woods, the Lake, or the Highlands. Trail signs also work with E.
- F / Fullscreen button: enter or leave fullscreen. Escape also exits.
- Sound button: optional synthesized wind ambience.
- Touch controls include direction, dash, and interaction.

## Time and weather

The in-game clock uses `America/New_York`, including EST/EDT automatically. Lighting follows fixed local hours: dawn 5–7 AM and dusk 6–9 PM, with a distinct blue 52% maximum nighttime tint with warmer village lights. There are no star/firefly particles.

Open-Meteo weather for Flushing, Queens (40.7654, -73.8174) refreshes every ten minutes. Temperature is Fahrenheit and wind is mph. Rain and snow are visual effects, and reported wind speed drives foliage and character movement. Gust timing and direction are artistic. If the weather feed fails, the game labels its fallback or last received conditions. A labeled gentle 4 mph ambient breeze is used when live wind is unavailable. Weather data: https://open-meteo.com/ (CC BY 4.0).

All art and the bundled Alegreya font load locally; only live weather requires Internet access. No API key or location permission is needed.

Westwind Fields lie west of the village; Eastbank Fields are across the river bridge to the east. Each field contains four slimes. They roam, chase nearby players, and stay in their own field. Two sword hits defeat a slime; defeated slimes return after nine seconds, away from the player. You have five hearts, brief protection after taking damage, and recover in the village if defeated. Resting at the bench with E also restores health. The counter tracks defeats during the current session; reloading resets progress.

Houses are exterior scenery. Wind effects are sprite movement rather than separately rigged hair animation.

## Artwork and animation

All graphics are in `assets/storybook/`. Three character sheets contain eight frames in four directions for walking, running, and attacking. Horizontal locomotion uses distance-based timing and mirrored east frames for west. `hero-frames.js` supplies character rectangles and foot pivots; `painted-frames.js` supplies scenery rectangles. Canvas uses smooth scaling, cached textured ground, depth sorting, contact shadows, swaying trees, and animated water.

These are AI-generated prototype assets, separated for eventual replacement by commissioned art. See the art direction guide for the handoff list.
