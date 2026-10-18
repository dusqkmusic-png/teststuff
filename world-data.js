"use strict";

// WORLD CONTENT — names, dialogue and object positions.
// The map is 1600 pixels wide and 1216 pixels tall.
// x grows toward the right; y grows toward the bottom. See EDITING.md.
// Area names shown above the game.
const AREA_NAMES = {
  town: "Willowmere Bell Square",
  meadow: "Willowmere Meadow",
  garden: "Juniper Gardens",
  river: "The Willow River",
};

const TOWN_BOUNDS = { left: 448, right: 1016, top: 352, bottom: 832 };

// Enemy fields automatically create the number of enemies set in settings.js.
// Keep these outside the town and river. The existing paths connect both fields.
const fields = [
  { name: "Westwind Fields", x: 80, y: 430, w: 320, h: 430 },
  { name: "Eastbank Fields", x: 1260, y: 430, w: 270, h: 430 },
];

// Building position, footprint width/height, and interaction name.
// Building artwork follows this list order: pavilion, shop, cottage.
const houses = [
  { x: 490, y: 450, w: 112, h: 96, roof: "#af6550", name: "Juniper Cottage" },
  {
    x: 845,
    y: 410,
    w: 112,
    h: 96,
    roof: "#6b8191",
    name: "The Riverside House",
  },
  {
    x: 535,
    y: 245,
    w: 112,
    h: 96,
    roof: "#9c7259",
    name: "The Gardener’s Home",
  },
];

// Nearby E-key interactions. Add heals: true to make a healing spot.
const objects = [
  {
    x: 760,
    y: 571,
    text: "The old village bell. Its bronze catches the light above the square.",
  },
  { x: 865, y: 760, text: "Clear water tumbles softly into the fountain." },
  {
    x: 700,
    y: 650,
    text: "Willowmere Village • North: gardens. East: river. West and across the east bridge: enemy fields. J / Z swings your sword.",
  },
  {
    x: 675,
    y: 740,
    heals: true,
    text: "You sit for a moment. The world can wait.",
  },
  { x: 1000, y: 650, text: "The water carries the day slowly downstream." },
  { x: 590, y: 570, text: "Someone has been tending these flowers with care." },
];

// Visible town props: atlas column/row, position, width and height.
// Moving a solid prop also requires moving its footprint in townObstacles below.
const townScenery = [
  { col: 0, row: 0, x: 686, y: 392, w: 148, h: 166 },
  { col: 1, row: 0, x: 625, y: 584, w: 35, h: 70 },
  { col: 1, row: 0, x: 871, y: 584, w: 35, h: 70 },
  { col: 3, row: 0, x: 510, y: 791, w: 124, h: 41 },
  { col: 3, row: 0, x: 877, y: 791, w: 124, h: 41 },
  { col: 3, row: 3, x: 668, y: 795, w: 34, h: 62 },
  { col: 3, row: 3, x: 818, y: 795, w: 34, h: 62 },
];

// Solid footprints for planters, fountain, bench, bell columns, and rails.
// Keep the central route around x = 760 and the east/west path at y = 650 open.
const townObstacles = [
  { x: 534, y: 585, w: 128, h: 13 },
  { x: 850, y: 585, w: 128, h: 13 },
  { x: 845, y: 723, w: 40, h: 14 },
  { x: 650, y: 737, w: 52, h: 9 },
  { x: 690, y: 529, w: 23, h: 21 },
  { x: 806, y: 529, w: 23, h: 21 },
  { x: 510, y: 814, w: 124, h: 12 },
  { x: 877, y: 814, w: 124, h: 12 },
  { x: 673, y: 838, w: 22, h: 14 },
  { x: 824, y: 838, w: 22, h: 14 },
];

// Replace art using the same filenames/layout, or change these paths.
// Atlas frame rectangles remain in art.js and hero-frames.js (see assets/README.md).
const ART_FILES = {
  terrain: "assets/terrain.png",
  village: "assets/village.png",
  combat: "assets/combat.png",
  grass: "assets/grass.png",
  town: "assets/town.png",
  "hero-walk": "assets/hero-walk.png",
  "hero-run": "assets/hero-run.png",
  "hero-attack": "assets/hero-attack.png",
};
