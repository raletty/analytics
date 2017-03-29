from urllib2 import urlopen
from bs4 import BeautifulSoup
import pandas as pd
import sys
import re

reload(sys)
sys.setdefaultencoding('utf8')

team_map = {
    "Bears": "CHI", "Bengals": "CIN", "Bills": "BUF", "Broncos": "DEN",
    "Browns": "CLE", "Buccaneers": "TAM", "Colts": "IND", "Cardinals": "ARI",
    "Chargers": "SDG", "Chiefs": "KAN", "Cowboys": "DAL", "Dolphins": "MIA",
    "Eagles": "PHI", "Falcons": "ATL", "Giants": "NYG", "Jaguars": "JAX",
    "Jets": "NYJ", "Lions": "DET", "Packers": "GNB", "Panthers": "CAR",
    "Patriots": "NWE", "Raiders": "OAK", "Redskins": "WAS", "Rams": "STL",
    "Ravens": "BAL", "Saints": "NOR", "Seahawks": "SEA", "Steelers": "PIT",
    "Texans": "HOU", "Titans": "TEN", "Vikings": "MIN", "49ers": "SFO"
}
year = 2016

players_template = "http://www.pro-football-reference.com/years/{year}/rushing.htm"
games_template = "http://www.pro-football-reference.com/players/{player}/rushing-plays/{year}"

players_url     = players_template.format(year=year)
players_html    = urlopen(players_url)
players_soup    = BeautifulSoup(players_html, "lxml")

players_url_regex = re.compile(r'/players/(.*)\.htm', re.I)

rushers_soup = players_soup.find(text="Fmb").findAllNext('a', href=True)
player_strings = [
    re.search(players_url_regex, a['href']).group(1)
    for a in rushers_soup if players_url_regex.match(a['href'])
]

nfl_players = player_strings[:25]

total_df = pd.DataFrame()

for player in nfl_players:
    games_url   = games_template.format(player=player, year=year)
    games_html  = urlopen(games_url)
    games_soup  = BeautifulSoup(games_html, "lxml")

    name = games_soup.find('title').getText().split(" 2016", 1)[0]

    individual_plays = games_soup.find(text="Individual Plays Table").findAllNext('tr')
    data_rows   = individual_plays[1:]

    print "Appending %d rows for player %s" % (len(data_rows), name)
    column_headers = [th.getText() for th in individual_plays[0].findAll('th')]
    column_headers.pop(0)

    game_data = [[td.getText() for td in data_rows[i].findAll('td')] for i in range(len(data_rows))]

    team_df = pd.DataFrame(game_data, columns=column_headers)

    delete_cols = [1, 8, 10, 11, 12]
    team_df.drop(team_df.columns[delete_cols], axis=1, inplace=True)
    team_df["Tm"].replace(team_map, inplace=True)
    team_df.insert(0, "Name", name)

    total_df = total_df.append(team_df, ignore_index=True)

total_df.to_csv("top_25_rushers_2016", index=False)