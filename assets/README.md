# Willowmere artwork — version 13

All active graphics live in `storybook/`. The previous pixel-art files have been removed. These PNGs were generated with OpenAI image generation for this prototype, using the supplied screenshots as visual references. They are not commissioned human artwork. No ripped game sprites are bundled. Keep provenance and license records when replacing assets.

| File | Layout / purpose |
| --- | --- |
| ground.png | 2 × 2 painted grass, sand, moss, upland textures |
| village.png | 4 × 4 trees, cottages, rocks, bench, sign, reeds, bridge, fence, flowers, stump |
| town.png | 4 × 4 arch, banners, planters, rails; houses; four fountain frames; stone props |
| regions.png | 4 × 4 woodland ground, nature props, water, upland cliff textures |
| combat.png | 4 × 4 green slimes, plum slimes, impact puffs, sword studies |
| hero-walk.png | 8 × 4 south/east/north/west walking poses |
| hero-run.png | 8 × 4 south/east/north/west running poses |
| hero-attack.png | 8 × 4 south/east/north/west sword poses |
| portrait.png | Traveler dialogue portrait |

The scenery atlas cells have uneven padding: exact source rectangles are in `painted-frames.js`. Character rectangles and foot pivots are in `hero-frames.js`. Replace these metadata tables if changing sheet dimensions or layout. East locomotion is mirrored for west; attack uses all four direction rows. Swords are included in attack poses. Terrain patterns are mirrored and blended at runtime; trees sway as whole sprites. This is painted 2D artwork with depth sorting, not a 3D scene.

`fonts/Alegreya.ttf` comes from https://github.com/google/fonts/tree/main/ofl/alegreya and is distributed under the included SIL Open Font License in `fonts/Alegreya-OFL.txt`. Weather attribution is in the main README.

Version 13 reuses these painted atlases with an oblique renderer. Upright sprites retain their proportions; ground textures, water and bridges follow the foreshortened floor. Cottages display taller and wider, with their base aligned to the solid footprint. No new generated images were needed for this perspective change.
