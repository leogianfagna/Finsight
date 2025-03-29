import requests

base_url = "http://localhost:8000/api/"

def add_user(full_name, username, password, cpf):
    url = f"{base_url}add_user/"
    params = {'full_name': full_name, 'username': username, 'password': password, 'cpf': cpf}
    response = requests.get(url, params=params)
    print(f"Add User Response: {response.json()}")

def get_user_tickers(username, ticker_type):
    url = f"{base_url}get_user_tickers/"
    params = {'username': username, 'ticker_type': ticker_type}
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

def add_user_ticker(username, ticker, destination, purchase_info):
    url = f"{base_url}add_user_ticker/"
    purchase_price, purchase_quantity, purchase_date = purchase_info
    params = {
        'username': username,
        'ticker': ticker,
        'destination': destination,
        'purchase_price': purchase_price,
        'purchase_quantity': purchase_quantity,
        'purchase_date': purchase_date
    }
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

def get_next_ticker_dividend(ticker_name):
    url = f"{base_url}get_next_ticker_dividend/"
    params = {'ticker': ticker_name}
    response = requests.get(url, params=params)
    print(f"Response: {response.json()}")

def get_next_dividend(username):
    url = f"{base_url}get_next_dividend/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Response: {response.json()}")

def is_ticker_valid(ticker_name):
    url = f"{base_url}get_ticker_validation/"
    params = {'ticker': ticker_name}
    response = requests.get(url, params=params)
    print(f"Ticker Validation Response: {response.json()}")

if __name__ == "__main__":
    # add_user_ticker("newbase", "PETR3", "add_obtained", [5.0, 601, "2025-03-29"])
    is_ticker_valid("PETR3.SA")