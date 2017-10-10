'use script';

var w = 900;
var h = 300;

function displayPlayerSignature(playerData) {
  console.log("playerData: ", playerData);

  var sigDistance = d3.scaleLinear()
    .domain([0, 100])
    .range([0, w]);

  var sigHeight = d3.scaleLinear()
    .domain([0, 2])
    .range([h, 0]);

  var minWidth = 1;
  var maxWidth = height / 5;
  var sigWidth = d3.scaleLinear()
    .domain([0, 2])
    .range([minWidth, maxWidth]);



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
  var svg = d3.select("#signature").append("svg")
    .attr("width", width)
    .attr("height", height);

  console.log("svg", svg);

  // var svg = d3.select("#svg")
  //   .attr("width", w + 100)
  //   .attr("height", h + 100);

  // svg.append("svg:rect")
  //   .attr("width", "100%")
  //   .attr("height", "100%")
  //   .attr("stroke", "#000")
  //   .attr("fill", "none");
  //
  // svg.append("svg:g")
  //   .attr("id", "barchart")
  //   .attr("transform", "translate(50,50)");
}
