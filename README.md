# Willowmere

A peaceful, original pixel-art world in the browser. No dependencies or build step.

## Run

From this directory, run `python3 -m http.server 8000 --bind 0.0.0.0`, then open the server in your browser. You can also open `index.html` directly, though live weather depends on your browser's network rules.

Use WASD or arrow keys to move in eight directions, hold Shift or Space to charge a straight-line dash, release to stop, and E to interact with nearby scenery, and the sound button for optional synthesized wind ambience. Touch devices have directional buttons. There are no enemies, combat, or quests. Houses are exterior scenery for now.

Day/night uses `America/New_York`, including EST/EDT automatically, regardless of the computer's timezone. The lighting transitions follow fixed local hours (dawn 5–7 AM, dusk 6–9 PM), rather than astronomical sunrise/sunset.

Weather is fetched on load and every ten minutes from Open-Meteo for Flushing, Queens (40.7654, -73.8174). Rain, snow, cloud cover, and mist are represented visually. No API key, location permission, or backend is needed. An unavailable feed is labeled explicitly; the world remains playable with clear visuals, or the last received conditions. Temperature is in Fahrenheit. Weather data: https://open-meteo.com/ (CC BY 4.0).

The optional Google Fonts request has system-font fallbacks. All game art is drawn locally with Canvas. No external image assets.

## Movement

Rendering now uses a 960×540 internal pixel canvas. Walking starts and stops immediately, diagonal speed is normalized, collision is resolved in small steps with sliding, and four-direction animation follows facing. Dash charges for 0.32 seconds, locks direction, and stops at obstacles. It is an inspired approximation, not a frame-exact recreation. External video/wiki research was blocked by the cloud network during this update.

## Version 3 — art and display

Press **F** or click **Fullscreen** for a distraction-free view; press F, Escape, or the exit button to return. Fullscreen uses the browser API, with a theater layout fallback where unavailable. The New York clock, date, and weather remain inside the game view. Night is a subtle dark tint (maximum 32% overlay); floating night particles are removed. Rain and snow still follow weather.

The scenery and traveler have been redrawn with original pixel artwork inspired by the detailed environments of Zelda 3 and Chrono Trigger: clustered foliage, bark, layered stone walls, individual shingles, window boxes, river reeds, textured cliffs, bridge hardware, and a traveler with a scarf and satchel. Static terrain and tree sprites are cached for performance. All art is generated locally in `art.js`; keep this file alongside `game.js`, `index.html`, and `style.css`.

## Version 4 — foliage, adventurer, river, and wind

Trees have irregular leaf fans, highlighted individual leaves, forked trunks, bark and moss. Roots stay fixed while cached canopy strips sway. The character is now a green-clad, blond adventurer with a pointed cap and small decorative shield, using broad SNES/GBA-style proportions and four facing animations. Hair and the cap tip flutter with wind, even while standing still.

The river has darker depths, shallow banks, shoreline foam, downstream streaks that follow its bends, rocks with trailing ripples, and moving surface ripples. Reeds and grass sway too. Wind strength uses Open-Meteo wind speed in mph, with gentle visual gusts. If live weather is unavailable, a labeled 4 mph ambient breeze is used; the game does not present that as actual local weather. Direction is artistic rather than tied to observed wind direction.
