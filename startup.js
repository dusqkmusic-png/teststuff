"use strict";

// Friendly error reporting, including a missing comma in a hand-edited settings file.
// Loaded before settings.js so syntax errors are visible instead of leaving a blank game.
let startupFailed = false;
window.addEventListener("error", (event) => {
  if (startupFailed) return;
  startupFailed = true;
  const notice = document.querySelector("#startup-error");
  const file = event.filename ? event.filename.split("/").pop() : "a game file";
  notice.textContent = `The game could not start. Check ${file}${event.lineno ? `, line ${event.lineno}` : ""}.\n${event.message}\nSee EDITING.md for help, then save and refresh.`;
  notice.hidden = false;
});
