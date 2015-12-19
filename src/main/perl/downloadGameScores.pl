use LWP::Simple;


$baseURL = "http://www.pro-football-reference.com/years/";
$endURL = "/games.htm";

for ($year = 2000; $year <= 2014; $year++){
    $URL = $baseURL . $year . $endURL;
    print "$URL\n";

    $page = get $URL;

    open (IND, '>'.$year.'.csv');

    while($page =~ /.*<td align="left" ><strong><a href=".*">(.*)<\/a><\/strong><\/td>\s*<td align="right" >(.*)<\/td>\s*<td align="left" ><a href=".*">(.*)<\/a><\/td>\s*<td align="right" ><strong>(\d*)<\/strong><\/td>\s*<td align="right" >(\d*)<\/td>.*/g) {
	@team1 = split(/ /, $1);
	@team2 = split(/ /, $3);
	$city1 = join("", @team1 [0 .. $#team1-1]);
	$city2 = join("", @team2 [0 .. $#team2-1]);
	print IND "$city1,$2,$city2,$4,$5\n";
    }
    close(IND);
}
