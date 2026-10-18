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

## Version 11

`hero-side-walk.png` and `hero-side-run.png` each contain eight right-facing phases in a single row. West-facing motion is mirrored at render time. The frames use fixed ground baselines and hair/head pivots recorded in hero-frames.js. They replace the east/west rows during ordinary movement; the existing attack sheet is unchanged. Horizontal gait timing follows traveled distance.

`regions.png` supplies the new maps: four forest/highland ground tiles, pine/oak/reeds/boulder sprites, four lake-water frames, and ruin/sign/bench/cliff cells. Source bounds are in region-art.js. Objects have transparency and ground is tiled into per-area terrain caches. The artwork was generated specifically for these original maps.
