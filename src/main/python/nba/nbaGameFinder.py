from urllib2 import urlopen
from bs4 import BeautifulSoup
import pandas as pd
import sys

reload(sys)
sys.setdefaultencoding('utf8')

nba_teams = [
    'ATL', 'BOS', 'BRK', 'CHO', 'CHI', 'CLE',
    'DAL', 'DEN', 'DET', 'GSW', 'HOU', 'IND',
    'LAC', 'LAL', 'MEM', 'MIA', 'MIL', 'MIN',
    'NOP', 'NYK', 'OKC', 'ORL', 'PHI', 'PHO',
    'POR', 'SAC', 'SAS', 'TOR', 'UTA', 'WAS'
]

url_template = "http://www.basketball-reference.com/teams/{team}/2017/gamelog"

total_df = pd.DataFrame()

for team in nba_teams:
    url         = url_template.format(team=team)
    html        = urlopen(url)
    soup        = BeautifulSoup(html, "lxml")

    data_rows   = soup.findAll('tr')[2:]
    print "Appending %d rows for team %s" % (len(data_rows), team)

    column_headers  = [th.getText() for th in soup.findAll('tr', limit=2)[1].findAll('th')]
    column_headers.pop(0)

    game_data       = [[td.getText() for td in data_rows[i].findAll('td')] for i in range(len(data_rows))]

    team_df = pd.DataFrame(game_data, columns=column_headers)

    team_df.columns.values[2] = "Home"
    delete_cols = [23]
    team_df.drop(team_df.columns[delete_cols], axis=1, inplace=True)
    team_df.dropna(subset=['G'], inplace=True)
    team_df.insert(0, "Team", team)

    total_df = total_df.append(team_df, ignore_index=True)

total_df.to_csv("full_2017_nba_game_scores", index=False)