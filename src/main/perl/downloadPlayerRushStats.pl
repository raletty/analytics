use LWP::Simple;

@nfl_teams = (
    'ARI','ATL','BAL','BUF',
    'CAR','CHI','CIN','CLE',
    'DAL','DEN','DET','GNB',
    'HOU','IND','JAX','KAN',
    'STL','MIA','MIN','NWE',
    'NOR','NYG','NYJ','OAK',
    'PHI','PIT','SDG','SFO',
    'SEA','TAM','TEN','WAS'
);

@field_teams = (
    'CRD','ATL','RAV','BUF',
    'CAR','CHI','CIN','CLE',
    'DAL','DEN','DET','GNB',
    'HTX','CLT','JAX','KAN',
    'RAM','MIA','MIN','NWE',
    'NOR','NYG','NYJ','RAI',
    'PHI','PIT','SDG','SFO',
    'SEA','TAM','OTI','WAS'
);

@players = ('M/MurrDe00','P/PeteAd01','C/CharJa00','M/McCoLe01','D/DrewMa00','R/RiceRa00','T/TurnMi00','J/JohnCh04','F/FostAr00','J/JackSt00','F/FortMa00','L/LyncMa00','G/GoreFr00');

$baseURL = "http://www.pro-football-reference.com/players/";
$rushURL = "/rushing-plays/";

open (IND, '>', "rush.csv");
print IND "Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed\n";

foreach (@players) {
    
    foreach ($year = 2007; $year <= 2015; $year++) {
	
	$URL = $baseURL . $_ . $rushURL . $year;
	print "$URL\n";
	
	$page = get $URL;
	
	$page =~ /.*data-stat="team" ><a href.*>(.*)<\/a>.*data-stat="g".*/;
	$team = $1;

	$page =~ /<title>(.*) \d/;
	$player = $1;

	print "$player, $team\n";

	while ($page =~ /<tr ><th scope="row" class="left " data-stat="game_date".*data-stat="quarter" >(.*)<\/.*data-stat="qtr_time_remain" >(.*)<\/.*data-stat="down" >(.*)<\/.*data-stat="yds_to_go" >(.*)<\/.*data-stat="location" >(.*)<\/.*data-stat="score" >(.*)<\/.*data-stat="description" >.*<.*data-stat="yards" >(.*)<\/.*data-stat="exp_pts_before".*<\/tr>/g) {
	    print IND "$player,$team,$1,$2,$3,$4,$5,$6,$7\n";
	}

    }

}
close(IND);
