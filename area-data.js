"use strict";

// Separate maps. Coordinates use the same 1600 × 1216 canvas world as town.
// Change names, trails, entry points, and messages here; see EDITING.md.
const AREA_DATA = {
  woods: {
    name: "The Woods",
    subtitle: "Mossy trails & an old forest clearing",
    spawn: { x: 800, y: 1060 },
    seed: 211,
    treeSpacing: 78,
    paths: [
      [
        [800, 1120],
        [800, 930],
        [610, 810],
        [580, 620],
        [780, 500],
        [1000, 350],
        [1000, 160],
      ],
      [
        [610, 810],
        [350, 730],
        [300, 500],
      ],
    ],
    clearings: [
      { x: 1000, y: 300, r: 110 },
      { x: 300, y: 520, r: 105 },
    ],
    interactions: [
      {
        x: 300,
        y: 540,
        heals: true,
        text: "You rest beneath the branches. The woods breathe around you.",
      },
      {
        x: 1000,
        y: 310,
        text: "An old stone marker, softened by moss. The forest has kept its secrets.",
      },
    ],
  },
  lake: {
    name: "The Lake",
    subtitle: "A quiet shoreline & a wooden fishing pier",
    spawn: { x: 800, y: 1060 },
    seed: 317,
    treeSpacing: 150,
    water: { x: 820, y: 570, rx: 430, ry: 330 },
    pier: { x: 755, y: 765, w: 90, h: 208 },
    paths: [
      [
        [800, 1120],
        [800, 970],
        [800, 900],
      ],
      [
        [800, 1030],
        [1180, 960],
        [1360, 720],
        [1330, 450],
        [1130, 200],
      ],
    ],
    clearings: [
      { x: 800, y: 1030, r: 110 },
      { x: 1330, y: 590, r: 95 },
    ],
    interactions: [
      {
        x: 800,
        y: 785,
        text: "The lake laps gently against the pier. Ripples drift into the distance.",
      },
      {
        x: 1320,
        y: 600,
        heals: true,
        text: "You sit beside the lake and let the world slow down.",
      },
    ],
  },
  highlands: {
    name: "The Highlands",
    subtitle: "Winding climbs & wide mountain views",
    spawn: { x: 800, y: 1060 },
    seed: 431,
    treeSpacing: 190,
    paths: [
      [
        [800, 1120],
        [800, 1040],
        [680, 950],
        [680, 660],
        [1040, 570],
        [1040, 250],
        [1250, 180],
      ],
    ],
    clearings: [
      { x: 800, y: 1050, r: 95 },
      { x: 1250, y: 180, r: 100 },
    ],
    ridges: [
      { x: 160, y: 330, w: 1280, h: 54, gapX: 1040, gapWidth: 110 },
      { x: 200, y: 720, w: 1260, h: 54, gapX: 680, gapWidth: 110 },
    ],
    interactions: [
      {
        x: 1250,
        y: 190,
        text: "A wide horizon, a cool wind, and the whole valley below.",
      },
      {
        x: 800,
        y: 1010,
        text: "The trail climbs north. Follow the worn ramps between the ridges.",
      },
    ],
  },
};

// Town trailheads and their return destinations. E opens the route.
const AREA_ROUTES = [
  { from: "town", to: "woods", x: 760, y: 882, label: "The Woods" },
  { from: "town", to: "lake", x: 1240, y: 650, label: "The Lake" },
  { from: "town", to: "highlands", x: 760, y: 335, label: "The Highlands" },
  {
    from: "woods",
    to: "town",
    x: 800,
    y: 1120,
    label: "Return to Town",
    arrival: { x: 760, y: 850 },
  },
  {
    from: "lake",
    to: "town",
    x: 800,
    y: 1120,
    label: "Return to Town",
    arrival: { x: 1220, y: 650 },
  },
  {
    from: "highlands",
    to: "town",
    x: 800,
    y: 1120,
    label: "Return to Town",
    arrival: { x: 760, y: 365 },
  },
];
