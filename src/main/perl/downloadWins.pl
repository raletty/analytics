use LWP::Simple;

@nfl_teams=('atl', 'rav', 'buf', 'car', 'chi', 'cin', 'cle', 'dal', 'den', 'det', 'gnb', 'htx', 'clt', 'jax', 'kan', 'mia', 'min', 'nwe', 'nor', 'nyg', 'nyj', 'rai', 'phi', 'pit', 'sdg', 'sfo', 'sea', 'ram', 'tam', 'oti', 'was');

$baseURL = "http://www.pro-football-reference.com/teams";
$sepURL = "/";
$endURL = ".htm";

foreach (@nfl_teams) {
    open (IND, '>' . $_ . '.csv');
    for ($year = 1988; $year <= 2014; $year++) {
	$URL = $baseURL . $sepURL . $_ . $sepURL . $year . $endURL;
	print "$URL\n";

	$page = get $URL;

	while($page =~ /.*<p><strong>(.*)<\/strong>, Finished.*\s*.*Expected W-L<\/a>: (.*)\. <p>.*/g) {
	    print "first match: $1, second match: $2\n";
	    print IND "$_,$year,$1,$2\n";
	}
    }
    close(IND);
}
