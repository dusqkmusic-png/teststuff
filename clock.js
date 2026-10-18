"use strict";

// Two traditional clock faces, both driven by the configured timezone.
function setupAnalogClocks() {
  for (const face of document.querySelectorAll("[data-analog-clock]")) {
    let marks = "";
    for (let i = 0; i < 60; i++) {
      const a = (i * Math.PI) / 30,
        r = i % 5 === 0 ? 43 : 46;
      marks += `<line x1="${60 + Math.sin(a) * r}" y1="${60 - Math.cos(a) * r}" x2="${60 + Math.sin(a) * 49}" y2="${60 - Math.cos(a) * 49}" stroke="#81734c" stroke-width="${i % 5 === 0 ? 2 : 1}"/>`;
    }
    face.innerHTML = `<circle cx="60" cy="60" r="58" fill="#5a5136" stroke="#cbb97d" stroke-width="2"/><circle cx="60" cy="60" r="53" fill="#f5e9bd" stroke="#a88f53" stroke-width="2"/>${marks}<g fill="#6a6341" font-family="WillowSerif,Georgia,serif" font-size="12" text-anchor="middle"><text x="60" y="29">12</text><text x="94" y="64">3</text><text x="60" y="102">6</text><text x="26" y="64">9</text></g><line class="hour-hand" x1="60" y1="66" x2="60" y2="34" stroke="#5b6240" stroke-width="5" stroke-linecap="round"/><line class="minute-hand" x1="60" y1="68" x2="60" y2="19" stroke="#5b6240" stroke-width="3" stroke-linecap="round"/><line class="second-hand" x1="60" y1="69" x2="60" y2="17" stroke="#af8151" stroke-width="1.3"/><circle cx="60" cy="60" r="3.5" fill="#d4b867" stroke="#88734b"/>`;
  }
}
function clockHandAngles(date = new Date()) {
  const values = Object.fromEntries(
    new Intl.DateTimeFormat("en-US", {
      timeZone: GAME_SETTINGS.clock.timeZone,
      hourCycle: "h23",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    })
      .formatToParts(date)
      .map((p) => [p.type, p.value]),
  );
  const hour = Number(values.hour),
    minute = Number(values.minute),
    second = Number(values.second);
  return {
    hour: (hour % 12) * 30 + minute * 0.5 + second / 120,
    minute: minute * 6 + second * 0.1,
    second: second * 6,
  };
}
function updateAnalogClocks(date = new Date()) {
  const angles = clockHandAngles(date),
    label = new Intl.DateTimeFormat("en-US", {
      timeZone: GAME_SETTINGS.clock.timeZone,
      hour: "numeric",
      minute: "2-digit",
      second: "2-digit",
      timeZoneName: "short",
    }).format(date);
  for (const face of document.querySelectorAll("[data-analog-clock]")) {
    face.setAttribute(
      "aria-label",
      `${GAME_SETTINGS.clock.locationLabel} time: ${label}`,
    );
    for (const hand of ["hour", "minute", "second"])
      face
        .querySelector("." + hand + "-hand")
        .setAttribute("transform", `rotate(${angles[hand]} 60 60)`);
  }
}
setupAnalogClocks();
