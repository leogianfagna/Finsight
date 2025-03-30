# Rotas
Descrição das rotas que podem ser usadas no backend.

## `add_user_ticker`
Adiciona um ativo comprado (exige informações de compra) ou desejado (apenas nome da ação) na lista de ações de um determinado usuário.

### Parâmetros
- **username: string** <br>Nome do usuário proprietário das ações.

- **ticker: string** <br>Nome da ação, exemplo "GGBR4".

- **destination: string** <br>Define se a adição do papel vai para **Lista de desejos** ou para a **Lista de obtidos** do usuário. Ele pode ser `add_to_wishlist` ou `add_obtained`.

- **[obtained_infos]: list** <br>Lista com informações de compra, que será usada caso a ação seja enviada para a **Lista de obtidos**. Ela é composta por [preco, qtd, data_compra].

### Retorno
- **Json:** <br>Mensagem de resultado.

> [!TIP]
> Exemplo de uso:
> ```python
> add_user_ticker("my_user", "PETR3", "add_to_wishlist", [])
> add_user_ticker("my_user", "PETR3", "add_obtained", [5.0, 601, "2025-03-29"])
> ```

## `add_user`
Registra um novo usuário no sistema.

### Parâmetros
- **full_name: string** <br>Nome completo do usuário.

- **username: string** <br>Nome de usuário.

- **password: string** <br>Senha do usuário.

- **cpf: string** <br>CPF do usuário.

### Retorno
- **Json:** <br>Mensagem de sucesso ou erro.

> [!TIP]
> Exemplo de uso:
> ```python
> add_user("Mario Balotelli", "mario123", "minhasenha123", "123.456.789-00")
> ```

## `get_all_users`
Retorna todos os usuários cadastrados no sistema.

### Parâmetros
- Nenhum.

### Retorno
- **Json:** <br>Lista de usuários com id, username e password.

> [!TIP]
> Exemplo de uso:
> ```python
> get_all_users()
> ```

## `update_user`
Atualiza a senha de um usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

- **password: string** <br>Nova senha do usuário.

### Retorno
- **Json:** <br>Mensagem de sucesso ou erro.

> [!TIP]
> Exemplo de uso:
> ```python
> update_user("leo123", "novaSenha123")
> ```

## `delete_user`
Remove um usuário do sistema.

### Parâmetros
- **username: string** <br>Nome do usuário.

### Retorno
- **Json:** <br>Mensagem de sucesso ou erro.

> [!TIP]
> Exemplo de uso:
> ```python
> delete_user("leo123")
> ```

## `get_user_tickers`
Obtém a lista de ações do usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

- **ticker_type: string (opcional)** <br>Define o tipo: `wishlist` para lista de desejos ou outro valor para ações obtidas.

### Retorno
- **Json:** <br>Lista de tickers do usuário. Se for wishlist, vai ser apenas uma lista com o nome das ações. Caso contrário, vai resgatar todo o histórico de compra do usuário.

> [!TIP]
> Exemplo de uso:
> ```python
> get_user_tickers("leo123")
> get_user_tickers("leo123", "wishlist")
> ```

## `delete_user_ticker`
Remove uma ação da lista de um usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

- **ticker: string** <br>Nome do ticker a ser removido.

### Retorno
- **Json:** <br>Mensagem de sucesso ou erro.

> [!TIP]
> Exemplo de uso:
> ```python
> delete_user_ticker("leo123", "ITUB4")
> ```

## `clear_user_tickers`
Remove todas as ações da lista de um usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

### Retorno
- **Json:** <br>Mensagem de sucesso ou erro.

> [!TIP]
> Exemplo de uso:
> ```python
> clear_user_tickers("leo123")
> ```

## `get_dividend_history`
Retorna o histórico de dividendos de uma ação específica.

### Parâmetros
- **ticker: string** <br>Nome da ação (ex: "PETR4.SA").

> [!CAUTION]
> Para ações brasileiras, obrigatório incluir `.SA` ao final.

### Retorno
- **Json:** <br>Mensagem e histórico de dividendos.

> [!TIP]
> Exemplo de uso:
> ```python
> get_dividend_history("ITSA4")
> ```

## `get_username_dividend_history`
Retorna o histórico de dividendos de todas as ações obtidas por um usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

### Retorno
- **Json:** <br>Mensagem e histórico de dividendos.

> [!TIP]
> Exemplo de uso:
> ```python
> get_username_dividend_history("leo123")
> ```

## `get_next_ticker_dividend`
Retorna a próxima data de dividendo de uma ação específica.

### Parâmetros
- **ticker: string** <br>Nome da ação.

### Retorno
- **Json:** <br>Mensagem e data do próximo dividendo.

> [!TIP]
> Exemplo de uso:
> ```python
> get_next_ticker_dividend("BBAS3")
> ```

## `get_next_dividend`
Retorna a data do dividendo mais próxima entre todas as ações obtidas por um usuário. Importante para a tela inicial mostrando qual será o próximo pagamento.

### Parâmetros
- **username: string** <br>Nome do usuário.

### Retorno
- **Json:** <br>Nome do papel e data do dividendo mais próximo.

> [!TIP]
> Exemplo de uso:
> ```python
> get_next_dividend("leo123")
> ```

## `get_full_name_by_id`
Retorna o nome completo de um usuário a partir do seu ID.

### Parâmetros
- **id: string** <br>ID do usuário (ObjectId).

### Retorno
- **Json:** <br>Nome completo do usuário.

> [!TIP]
> Exemplo de uso:
> ```python
> get_full_name_by_id("65fd2e8e0c9a7f0c4f3d5a77")
> ```

## `get_ticker_validation`
Verifica se o ticker informado é válido.

### Parâmetros
- **ticker: string** <br>Nome do ticker.

> [!CAUTION]
> Para ações brasileiras, obrigatório incluir `.SA` ao final.

### Retorno
- **Json:** <br>Booleano indicando se o ticker é válido.

> [!TIP]
> Exemplo de uso:
> ```python
> get_ticker_validation("MGLU3")
> ```

## `get_ticker_price`
Retorna o valor atual de um ticker.

### Parâmetros
- **ticker: string** <br>Nome do ticker.

> [!CAUTION]
> Para ações brasileiras, obrigatório incluir `.SA` ao final.

### Retorno
- **Json:** <br>Preço atual do ticker.

> [!TIP]
> Exemplo de uso:
> ```python
> get_ticker_price("VALE3")
> ```

## `get_mean_price`
Retorna o preço médio ponderado das ações obtidas por um usuário.

### Parâmetros
- **username: string** <br>Nome do usuário.

### Retorno
- **Json:** <br>Dados com o nome da ação e o preço médio de compra dele em seguida, formato de lista.

> [!TIP]
> Exemplo de uso:
> ```python
> get_mean_price("leo123")
> ```