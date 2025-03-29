# Para inicialização do servidor
```bash
pip install django djangorestframework pymongo
pip install python-dotenv
pip install mongoengine
pip install requests # para testes no próprio Python
pip install djongo
```

# Rotas

### Adicionar uma ação à um usuário
> `add_user_ticker("username", "ticker", "destination", [obtained_infos])`

Destination define se a adição do papel vai para **Lista de desejos** ou para a **Lista de obtidos** do usuário. Ele pode ser:
- `add_to_wishlist`: Adiciona na lista de desejos.
    - Neste caso, não exige informar as opções de compra. Exemplo: `add_user_ticker("my_user", "PETR3", "add_to_wishlist", [])`
- `add_obtained`: Adiciona na lista de obtidos.
    - Informar as opções de compra. Exemplo: `add_user_ticker("my_user", "PETR3", "add_obtained", [5.0, 601, "2025-03-29"])`

### Resgatar ações do usuário
> `get_user_tickers("username", "ticker_type")`

Ticker_type define se quer resgatar as ações da **Lista de desejos** (vai retornar um array com apenas nomes) ou da **Lista de obtidos** (vai retornar um array de array com informações de compra) do usuário. Ele pode ser:
- `get_user_tickers("username", "wishlist")`: Resgata da lista de desejos.
- `get_user_tickers("username", "obtained")`: Resgata da lista de obtidos.