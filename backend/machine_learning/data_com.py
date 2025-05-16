import os
import yfinance as yf
import pandas as pd
import numpy as np
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LinearRegression
import matplotlib
import matplotlib.pyplot as plt
matplotlib.use('Agg')
from django.conf import settings

def obter_curva_media_normalizada(ticker_dividend, ticker_history, count_days=40, total_dates=5):
    curvas_normalizadas = []

    for i in range(1, total_dates + 1):
        ex_date = pd.to_datetime(ticker_dividend.index[-i]).tz_localize(None)  # remove timezone
        valor_provento = ticker_dividend.iloc[-i]

        start_date = ex_date
        end_date = ex_date + pd.Timedelta(days=count_days)
        
        historico = ticker_history[(ticker_history.index.tz_localize(None) > start_date) & 
                                   (ticker_history.index.tz_localize(None) < end_date)]

        if len(historico) < count_days - 15:
            continue

        preco_base = historico.iloc[0]["Close"]
        curva_normalizada = historico["Close"].iloc[:count_days] / preco_base
        curva_normalizada.index = range(len(curva_normalizada))
        curvas_normalizadas.append(curva_normalizada)

    df_curvas = pd.concat(curvas_normalizadas, axis=1).T
    df_curvas = df_curvas.dropna(axis=1)

    media = df_curvas.mean()

    return media


def previsao_com_ajuste_curva(codigo_acao, data_com_str):
    
    nome_arquivo = fr"C:\Users\mateu\OneDrive\Área de Trabalho\PI 5\PI5\PI-5\dados\{codigo_acao}.csv"

    if not os.path.exists(nome_arquivo):
        print(f"Arquivo não encontrado. Baixando os dados de {codigo_acao}...")
        dados = yf.download(codigo_acao, start="2024-01-01", end="2025-12-31")
        dados.to_csv(nome_arquivo)
        print(f"Dados de {codigo_acao} baixados e salvos como {nome_arquivo}.")
    else:
        print(f"Arquivo {nome_arquivo} encontrado. Carregando os dados...")
        dados = pd.read_csv(nome_arquivo, parse_dates=["Date"], index_col="Date")

    data_com = pd.to_datetime(data_com_str).tz_localize(None)  # remove timezone

    ticker_dividend = dados["Dividends"]
    ticker_dividend = ticker_dividend[ticker_dividend > 0]

    if ticker_dividend.empty:
        print("Nenhum dividendo encontrado para calcular a curva média.")
        return None

    curva_media = obter_curva_media_normalizada(ticker_dividend, dados)

    dados.dropna(inplace=True)
    dados['Label'] = dados['Close'].shift(-1)
    dados.dropna(inplace=True)

    X_base = dados[['Open', 'High', 'Low']]
    X_volume = dados[['Volume']]
    y = dados['Label']

    scaler_base = StandardScaler()
    X_base_scaled = scaler_base.fit_transform(X_base)

    scaler_vol = StandardScaler()
    X_vol_scaled = scaler_vol.fit_transform(X_volume)

    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X_base_scaled)

    X_final = np.hstack([X_pca, X_vol_scaled])

    lr = LinearRegression()
    lr.fit(X_final, y)

    ultimo_dia = dados.iloc[-1].copy()
    data_atual = pd.to_datetime(dados.index[-1]).tz_localize(None)  # remove timezone
    datas_previstas = []
    precos_previstos = []

    while data_atual < data_com:
        open_sim = ultimo_dia['Close']
        ultimo_dia['High'] = ultimo_dia['High'] * (1 + np.random.uniform(-0.02, 0.02))
        ultimo_dia['Low'] = ultimo_dia['Low'] * (1 + np.random.uniform(-0.02, 0.02))
        ultimo_dia['Volume'] = ultimo_dia['Volume'] * (1 + np.random.uniform(-0.05, 0.05))

        high_sim = ultimo_dia['High']
        low_sim = ultimo_dia['Low']
        volume_sim = ultimo_dia['Volume']

        X_base_novo = np.array([[open_sim, high_sim, low_sim]])
        X_vol_novo = np.array([[volume_sim]])

        X_base_novo_scaled = scaler_base.transform(X_base_novo)
        X_vol_novo_scaled = scaler_vol.transform(X_vol_novo)

        X_pca_novo = pca.transform(X_base_novo_scaled)
        X_input = np.hstack([X_pca_novo, X_vol_novo_scaled])

        preco_previsto = lr.predict(X_input)[0]

        ultimo_dia['Close'] = preco_previsto
        ultimo_dia['Open'] = open_sim
        ultimo_dia['High'] = high_sim
        ultimo_dia['Low'] = low_sim
        ultimo_dia['Volume'] = volume_sim

        data_atual += pd.Timedelta(days=1)
        datas_previstas.append(data_atual)
        precos_previstos.append(preco_previsto)

    preco_ultimo = precos_previstos[-1] if precos_previstos else dados.iloc[-1]['Close']
    datas_apos = []
    precos_apos = []

    max_dias_curva = len(curva_media)

    for dia in range(1, max_dias_curva):
        data_ajuste = data_com + pd.Timedelta(days=dia)
        fator_ajuste = curva_media.get(dia, 1.0)
        preco_ajustado = preco_ultimo * fator_ajuste

        datas_apos.append(data_ajuste)
        precos_apos.append(preco_ajustado)

    df_previsao_ate_com = pd.DataFrame({'Data': datas_previstas, 'Preco_Previsto': precos_previstos})
    df_previsao_apos_com = pd.DataFrame({'Data': datas_apos, 'Preco_Previsto': precos_apos})

    df_previsao_completa = pd.concat([df_previsao_ate_com, df_previsao_apos_com], ignore_index=True).set_index('Data')

    plt.figure(figsize=(14, 7))
    plt.plot(df_previsao_completa.index, df_previsao_completa['Preco_Previsto'], marker='o', label="Previsão Completa")
    plt.axvline(x=data_com, color='red', linestyle='--', label="Data COM")
    plt.title(f"Previsão com ajuste via curva normalizada para {codigo_acao}")
    plt.xlabel("Data")
    plt.ylabel("Preço previsto")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()

    # Cria diretório se não existir
    # Define o caminho completo do arquivo
    diretorio = os.path.join(settings.MEDIA_ROOT, 'graficos')
    os.makedirs(diretorio, exist_ok=True)  # cria a pasta se não existir

    caminho_imagem = os.path.join(diretorio, f"grafico_{codigo_acao}.png")

    print("Salvando gráfico em:")
    print(caminho_imagem)
    print("Arquivo existe antes de salvar?", os.path.exists(caminho_imagem))

    fig = plt.gcf()  # pega a figura atual
    plt.close()
    return fig
