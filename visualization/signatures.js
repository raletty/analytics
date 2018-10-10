'use script';

let svg;

/**
 * (called as the page loads)
 * - sets up the drop down list
 * @param players -- player names to display
 */
function displayPlayersList(players) {
  console.log("players", players);
  let select = document.getElementById("selectPlayer");
  for(let i = 0; i < players.length; i++) {
    let elem = document.createElement("option");
    elem.textContent = elem.value = players[i];
    select.appendChild(elem);
  }
}

/**
 * Helper method to attach player data to each number in range.
 * Player data attached: { yards from goal, avg rush, carries, relative performance, name }
 * @param {*} playerData -- stats passed in for player
 * @param {*} player     -- player name (necessary?)
 */
function transformPlayerData(playerData, player) {
  return _u.flatMap(playerData, function(playerDatum) {
    const rangeSplit = playerDatum.range.split('-');
    return _u.
      range(parseInt(rangeSplit[0]), parseInt(rangeSplit[1]) + 1).
      map(function(i) {
        return {
          x: i,
          y: playerDatum.avgRush,
          w: playerDatum.numRushes,
          c: playerDatum.nAvgRush,
          name: player
        };
      });
  });
}

/**
 * The main function; takes in player data and creates the signature:
 * - gathers data for each player per yard number
 * - creates player transition
 * - attaches (with transition) data + gradient to paths using d3 area
 * - create colorizing array based on color value from data
 * - append linear gradient to [area-gradient] using color data
 * @param {*} playerData
 * @param {*} player
 */
function displayPlayerSignature(playerData, player) {
  const data = transformPlayerData(playerData, player);
  const baseTransition = d3.transition().duration(500).ease(d3.easePoly);
  
  let stateAbove = d3.selectAll("#player-path-above");
  let stateBelow = d3.selectAll("#player-path-below");
  
  // const state = d3.selectAll("#player-path");
  // state.attr("class", "player-path-update")
  //     .style("stroke", "#000")
  //     .style("fill", "none")
  //   .transition(baseTransition)
  //     .attr("d", line(data));

  stateBelow.attr("class", "player-update area-below")
    .transition(baseTransition)
      .attr("d", areaBelow(data))
      .style("fill", "url(#area-gradient)");

  stateAbove.attr("class", "player-update area-above")
    .transition(baseTransition)
      .attr("d", areaAbove(data))
      .style("fill", "url(#area-gradient)");

  let colorData = [];
  const stripe = false; // set stripe to true to prevent linear gradient fading
  for (var i = 0; i < data.length; i++) {
    const prevData = data[i - 1];
    const currData = data[i];
    if (stripe && prevData) {
      colorData.push({
        offset: currData.x + "%",
        stopColor: colorScale(prevData.c)
      });
    }
    colorData.push({
      offset: currData.x + "%",
      stopColor: colorScale(currData.c)
    });
  }

  console.log(`Color data: ${JSON.stringify(colorData.slice(1, 10))} for player: ${player}`)

  // generate the linear gradient used by the signature
  svg.append("linearGradient")
    .attr("id", "area-gradient")
    .attr("gradientUnits", "userSpaceOnUse")
    .selectAll("stop")
      .data(colorData)
      .enter().append("stop")
        .attr("offset", function(d) { return d.offset })
        .attr("stop-color", function (d) { return d.stopColor; });

}

/**
 * - selects the [signature] element and appends an SVG
 * - creates a linear gradient with the id [area-gradient]
 * - attaches two paths to the SVG that will encapsulate relevant area
 */
function init() {
  svg = d3.select("#signature")
    .append("svg")
      .attr("width", svgWidth)
      .attr("height", svgHeight)
    .append("g")
      .attr("class", "area-group2");

  // svg.append("linearGradient")
  //   .attr("id", "area-gradient")
  //   .attr("gradientUnits", "userSpaceOnUse");

  svg.append("path").attr("id", "player-path-above")
  svg.append("path").attr("id", "player-path-below");
  // svg.append("path").attr("id", "player-path");
}
