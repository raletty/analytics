'use script';

var svg;
var svgWidth = 800;
var svgHeight = 400;

// TODO: Make minWidth 0?
var minWidth = 1;
var maxWidth = svgHeight / 4;

function displayPlayerSignature(playerData) {
  svg.selectAll("*").remove();

  var data = _u.flatMap(playerData, function(playerDatum) {
    var rangeSplit = playerDatum.range.split('-');
    return _u.
      range(parseInt(rangeSplit[0]), parseInt(rangeSplit[1]) + 1).
      map(function(i) {
        return {
          x: i,
          y: playerDatum.avgRush,
          w: playerDatum.numRushes,
          c: playerDatum.nAvgRush
        };
      });
  });

  var gArea = svg.append("g").attr("class", "area-group");
  gArea.append("path")
    .datum(data)
    .attr("class", "area area-above")
    .attr("d", areaAbove)
    .style("fill", "url(#area-gradient)");

  gArea.append("path")
    .datum(data)
    .attr("class", "area area-below")
    .attr("d", areaBelow)
    .style("fill", "url(#area-gradient)");

  // var line = d3.line()
  //   .x(function(d) { return sigDistance(d.x); })
  //   .y(function(d) { return sigHeight(d.y); })
  //   .curve(d3.curveBasis);
  //
  // gArea.append("path")
  //   .datum(data)
  //   .attr("d", line)
  //   .style("stroke", "#000")
  //   .style("fill", "none")

  var colorData = [];
  var stripe = false; // set stripe to true to prevent linear gradient fading
  for (var i = 0; i < data.length; i++) {
    var prevData = data[i - 1];
    var currData = data[i];
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

  // generate the linear gradient used by the signature
  gArea.append("linearGradient")
    .attr("id", "area-gradient")
    .attr("gradientUnits", "userSpaceOnUse")
    .attr("y1", 0)
    .attr("y2", 0)
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
  svg = d3.select("#signature").append("svg")
    .attr("width", svgWidth)
    .attr("height", svgHeight);

  console.log("svg", svg);
}
