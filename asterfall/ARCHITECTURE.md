# Presentation rebuild — 0.3

The architecture was inspected before implementation. Version 0.2 put camera
positioning, direct drawing, input, locomotion, attack timers, spawning and
particles into Asterfall's update/render methods. Models.Puppet calculated
bobbing and limb motion while drawing. The world used one broad flat slab.
HUD widgets and loot names occupied hardcoded rectangles.

## Replaced paths

- **PlayerMotor** owns acceleration, braking, shortest-arc turning, movement
  anticipation, continuous stride, recoil and a time-based eased dodge.
- **CombatController** owns explicit anticipation/contact/recovery phases,
  strike-once semantics, attack lunging, real-time hit-stop and camera impulses.
  Effects are still processed by reverse index: callbacks can append effects
  safely, preserving the fix for the reported level-up crash.
- **Presentation** owns a close 39-degree perspective camera rig, damping,
  movement look-ahead, shake, lighting, shadows and the offscreen render graph.
  Window/UI rendering no longer owns the 3D scene.
- **Models.Puppet** consumes a prepared pose: blended run/idle, stride-based
  limbs, anticipatory sword motion, dodge compression, hurt/recoil and flash.
  Rendering the shadow/depth/color passes does not advance simulation.
- **TestArea** replaces the three-garden world. Its scene has one hero, one
  enemy type/instance, one destructible crate, one chest and one building.
  Raised grass platforms, exposed masonry cliffs and a traversable sloped ramp
  share height sampling with movement and mouse picking. WorldBuilder now
  supplies reusable props, not an unused alternate world generator.
- **UI/LootLayout** replace the normal HUD and unconditional name drawing.
  The bottom gold/teal frame is compact, notes collapse, and loot labels are
  distance-gated and resolved against each other and the hero's silhouette.

## Render graph

1. Directional shadow depth, 2048 square with a softened PCF footprint.
2. Camera-space packed depth, with 24-bit depth testing.
3. Toon material scene: wrapped lighting, rim term, two warm point lights,
   cool night ambient, directional shadows and distance fog.
4. Composite: depth-based screen-space ambient occlusion/contact darkening,
   restrained thresholded bloom, saturation/contrast grading and vignette.
5. UI, ungraded and at window resolution for readable text.

This is a lightweight stylized **toon** pipeline, not a claim of full metallic/
roughness PBR. Screen-space AO cannot see offscreen geometry. The original
procedural models remain a basis for iteration; they are not commissioned
production character assets. The renderer requires OpenGL 2.0 with framebuffer
objects and 24-bit depth renderbuffers. Large windows cap the 3D render target
at 1440 pixels wide; the HUD stays at full window resolution.

## Validation

- 186 behavioral checks for movement, stopping, turning, eased dodge distance,
  attack timing, hit-stop release, terrain height and overlapping loot labels.
- The original 28 data/equipment/save/weather checks remain.
- Actual graphics-context regression: 60 projectile level-up kills, timed melee,
  hit-stop, knockback, traversable ramp, blocked cliff, larger camera framing,
  chest/crate interactions and stable mesh ownership across resets.
- Rendered day/night screenshots and an in-engine movement/attack/dodge GIF.

The motion clip is captured from the game, not a generated concept image.
Windows launcher is cross-built; final subjective feel and Windows/GPU
compatibility still need human playtesting.

## Scope deliberately held

No new item definitions, classes, quests, enemy types, progression systems,
map expansions or menu features. Existing saves, equipment, talents and local
weather remain compatible. Boss/expedition content is not expanded or presented
in this focused build. N reuses the reset control for the practice garden.
