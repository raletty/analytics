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

numRushers = 100
yearStart = 1994
yearEnd = 2017

for year in range(yearStart, yearEnd):
    players_template = "http://www.pro-football-reference.com/years/{year}/rushing.htm"
    games_template = "http://www.pro-football-reference.com/players/{player}/rushing-plays/{year}"

    players_url     = players_template.format(year=year)
    players_html    = urlopen(players_url)
    players_soup    = BeautifulSoup(players_html, "lxml")

    players_url_regex = re.compile(r'/players/(.*)\.htm', re.I)

    player_names_soup = players_soup.find(text="Fmb").findAllNext('a', href=True)
    player_positions_soup = players_soup.find(text="Fmb").findAllNext('td', { "data-stat": "pos" })

    player_names_strings = [
        re.search(players_url_regex, a['href']).group(1)
        for a in player_names_soup if players_url_regex.match(a['href'])
    ]

    player_positions_strings = [
        td.getText()
        for td in player_positions_soup
    ]

    nfl_players = [
        x[0]
        for x in zip(player_names_strings, player_positions_strings) if ('rb' in x[1].lower())
    ][:numRushers]

    total_df = pd.DataFrame()

    for player in nfl_players:
        games_url        = games_template.format(player=player, year=year)
        games_html       = urlopen(games_url)
        games_soup       = BeautifulSoup(games_html, "lxml")

        name             = games_soup.find('title').getText().split(" %d" % year, 1)[0]

        ind_plays_table  = games_soup.find(text="Individual Plays Table")

        if ind_plays_table is None:
            continue

        individual_plays = ind_plays_table.findAllNext('tr')
        data_rows        = individual_plays[1:]

        print "Appending %d rows for player %s in year %d" % (len(data_rows), name, year)
        column_headers   = [th.getText() for th in individual_plays[0].findAll('th')]
        column_headers.pop(0)

        game_data = [
            [
                td.getText()
                for td in data_rows[i].findAll('td')
            ]
            for i in range(len(data_rows))
        ]

        team_df = pd.DataFrame(game_data, columns=column_headers)

        delete_cols = [1, 8, 10, 11, 12]
        team_df.drop(team_df.columns[delete_cols], axis=1, inplace=True)
        team_df["Tm"].replace(team_map, inplace=True)
        team_df.insert(0, "Name", name)

        total_df = total_df.append(team_df, ignore_index=True)

    total_df.to_csv("top_%d_rushers_%d" % (numRushers, year), index=False)
