import yfinance as yf

"""
# Exemplo com a ação VALE3
acao = "VALE3.SA"
ticker = yf.Ticker(acao)

# Obtém o histórico de dividendos
historico_dividendos = ticker.dividends
print(ticker.history(period="2d"))
"""

"""
As requisições HTTP só aceitam JSON, portanto foi convertido todos os dados para esse tipo, inclusive os dados do tipo
timestamp para as datas de dividendos
"""

def fetch_dividend_history(ticker_name):
    ticker = yf.Ticker(ticker_name)
    print(ticker)
    dividend_history = ticker.dividends
    
    dividend_history = dividend_history.reset_index()
    dividend_history['Date'] = dividend_history['Date'].dt.strftime('%Y-%m-%d')
    
    return dividend_history.to_dict(orient='records')

