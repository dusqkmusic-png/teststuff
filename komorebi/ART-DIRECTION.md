# Reference study and visual rules

## Shared language of the three supplied screenshots

All three depict a stylized 3D world with rounded silhouettes, exaggerated vegetation, textured surfaces, warm outdoor light, ambient fill, and visible cast shadows. Materials convey surface character without photorealistic roughness. Grass, plants and small decorative forms provide detail below the scale of buildings. Character faces and costume shapes are deliberately enlarged for readability.

The camera looks down at an oblique angle: roofs, ground, vertical walls, and character faces remain visible together. The world has substantial physical depth, rather than a stack of flat screen-facing sprites. The reference images do not provide camera metadata; angle and focal length below are starting estimates judged from visible geometry.

## Differences and how they inform this prototype

1. **Title / town image:** wide environmental composition, framed by large foreground vegetation, with roads and water pulling the eye through the town. Its large logo obscures the center, so it is less reliable for gameplay scale. Use it for environmental hierarchy and coherent landscaping.
2. **Party gameplay image:** strongest source for playable camera distance and traversable-space density. Small characters occupy a planted world with raised terrain, clear walking pockets, and foreground occlusion. Use it for the elevated gameplay view and scenery-to-character relationship. Its UI and fantasy objects are not part of this neighborhood milestone.
3. **Close character image:** exposes chibi anatomy, distinct hair masses, clothing layers, boots, backpack, and painted ground detail. It is a close-up, not the intended gameplay zoom. Use it for the temporary character's proportion and material treatment.

## Rules adopted

- Use genuine 3D, rounded organic silhouettes, generous roof overhangs, dimensional trim, porches and domestic details.
- Keep an elevated fixed heading with moderately compressed perspective. The player stays near screen center with a small velocity lead and a short camera catch-up.
- Establish legible paths through dense planted edges; retain one calm, open shared green.
- Favor warm cream, muted teal, terracotta, green foliage and ochre paths. The prototype intentionally moderates the references' neon-like greens to fit the requested nostalgic suburban setting.
- Keep the temporary player small within the environment, with a recognizable head, costume and backpack. Do not substitute a primitive collision shape for the visible player.
- Use soft directional daylight plus ambient fill. Avoid flat debug colors, dark dungeon palettes and excessive UI.
- Repeat the vocabulary of neighboring houses while varying roof colors and pitch; place all objects in continuous coordinates with no visible tile grid.

## Rendering and scale

The prototype uses libGDX ModelBatch, custom generated meshes, original deterministic surface textures, material-based static batching, a depth shadow pass and a nine-tap shadow filter. Tree crowns use continuous irregular surfaces and vertex shading. The fixed afternoon light is isolated in `LightingController`.

The camera has a 36-degree vertical field of view, a heading offset of approximately 27 degrees, and an elevation of approximately 37 degrees above horizontal. The offset is `(12.6, 21.6, 25.2)` meters from a focus point about 0.6 meters above the player. This is restrained perspective, not a strict isometric projection. Visible distance varies with aspect ratio; the default window is 1440 × 900.

The character is about 1.9 meters including the hair silhouette; house footprints are approximately 7.3 × 6.1 meters, with eaves at 3.9 meters. A street, its sidewalks and multiple houses fit in the gameplay view. The walkable neighborhood is approximately 58 × 46 meters, surrounded by decorative planting. The layout is authored rather than generated scatter: random variation is seeded and limited to planted surface detail.

The initial result is an original suburban interpretation of the references. It still uses temporary environment and character art; production-quality painted models and a full anime character remain later art work, subject to visual review of this build.
