import yfinance as yf

# Exemplo com a ação VALE3
acao = "VALE3.SA"
ticker = yf.Ticker(acao)

# Obtém o histórico de dividendos
historico_dividendos = ticker.dividends
print(ticker.history(period="2d"))
