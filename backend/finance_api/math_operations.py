import pandas as pd

def calculate_mean_price(tickers):
    """
    Retorna o preço médio de compra de cada ação baseado em um histórico completo de compras.

    Args:
        tickers (list): Lista de informações de ação, exemplo:
            example_list = [
                ['GGBR4', '1.05', '100', '2025-03-29'],
                ['GGBR4', '1.11', '30', '2025-03-29'],
                ['GGBR4', '0.9', '5', '2025-03-29'],
                ['GGBR4', '1.21', '70', '2025-03-29'],
                ['VALE3', '49.0', '123', '2025-03-29'],
                ['PETR3', '5.0', '601', '2025-03-29']
            ]

    Returns:
        serie (pandas): Uma serie que o índice é o ativo e o valor é o preço médio.
    """
    
    df = pd.DataFrame(columns=["total_price", "quantity"])

    # Percorre cada histórico de compra e prepara para inserir em um dataframe auxiliar
    for ticker_info in tickers:
        ticker_info.pop()
        ticker_name = ticker_info.pop(0)

        total_price = float(ticker_info[0]) * int(ticker_info[1])
        ticker_info[0] = total_price

        # Soma total_price e quantity com a ação repetida, se não, apenas adiciona no dataframe
        if ticker_name in df.index:
            old_total_price = df.loc[ticker_name].total_price
            df.loc[ticker_name, "total_price"] = old_total_price + ticker_info[0]

            old_total_quantity = df.loc[ticker_name].quantity
            df.loc[ticker_name, "quantity"] = int(old_total_quantity) + int(ticker_info[1])
        else:
            df.loc[ticker_name] = ticker_info

    # Preparar serie com dados finais, percorrendo o dataframe inteiro e calculando o preço médio
    tickers = []
    mean_prices = []
    for row in df.itertuples():
        tickers.append(row.Index)
        mean_prices.append(round(row.total_price / int(row.quantity), 2))

    return pd.Series(index = tickers, data = mean_prices)