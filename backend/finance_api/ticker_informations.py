import yfinance as yf

def is_ticker_valid(ticker_name):
    try:
        ticker = yf.Ticker(ticker_name)
        return True if ticker.info else False
    except:
        return False