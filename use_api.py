import requests

base_url = "http://localhost:8000/api/"

def add_user(username, password):
    url = f"{base_url}add_user/"
    params = {'username': username, 'password': password}
    response = requests.get(url, params=params)
    print(f"Add User Response: {response.json()}")

def get_user_tickers(username):
    url = f"{base_url}get_user_tickers/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Ações encontradas: {response.json()}")

def get_all_users():
    url = f"{base_url}get_all_users/"
    response = requests.get(url)
    print(f"All Users: {response.json()}")

def update_user(username, new_password):
    url = f"{base_url}update_user/"
    params = {'username': username, 'password': new_password}
    response = requests.get(url, params=params)
    print(f"Update User Response: {response.json()}")

def add_user_ticker(username, ticker):
    url = f"{base_url}add_user_ticker/"
    params = {'username': username, 'ticker': ticker}
    response = requests.get(url, params=params)
    print(f"Update User Response: {response.json()}")

def delete_user_ticker(username, ticker):
    url = f"{base_url}delete_user_ticker/"
    params = {'username': username, 'ticker': ticker}
    response = requests.get(url, params=params)
    print(f"Update User Response: {response.json()}")

def clear_user_tickers(username):
    url = f"{base_url}clear_user_tickers/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Update User Response: {response.json()}")

def delete_user(username):
    url = f"{base_url}delete_user/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Delete User Response: {response.json()}")

if __name__ == "__main__":
    add_user_ticker("Teste", "GGBR4")
    add_user_ticker("Teste", "PETR3")
    add_user_ticker("Teste", "POMO4")
    clear_user_tickers("Teste")