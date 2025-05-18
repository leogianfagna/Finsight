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
    nome_arquivo = fr"backend/data/{codigo_acao}.csv"

    if not os.path.exists(nome_arquivo):
        print(f"Arquivo não encontrado. Baixando os dados de {codigo_acao}...")
        dados = yf.download(codigo_acao, start="2024-01-01", end="2025-12-31")
        dados.to_csv(nome_arquivo)
        print(f"Dados de {codigo_acao} baixados e salvos como {nome_arquivo}.")
    else:
        print(f"Arquivo {nome_arquivo} encontrado. Carregando os dados...")
        dados = pd.read_csv(nome_arquivo, parse_dates=["Date"], index_col="Date")

    # Próxima data-com preparada (timezone removido)
    data_com = pd.to_datetime(data_com_str).tz_localize(None)

    # Preparar histórico de dividendos
    ticker_dividend = dados["Dividends"]
    ticker_dividend = ticker_dividend[ticker_dividend > 0]

    if ticker_dividend.empty:
        print("Nenhum dividendo encontrado para calcular a curva média.")
        return None

    curva_media = obter_curva_media_normalizada(ticker_dividend, dados)

    # Criação do rótulo: fechamento do dia seguinte
    # Criar uma nova coluna chamada "Label" que será o valor de "Close" (nosso rótulo)
    # Método shift() move a coluna. Está movendo tudo para cima pois queremos prever o fechamento do dia seguinte
    # Dropamos a última linha que vira valor nulo (já que subimos uma posição para cima)
    dados.dropna(inplace=True)
    dados['Label'] = dados['Close'].shift(-1)
    dados.dropna(inplace=True)

    # Nossos coeficientes em dataframes
    # X_base e X_volume divididos pois 'Open', 'High' e 'Low' são fortemente correlacionadas e se tornarão uma razão só
    X_base = dados[['Open', 'High', 'Low']]
    X_volume = dados[['Volume']]
    y = dados['Label']

    # Normalizar os dados (da base e volume de forma separada)
    scaler_base = StandardScaler()
    X_base_scaled = scaler_base.fit_transform(X_base)
    scaler_vol = StandardScaler()
    X_vol_scaled = scaler_vol.fit_transform(X_volume)

    # Aplicar PCA apenas em Open, High e Low
    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X_base_scaled)

    # Concatenar os componentes gerados no PCA com o volume padronizado (ele não passa por PCA)
    X_final = np.hstack([X_pca, X_vol_scaled])

    # Treinar modelo de regressão linear com dados históricos (X_final e y), para ser utilizada posteriormente para cada dia
    lr = LinearRegression()
    lr.fit(X_final, y)

    # Projetar os preços diários dos ativos até a data COM
    ultimo_dia = dados.iloc[-1].copy()
    data_atual = pd.to_datetime(dados.index[-1]).tz_localize(None)
    datas_previstas = []
    precos_previstos = []

    # Simular cada dia de mercado. Os parâmetros são valores do dia anterior com uma margem de volatibilidade, que são normalizados
    # e usados na regressão linear já treinada. Esses valores são salvos para a próxima iteração e criar estatísticas mais reais
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

        # Prevê o preço utilizando o modelo de regressão já treinado
        preco_previsto_array = lr.predict(X_input)
        preco_previsto = preco_previsto_array[0]

        ultimo_dia['Close'] = preco_previsto
        ultimo_dia['Open'] = open_sim
        ultimo_dia['High'] = high_sim
        ultimo_dia['Low'] = low_sim
        ultimo_dia['Volume'] = volume_sim

        data_atual += pd.Timedelta(days=1)
        datas_previstas.append(data_atual)
        precos_previstos.append(preco_previsto)

    # Pós data com o preço cai, equivalente ao preço pago de proventos. Essa queda precisa ser debitada do valor do último dia para
    # ser inserida e prever o PÓS DATA COM. Prever com um algoritmo quanto será pago
    df_pgto_dividendo = pd.read_csv(nome_arquivo, parse_dates=["Date"], index_col="Date")
    df_pgto_dividendo = df_pgto_dividendo[~((df_pgto_dividendo['Dividends'] == 0) | (df_pgto_dividendo['Dividends'].isna()))]

    ultimos_5_dividend = df_pgto_dividendo.tail(5).copy()

    # Calcula a porcentagem que o dividendo representa do fechamento
    ultimos_5_dividend["Pct_Dividendo_Close"] = (ultimos_5_dividend["Dividends"] / ultimos_5_dividend["Close"]) * 100
    media_yield_final = ultimos_5_dividend["Pct_Dividendo_Close"].mean()

    # Ajustar no preço de abertura pós data com a diferença desse valor pago de provento
    preco_ultimo = precos_previstos[-1] if precos_previstos else dados.iloc[-1]['Close']
    preco_ultimo = preco_ultimo * ((100 - media_yield_final)/100)

    # Código para gerar df pós data com, resgata o último preço previsto PRÉ DATA COM em "preco_ultimo" como ponto de partida
    # Após esse valor de partida, multiplica ele pelo "fator_ajuste", que é o ponto da curva normalizada, resultando em um ponto no gráfico
    datas_apos = []
    precos_apos = []
    max_dias_curva = len(curva_media)

    for dia in range(1, max_dias_curva):
        dia_previsao = data_com + pd.Timedelta(days=dia)
        fator_ajuste = curva_media.get(dia, 1.0)
        preco_ajustado = preco_ultimo * fator_ajuste

        datas_apos.append(dia_previsao)
        precos_apos.append(preco_ajustado)

    df_previsao_ate_com = pd.DataFrame({'Data': datas_previstas, 'Preco_Previsto': precos_previstos})
    df_previsao_apos_com = pd.DataFrame({'Data': datas_apos, 'Preco_Previsto': precos_apos})
    df_previsao_completa = pd.concat([df_previsao_ate_com, df_previsao_apos_com], ignore_index=True).set_index('Data')

    plt.figure(figsize=(14, 7))
    plt.plot(df_previsao_completa.index, df_previsao_completa['Preco_Previsto'], marker='o', label="Previsão Completa")
    plt.axvline(x=data_com, color='red', linestyle='--', label="Data COM")
    plt.axhline(y=df_previsao_ate_com.iloc[-1]['Preco_Previsto'], color='green', linestyle=':', label="Valor de lucro real")
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
    return fig, preco_ultimo
