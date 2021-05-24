# python3
from bs4 import BeautifulSoup
import pandas as pd
import requests

nba_teams = [
    'Atlanta Hawks', 'Boston Celtics', 'Brooklyn Nets', 'New Jersey Nets',
    'Charlotte Hornets', 'Charlotte Bobcats', 'Chicago Bulls', 'Cleveland Cavaliers',
    'Dallas Mavericks', 'Denver Nuggets', 'Detroit Pistons', 'Golden State Warriors',
    'Houston Rockets', 'Indiana Pacers', 'Los Angeles Clippers', 'Los Angeles Lakers',
    'Memphis Grizzlies', 'Vancouver Grizzlies', 'Miami Heat', 'Milwaukee Bucks',
    'Minnesota Timberwolves', 'New Orleans Pelicans', 'New Orleans Hornets', 'New York Knicks',
    'Oklahoma City Thunder', 'Seattle SuperSonics', 'Orlando Magic', 'Philadelphia 76ers',
    'Phoenix Suns', 'Portland Trail Blazers', 'Sacramento Kings', 'San Antonio Spurs',
    'Toronto Raptors', 'Utah Jazz', 'Washington Wizards'
]

trade_regex = '.*The (.*?) traded.*to the (.*?) for'
three_team_trade_regex = 'In a 3-team trade, the (.*?) traded.*?to the (.*?);.*?the (.*?) traded.*?to the (.*?);.*?the (.*?) traded.*?to the'
total_regex = r'{}|{}'.format(trade_regex, three_team_trade_regex)

url_template = "https://www.basketball-reference.com/leagues/NBA_{year}_transactions.html"

yearStart = 1980
yearEnd = 2021

total_trades_df = pd.DataFrame()

for year in range(yearStart, yearEnd):
    # parse transaction page into soup
    url = url_template.format(year=year)
    response = requests.get(url)
    soup = BeautifulSoup(response.content, "html.parser")

    # specifically extract transactions and then trades
    transaction_soup = soup.find('ul', attrs={'class': 'page_index'})
    rows = [row.getText() for row in transaction_soup.find_all_next('p')]
    transaction_df = pd.DataFrame(rows, columns=['action'])
    trades_df = transaction_df[transaction_df.action.str.contains('traded')]

    # extract teams involved in trade and append year of trade
    extracted_trades_df = trades_df['action'].str.extractall(total_regex)
    extracted_trades_df['teams'] = extracted_trades_df.apply(
        lambda line: '|'.join(line.dropna().sort_values().unique().astype(str)), axis=1
    )
    extracted_trades_df['year'] = year

    # append trade data to cummulative dataframe
    total_trades_df = total_trades_df.append(extracted_trades_df[['year', 'teams']], ignore_index=True)

total_trades_df.to_csv('extracted_trades.csv', index=False, header=False)
