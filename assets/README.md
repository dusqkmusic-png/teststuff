# Willowmere local art

These original PNG atlases were generated for Willowmere with OpenAI image generation. No Nintendo game graphics or third-party asset packs are included. The game loads these files locally without contacting an art CDN.

Each atlas uses four columns and four rows. Terrain and village source cell sizes are derived from image dimensions. Character source rectangles are recorded in `adventurerFrames` in art.js to account for padding and align feet across the walk cycle; update that metadata if replacing the character sheet.

- `terrain.png`: grass variants, dirt variants, four water frames, cliff variants (one row each).
- `adventurer.png`: four walk frames per facing direction, in south/east/north/west row order.
- `village.png`: trees; cottages and well; rock/bench/sign/reeds; bridge/fence/flowers/stump.

Canvas draws atlas rectangles with smoothing disabled. The terrain cache is built from tile images; moving water uses four image frames and downstream scrolling. Tree sprite strips sway, and the upper portion of the character sprite shifts subtly with wind. These are visual wind effects, rather than separately rigged hair animation.

Keep the complete assets directory when copying or downloading the game. Asset load failures appear in the game rather than silently displaying missing sprites.
