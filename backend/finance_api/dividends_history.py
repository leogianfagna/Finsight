import yfinance as yf
import pandas as pd
from datetime import date

"""
# Exemplo com a ação VALE3
acao = "VALE3.SA"
ticker = yf.Ticker(acao)

# Obtém o histórico de dividendos
historico_dividendos = ticker.dividends
print(ticker.history(period="2d"))
"""

def fetch_dividend_history(ticker_name):
    ticker = yf.Ticker(ticker_name)
    dividend_history = ticker.dividends
    return dividend_history.to_json(orient='index')

def fetch_next_dividend(ticker_name):
    ticker = yf.Ticker(ticker_name)
    calendar = ticker.calendar
    next_ex_date = calendar['Ex-Dividend Date']
    today = date.today()

    date_today_formatted = pd.Timestamp(today).date()
    date_ex_formatted = pd.Timestamp(next_ex_date).date()

    if (date_today_formatted < date_ex_formatted):
        return next_ex_date
    else:
        return None