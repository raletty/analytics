'use strict';

let players;
let yardRows;
let tdRows;
let showYards = true;
const filename = 'playerRushes2yds.csv';

// load player data
d3.csv(filename, function(data) {
  // player names
  players = _u.uniq(data.map(function(d) { return d['Player']; }));

  // yardage statistics for player
  yardRows = data.map(function(d) {
    const player = d['Player'];
    const range = d['Range'];
    const numRushes = +d['Num Rushes'];
    const avgRush = +d['Average Rush'];
    const nAvgRush = +d['Normalized Average Rush'];
    const nNumRushes = +d['Normalized Num Rushes'];
    return { 'player': player, 'range': range, 'avgRush': avgRush, 'numRushes': numRushes, 'nAvgRush': nAvgRush, 'nNumRushes': nNumRushes };
  });

  // touchdown statistics for player
  tdRows = data.map(function(d) {
    const player = d['Player'];
    const range = d['Range'];
    const numRushes = +d['Num Rushes'];
    const numTds = +d['Num TDs'];
    const nTdRate = +d['Normalized TD Rate'];
    const nNumTds = +d['Normalized Num TDs'];
    return { 'player': player, 'range': range, 'numRushes': numRushes, 'numTds': numTds, 'nTdRate': nTdRate, 'nNumTds': nNumTds };
  });

  displayPlayersList(players);
});

/**
 * entrypoint into dropdown change:
 * - get name of selected player
 * - select yardage or touchdown data depending on switch
 * - filter data for only selected player data
 */
const loadPlayerSignature = function() {
  const player = document.getElementById('selectPlayer').value;
  const data = showYards ? yardRows : tdRows;
  const playerData = _u.filter(data, { 'player': player });
  console.log('loading signature for player: ', player);
  console.log('playerData', playerData);

  // main function to display signature!
  displayPlayerSignature(playerData, player);
}
