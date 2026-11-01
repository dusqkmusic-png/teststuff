# Komorebi — neighborhood visual prototype

A Java 17+ / libGDX 1.13.1 desktop prototype of a peaceful suburban neighborhood. This is the first visual review milestone: one walkable scene, original temporary character, continuous eight-direction movement, collision, a damped elevated camera, and warm afternoon lighting with filtered directional shadows.

## Play

Extract the entire folder before launching. Keep `Neighborhood.jar`, `assets/`, and `lib/` together.

- **Windows:** double-click `Play.bat`.
- **Linux:** run `./Play.sh` (or `sh Play.sh`).
- **macOS:** run `sh Play.command` from Terminal. The launcher supplies the required first-thread JVM option.
- A **64-bit Java 17 or newer runtime** and an OpenGL 2 compatible graphics driver are required. No Python, Gradle or JDK is needed to play the supplied build.
- If `java` is not found, install a Java 17+ runtime, reopen your terminal and run the launcher again.

**WASD / arrow keys:** walk relative to the camera. **R:** reset player and camera. **H:** hide/show the small text overlay. **F12:** save a screenshot in `screenshots/`. **Esc:** exit.

The first view places the player at the entrance to the shared green, facing a residential street. Explore both sidewalks, the garden loop, and accessible yards. Buildings are exterior facades; interiors and climbing porch steps are outside this milestone.

## What to review

Look at `screenshots/neighborhood.png` and `screenshots/garden.png`, both captured from the actual Java renderer. Then judge the moving build: character scale, camera angle, start/stop response, neighborhood density, house treatment, and daytime palette. The game uses original procedural meshes and authored surface textures; the character and foliage are temporary art, not extracted Fantasy Life assets or finished production anime art.

The scene contains six pitched-roof houses with shutters, porch details and window boxes; a gently curved road; sidewalks, curbs and drains; fences with path openings; mailboxes; lamps; a utility pole; planted yards; and a shared green with a flowering tree, benches and a noticeboard. Plants frame the routes, while the lawn remains open enough to walk through.

## Build from source

Python 3 and Java 17+ are sufficient; the Eclipse Java compiler is included in `lib/ecj.jar`.

```sh
python3 build.py
java -cp 'Neighborhood.jar:lib/*' neighborhood.MovementChecks
./Play.sh
```

On Windows, the test classpath is `Neighborhood.jar;lib/*`.

`fetch_dependencies.py` retrieves pinned Maven artifacts and checks them against `dependencies.sha256.json`. Existing release dependencies work offline. `make_assets.py` regenerates deterministic original textures and bitmap typography; it additionally needs Pillow and a DejaVu Sans font at the path named in that script. Regeneration is optional: all generated assets are included.

## Scope and limits

This prototype deliberately stops at the visual milestone. There is no combat, loot, inventory, quest, weather, or full day/night system. `LightingController` owns environment light independently so that future time-of-day changes can be added without rewriting the scene.

Desktop native libraries are included for Linux x64, Windows x64, macOS x64 and macOS ARM64. Execution was verified on Linux x64 with Mesa software OpenGL; the Windows and macOS launchers are supplied but have not been run on those operating systems. There is no bundled Java runtime or native executable installer.

See `ART-DIRECTION.md`, `ARCHITECTURE.md` and `VALIDATION.md` for visual rules, code boundaries, and test evidence.
