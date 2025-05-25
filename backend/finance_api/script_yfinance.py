import yfinance as yf
import pandas as pd
import os

# Parâmetros do usuário
TICKERS = ["VALE3.SA", "CMIN3.SA", "BRAP4.SA", "GGBR4.SA", "PETR4.SA", "RECV3.SA", "CMIG4.SA",
           "TAEE11.SA", "CPFE3.SA", "MRFG3.SA", "JBSS3.SA", "VIVT3.SA", "BBSE3.SA", "LEVE3.SA", "POMO4.SA"]
DATA_INICIO = "2020-01-01"
DATA_FIM = "2025-05-25"
DATA_PATH = "backend/data"

# Cria a pasta se não existir
os.makedirs(DATA_PATH, exist_ok=True)

# Função para baixar ou carregar dados
def obter_dados(ticker_str):
    print(f"\n📈 Processando: {ticker_str}")
    try:
        ticker = yf.Ticker(ticker_str)
        df = ticker.history(start=DATA_INICIO, end=DATA_FIM)
        if df.empty:
            raise ValueError("Dados retornados estão vazios.")
        caminho_csv = os.path.join(DATA_PATH, f"{ticker_str}.csv")
        df.to_csv(caminho_csv)
        print(f"✅ Dados de {ticker_str} salvos em CSV.")
    except Exception as e:
        print(f"⚠️ Erro ao baixar dados de {ticker_str}: {e}")
        caminho_csv = os.path.join(DATA_PATH, f"{ticker_str}.csv")
        if os.path.exists(caminho_csv):
            print(f"📂 Carregando dados salvos localmente para {ticker_str}.")
            df = pd.read_csv(caminho_csv, index_col=0, parse_dates=True)
        else:
            print(f"❌ Nenhum dado disponível para {ticker_str}.")
            df = pd.DataFrame()
    return df

# Função para obter dividendos e salvar em CSV
def obter_dividendos(ticker_str):
    print(f"💰 Buscando dividendos: {ticker_str}")
    try:
        ativo = yf.Ticker(ticker_str)
        dividendos = ativo.dividends
        if dividendos.empty:
            print(f"⚠️ Nenhum dividendo registrado para {ticker_str}.")
        else:
            caminho_divs = os.path.join(DATA_PATH, f"{ticker_str}_dividendos.csv")
            dividendos.to_csv(caminho_divs)
            print(f"✅ Dividendos de {ticker_str} salvos.")
        return dividendos
    except Exception as e:
        print(f"❌ Erro ao buscar dividendos de {ticker_str}: {e}")
        return pd.Series()

# Executa para todos os tickers
dados_todos = {}
dividendos_todos = {}

for ticker in TICKERS:
    df = obter_dados(ticker)
    divs = obter_dividendos(ticker)
    dados_todos[ticker] = df
    dividendos_todos[ticker] = divs
    print(df.head())
    print(divs.head())
