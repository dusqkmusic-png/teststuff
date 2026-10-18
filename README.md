# Willowmere

A peaceful, original pixel-art world in the browser. No dependencies or build step.

## Run

From this directory, run `python3 -m http.server 8000 --bind 0.0.0.0`, then open the server in your browser. You can also open `index.html` directly, though live weather depends on your browser's network rules.

Use WASD or arrow keys to move, E to interact with nearby scenery, and the sound button for optional synthesized wind ambience. Touch devices have directional buttons. There are no enemies, combat, or quests. Houses are exterior scenery for now.

Day/night uses `America/New_York`, including EST/EDT automatically, regardless of the computer's timezone. The lighting transitions follow fixed local hours (dawn 5–7 AM, dusk 6–9 PM), rather than astronomical sunrise/sunset.

Weather is fetched on load and every ten minutes from Open-Meteo for Flushing, Queens (40.7654, -73.8174). Rain, snow, cloud cover, and mist are represented visually. No API key, location permission, or backend is needed. An unavailable feed is labeled explicitly; the world remains playable with clear visuals, or the last received conditions. Temperature is in Fahrenheit. Weather data: https://open-meteo.com/ (CC BY 4.0).

The optional Google Fonts request has system-font fallbacks. All game art is drawn locally with Canvas. No external image assets.
