# Validation

The supplied build was compiled for Java 17 with Eclipse ECJ 3.39.0 and run on OpenJDK 21, Linux x64, using libGDX 1.13.1 / LWJGL3 and Mesa llvmpipe OpenGL.

## Passed

- Compilation of every Java source and packaging of the runnable JAR.
- All **19 deterministic movement/collision assertions**, run through `neighborhood.MovementChecks`.
- Eight-direction normalization, including idle input.
- Equal cardinal and diagonal travel distance.
- Prompt stopping and limited braking drift.
- House-wall penetration prevention and sliding along an obstacle.
- Substepped movement preventing tunneling even for a large displacement.
- Circular obstacle radius, adjacent open ground and world boundary behavior.
- Comparable travel at 30 and 120 simulation updates per second.
- Actual game startup, asset loading, geometry creation, default material shader plus custom shadow-filter compilation, shadow-map rendering and world rendering.
- Two game-rendered PNG captures, inspected at 1440 × 900.
- A **180-frame runtime smoke run**, moving the real player through the rendered scene; completed with **OpenGL error 0** and no exception.

## Commands

```sh
python3 build.py
java -cp 'Neighborhood.jar:lib/*' neighborhood.MovementChecks
./Play.sh --capture
./Play.sh --capture --yard
./Play.sh --smoke
```

The two capture commands exit after saving their screenshots. `--yard` changes the starting position for the second inspection view; it uses the same gameplay camera. These flags are inspection aids, not separate mockups.

## Review limitations

Execution was checked on Linux only. Windows and macOS native jars and launchers are included but require validation on those operating systems. Subjective movement feel and GPU performance still need the user's hands-on review. The scene's temporary procedural art establishes the initial direction and is not a claim of finished production art or exact reproduction of the references.
