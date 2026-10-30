# Asterfall 0.3 — Lantern Garden

A focused Java 3D action-RPG presentation and player-feel build. It replaces
core prototype paths before expanding content.

[**Download Windows game**](https://github.com/dusqkmusic-png/teststuff/releases/download/asterfall-v0.3.0/Asterfall-Windows.zip)

Extract the **whole ZIP into a new folder** and run **Asterfall.exe**. Java is
included. Existing saves remain in your user folder and stay compatible.

[Actual movement/combat GIF](https://github.com/dusqkmusic-png/teststuff/releases/download/asterfall-v0.3.0/Asterfall-Feel.gif) ·
[Day screenshot](https://github.com/dusqkmusic-png/teststuff/releases/download/asterfall-v0.3.0/Asterfall-Preview.png) ·
[Night screenshot](https://github.com/dusqkmusic-png/teststuff/releases/download/asterfall-v0.3.0/Asterfall-Night.png)

## Play

WASD moves; left click approaches/attacks; Shift + left click stands and attacks;
right mouse walks; Space dodges; F collects/opens/rests; Q/E use the retained
sword skills; I opens equipment; Esc pauses; **N resets the practice garden**.

One hero, one enemy type, one breakable crate, one chest, one cottage, and a
small grassy environment with actual elevation and a walkable ramp. No new
quests, item definitions, enemy types or progression content.

The close perspective camera, continuous movement controller, attack phases,
pose blending, lighting/compositing pipeline, compact HUD and loot layout are
rebuilt. See [ARCHITECTURE.md](ARCHITECTURE.md) for the inspection findings,
replacement boundaries, validation and rendering limitations.

Local time and live weather (default 11355) remain. Change location in Settings.
Original procedural art and animation; no Dragon Quest models or artwork used.
The Windows launcher is cross-built and needs human Windows/GPU playtesting.

## Development

Java 17+ and Python 3.12. On Linux, install Pillow 12, DejaVu Sans, Xvfb,
x11-xserver-utils and Mesa for graphics checks. Then:

    python fetch_dependencies.py
    python make_assets.py
    python build.py
    java -Duser.home=/tmp/asterfall-unit -jar asterfall.jar --self-test
    xvfb-run -a -s '-screen 0 1440x900x24' java -Duser.home=/tmp/asterfall-combat -cp 'asterfall.jar:lib/*' game.Desktop --combat-regression
    xvfb-run -a -s '-screen 0 1440x900x24' java -Duser.home=/tmp/asterfall-render -cp 'asterfall.jar:lib/*' game.Desktop --smoke
    python make_preview.py
    python package_release.py

Use fresh isolated user.home directories for each test. The self-test writes a
corruption fixture intentionally; never point it at real saves. GitHub Actions
runs these checks and publishes the Windows/source ZIPs with verified dependency
and runtime checksums. The existing Willowmere browser project at repository
root remains separate.
