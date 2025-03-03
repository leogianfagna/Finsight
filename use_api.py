import requests

base_url = "http://localhost:8000/api/"

def add_user(full_name, username, password):
    url = f"{base_url}add_user/"
    params = {'full_name': full_name, 'username': username, 'password': password}
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

def get_dividend_history(ticker_name):
    url = f"{base_url}get_dividend_history/"
    params = {'ticker': ticker_name}
    response = requests.get(url, params=params)
    print(f"Ticker Response: {response.json()}")

if __name__ == "__main__":
    get_dividend_history("VALE3.SA")