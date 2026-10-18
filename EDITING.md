# Start here, Dusqk

You do not need to understand the engine to change how the game feels. Most edits are a number, a sentence, or a color. No build step and no package installation.

## Your first edit: walk faster

1. Extract the entire download into a normal folder. Keep a spare copy before experimenting.
2. Open **settings.js** in a text editor. On Windows, right-click → Open with → Notepad works. VS Code is another option.
3. Find `walkSpeed: 112,` and change it to `walkSpeed: 140,`.
4. Save, open **index.html**, and try walking. If the game is already open, refresh it. Ctrl+F5 clears a stubborn browser cache.
5. Change it back to 112 whenever you like.

Avoid Word or Google Docs: they can add formatting and curly quotation marks. Make sure the editor saves `settings.js`, not `settings.js.txt`. In Windows Explorer, enabling View → Show → File name extensions makes this easier to see.

## Which file should I open?

| I want to… | File | Find |
|---|---|---|
| Walk or run faster | settings.js | player.walkSpeed / runSpeed |
| Have more hearts | settings.js | player.maxHealth |
| Make enemies easier | settings.js | enemies.health / chaseSpeed / contactDamage |
| Remove enemies entirely | settings.js | enemies.enabled |
| Spawn fewer enemies | settings.js | enemies.perField |
| Kill a normal slime in one hit | settings.js | sword.damage |
| Make night darker or lighter | settings.js | lighting.maxNightDarkness |
| Change dawn or dusk | settings.js | lighting's four hours |
| Rename the game or welcome message | settings.js | text |
| Change the clock/weather location | settings.js | clock / weather |
| Rename areas, fields, or buildings | world-data.js | AREA_NAMES / fields / houses |
| Write scenery dialogue | world-data.js | objects |
| Move a building or prop | world-data.js | houses / townScenery / townObstacles |
| Change UI colors | theme.css | the colors at the top |
| Replace commissioned artwork | assets/README.md | image layouts and frame metadata |
| Change the webpage layout | index.html / style.css | advanced edits; normal settings do not need these |

`game.js`, `combat.js`, and `art.js` are the engine. They are formatted and commented, but you can leave them alone while learning. `hero-frames.js` is sprite-coordinate data, not a difficulty settings file.

## Copy these starter changes

**A peaceful world** — under `enemies`:

```js
enabled: false,
```

**Easier battles** — under `enemies`:

```js
perField: 2,
health: 1,
chaseSpeed: 25,
```

**More hearts** — under `player`:

```js
maxHealth: 8,
```

**Darker nights** — under `lighting`:

```js
maxNightDarkness: 0.65,
```

0 means no night shading; 0.40 is gentle, 0.52 is the current default, and 0.65 is darker. The clock still follows real local time. Dawn/dusk hours use a 24-hour clock and must stay in this order: dawn start < dawn end < dusk start < dusk end.

**One-hit sword** — under `sword`:

```js
damage: 2,
```

Normal slimes have 2 health. If you change their health, adjust damage accordingly. A swing still hits an individual enemy only once.

**A different welcome message** — under `text`:

```js
welcome: 'Welcome to Dusqk’s village. Take your time.',
```

Use double quotes if the sentence contains a straight apostrophe: `welcome: "You're home.",`.

**A different panel color** — at the top of `theme.css`:

```css
--panel-top: #44546d;
--panel-bottom: #142037;
```

Try one change at a time so you can judge its effect.

## Small rules that prevent broken edits

- Numbers do not need quotes: `walkSpeed: 140,`.
- Text needs straight quotes: `title: 'Dusqk’s World',`.
- On/off values are `true` and `false`, without quotes.
- Keep the comma at the end of each entry.
- Keep the surrounding `{ }` and `[ ]` brackets.
- Do not paste a new `player` or `enemies` section over the existing one. Change its values.
- Save and refresh after editing. These files are read when the page loads; changes are not saved into a running game.

If an edit breaks the game, a visible error panel points to the file and often the line. Numeric settings also have checks with readable explanations. Undo your last edit or copy the original file back. For a missing image, restore the assets folder. If you run through a local server, browser developer tools can show more detail.

## Moving things around the world

The world is 1600 pixels wide × 1216 pixels tall. `(0, 0)` is the upper-left corner. Larger `x` moves right; larger `y` moves down. The normal starting point is `(768, 660)`.

In `world-data.js`:

- **houses**: change the building's x/y. Its solid footprint and interaction spot follow it. Width and height also affect the artwork size. Artwork follows list order, so keep the existing three building entries while learning.
- **objects**: change a point's x/y and text for an E-key interaction. `heals: true` makes it restore health.
- **fields**: each rectangle has x/y and width/height. The enemy count comes from settings.js. Avoid changing the current field rectangles until you are ready to check paths, trees, and the river.
- **townScenery**: each prop has atlas column/row plus x/y/width/height.
- **townObstacles**: the invisible solid footprints for those props. Move the corresponding footprint when moving a solid prop. These are separate because a tall arch has two solid feet and an open middle.

Keep the main east/west route at `y = 650` and the central north/south route at `x = 760` open. Map layout, water, bridge and path shapes still live in the engine; this is a small editable prototype, not a full map editor.

## Replacing art with human commissions

You can replace the local PNG files without changing the gameplay. Give an artist `assets/README.md`, the current image sheets, and the intended display sizes. Matching filenames, layout, padding and foot pivots is the easiest swap. A new layout requires updating the source rectangles in art.js or hero-frames.js. Keep licenses and credits alongside the new files.

## A quick check after editing

Open the game, walk and run, swing the sword, visit both fields, and try fullscreen. Check the time/weather panels. If you changed art, check all four facing directions. If you changed dialogue or healing spots, walk near them and press E.
