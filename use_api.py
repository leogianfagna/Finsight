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

def get_ticker_price(ticker_name):
    url = f"{base_url}get_ticker_price/"
    params = {'ticker': ticker_name}
    response = requests.get(url, params=params)
    print(f"Ticker Price Response: {response.json()}")

def get_username_dividend_history(username):
    url = f"{base_url}get_username_dividend_history/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"History Dividend User Response: {response.json()}")

def get_mean_price(username):
    url = f"{base_url}get_mean_price/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Mean Price Response: {response.json()}")

def get_account_balance(username):
    url = f"{base_url}get_account_balance/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Data Response: {response.json()}")

if __name__ == "__main__":
    get_account_balance("newbase")