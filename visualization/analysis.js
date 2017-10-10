'use strict';

var players;
var yardRows;
var tdRows;
var showYards = true;

d3.csv('normalizedPlayerRushes.csv', function(data) {
  players = _u.uniq(data.map(function(d) { return d['Player']; }));

  yardRows = data.map(function(d) {
    var player = d['Player'];
    var range = d['Range'];
    var avgRush = +d['Normalized Average Rush'];
    var numRushes = +d['Normalized Num Rushes'];
    return { 'player': player, 'range': range, 'avgRush': avgRush, 'numRushes': numRushes };
  });

  tdRows = data.map(function(d) {
    var player = d['Player'];
    var range = d['Range'];
    var tdRate = +d['Normalized TD Rate'];
    var numTds = +d['Normalized Num TDs'];
    return { 'player': player, 'range': range, 'tdRate': tdRate, 'numTds': numTds };
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

  d3.csv('normalizedPlayerRushes.csv', function(data) {
    console.log('playerData: ', data[0]);
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
    console.log('playerData: ', playerStats[0]);
    signatures(playerStats);
  });

}
