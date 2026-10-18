# Willowmere local art

The PNG atlases are original art generated for Willowmere with OpenAI image generation. No ripped Nintendo game graphics or third-party art packs are included. They load from this folder without an art CDN.

## Character actions

`hero-walk.png`, `hero-run.png`, and `hero-attack.png` each contain eight columns and four rows, ordered south/east/north/west. This gives 96 distinct source frames. Source bounds, foot pivots, and per-action scales are defined in `hero-frames.js`; update that file if replacing these sheets. The attack sprites include the held sword, body rotation and follow-through. The sheets were generated using Willowmere's previous original traveler as the design reference.

## Environment and effects

- `grass.png`: 2×2 muted meadow ground atlas.
- `terrain.png`: 4×4 legacy grass / dirt / water / cliff atlas. Grass now uses the separate grass sheet.
- `village.png`: 4×4 trees; legacy cottages and well; rock/bench/sign/reeds; bridge/fence/flowers/stump. Buildings and many props now use town.png.
- `town.png`: reference-inspired bell arch, flags, planters, rails, buildings, fountain, cobblestones and stone details. Its source rectangles are in `townFrames` in art.js to handle uneven padding.
- `combat.png`: 4×4 green slime frames, purple slime frames, hit effects and legacy sword icons. The separate sword icons are superseded by the character attack sheet.

Canvas draws image source rectangles with smoothing disabled. Ground is cached; water scrolls and animates. Tree sprite strips sway, and the upper part of the walk/run sprite shifts subtly with wind. These are visual wind effects, not a separately rigged hair animation.

## Font

`fonts/VT323-Regular.ttf` is the VT323 typeface from https://github.com/google/fonts/tree/main/ofl/vt323, distributed under the included SIL Open Font License 1.1 in `fonts/OFL.txt`.

Keep this entire folder with the game when downloading. Missing required images produce a visible loading error.
