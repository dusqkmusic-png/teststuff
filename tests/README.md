# Browser regression check

From the game directory, start `python3 -m http.server 8000 --bind 127.0.0.1`. In a development environment with Node, Playwright and Chromium installed, run `node tests/projection.cjs` in a second terminal. The game itself needs none of these test dependencies.

Optional environment variables: `GAME_URL` selects another server; `CHROMIUM_PATH` selects the Chromium executable (default `/usr/bin/chromium`). Weather is mocked to keep the check deterministic. The script checks actual Canvas draw transforms, depth ordering, map cameras, continuous elevations, all trail segments, footprints, water and pier collision, sword timing, NY clock hands, weather, fullscreen, map travel and mobile overflow. It fails on browser errors.
