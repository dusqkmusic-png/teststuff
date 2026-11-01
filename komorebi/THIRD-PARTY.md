# Third-party components

Original prototype Java sources, meshes and generated texture artwork were created for this project. No game assets were copied from the reference screenshots.

The `lib/` folder contains pinned versions of:

- libGDX 1.13.1 and gdx-jnigen-loader 2.5.2 — Apache License 2.0.
- LWJGL 3.3.3 and its native platform modules — BSD licenses, with bundled native library notices.
- Eclipse ECJ 3.39.0 — Eclipse Public License 2.0. This is a build tool, not used while playing.
- JLayer 1.0.1-gdx and JOrbis 0.0.17 — LGPL. Audio is disabled in this prototype; these are backend dependencies.
- The bundled bitmap font is rasterized from DejaVu Sans; see the DejaVu font terms.

Dependency SHA-256 hashes are recorded in `dependencies.sha256.json`. Jars retain their embedded licenses and notices. Extracted available license texts are also provided in `licenses/`. Maven upstream source artifacts are available using the coordinates in `fetch_dependencies.py` (replace `.jar` with `-sources.jar`).
