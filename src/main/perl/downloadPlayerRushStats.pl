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

# 32 Best running backs of the 21st century.
@players = (
    'A/AlexSh00',
    'B/BarbTi00',
    'B/BettJe00',
    'C/CharJa00',
    'D/DillCo00',
    'D/DrewMa00',
    'D/DunnWa00',
    'F/FaulMa00',
    'F/FortMa00',
    'F/FostAr00',
    'G/GeorEd00',
    'G/GoreFr00',
    'G/GreeAh00',
    'H/HolmPr00',
    'J/JackSt00',
    'J/JameEd00',
    'J/JohnCh04',
    'J/JoneTh00',
    'L/LewiJa00',
    'L/LyncMa00',
    'M/MartCu00',
    'M/McAlDe00',
    'M/MurrDe00',
    'M/McCoLe01',
    'P/PeteAd01',
    'P/PortCl00',
    'R/RiceRa00',
    'T/TaylFr00',
    'T/TomlLa00',
    'T/TurnMi00',
    'W/WestBr00',
    'W/WillRi00'
);

$baseURL = "http://www.pro-football-reference.com/players/";
$rushURL = "/rushing-plays/";

open (IND, '>', "21st_century_rushers.csv");
print IND "Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed\n";

foreach (@players) {
    
    foreach ($year = 1994; $year <= 2015; $year++) {
	
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
