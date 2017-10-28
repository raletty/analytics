'use strict';

var players;
var yardRows;
var tdRows;
var showYards = true;
// var filename = 'playerRushes5yds.csv';
var filename = 'playerRushes2yds.csv';

d3.csv(filename, function(data) {
  players = _u.uniq(data.map(function(d) { return d['Player']; }));

  yardRows = data.map(function(d) {
    var player = d['Player'];
    var range = d['Range'];
    var numRushes = +d['Num Rushes'];
    var avgRush = +d['Average Rush'];
    var nAvgRush = +d['Normalized Average Rush'];
    var nNumRushes = +d['Normalized Num Rushes'];
    return { 'player': player, 'range': range, 'avgRush': avgRush, 'numRushes': numRushes, 'nAvgRush': nAvgRush, 'nNumRushes': nNumRushes };
  });

  tdRows = data.map(function(d) {
    var player = d['Player'];
    var range = d['Range'];
    var numRushes = +d['Num Rushes'];
    var numTds = +d['Num TDs'];
    var nTdRate = +d['Normalized TD Rate'];
    var nNumTds = +d['Normalized Num TDs'];
    return { 'player': player, 'range': range, 'numRushes': numRushes, 'numTds': numTds, 'nTdRate': nTdRate, 'nNumTds': nNumTds };
  });

  displayPlayersList(players);
});

var loadPlayerSignature = function() {
  var player = document.getElementById("selectPlayer").value;
  console.log("loading signature for player: ", player);

  var data = showYards ? yardRows : tdRows;
  var playerData = _u.filter(data, { 'player': player });
  displayPlayerSignature(playerData);
}

var yards_csv = function() {

  d3.csv(filename, function(data) {
    var playerStats = data.map(function(d) {
      var player = d['Player'];
      var range = d['Range'];
      var avgRush = +d['Normalized Average Rush'];
      var numRushes = +d['Normalized Num Rushes'];
      return {
        'player': player,
        'range': range,
        'avgRush': avgRush,
        'numRushes': numRushes
      };
    });
    signatures(playerStats);
  });

}
