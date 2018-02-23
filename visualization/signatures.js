'use script';

let svg;

function transformPlayerData(playerData, player) {
  return _u.flatMap(playerData, function(playerDatum) {
    var rangeSplit = playerDatum.range.split('-');
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

function displayPlayerSignature(playerData, player) {

  const data = transformPlayerData(playerData, player);
  const baseTransition = d3.transition().duration(500).ease(d3.easePoly);

  // var state = d3.selectAll("#player-path");

  const stateAbove = d3.selectAll("#player-path-above");
  const stateBelow = d3.selectAll("#player-path-below");

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

function displayPlayersList(players) {
  // setup the drop down list
  console.log("players", players);
  var select = document.getElementById("selectPlayer");
  for(var i = 0; i < players.length; i++) {
    var elem = document.createElement("option");
    elem.textContent = elem.value = players[i];
    select.appendChild(elem);
  }
}

function init() {
  // setup the svg
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

  console.log("svg", svg);
}
