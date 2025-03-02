def get_user_tickers(username):
    url = f"{base_url}get_user_tickers/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Ações encontradas: {response.json()}")