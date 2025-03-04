import yfinance as yf
import pandas as pd

"""
# Exemplo com a ação VALE3
acao = "VALE3.SA"
ticker = yf.Ticker(acao)

# Obtém o histórico de dividendos
historico_dividendos = ticker.dividends
print(ticker.history(period="2d"))
"""

"""
As requisições HTTP só aceitam JSON, portanto, apenas retornar objetos convertidos para esse tipo
"""

def fetch_dividend_history(ticker_name):
    ticker = yf.Ticker(ticker_name)
    dividend_history = ticker.dividends
    result_as_json = dividend_history.to_json(orient='index')
    return result_as_json
