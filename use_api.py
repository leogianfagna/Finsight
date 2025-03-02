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

def delete_user(username):
    url = f"{base_url}delete_user/"
    params = {'username': username}
    response = requests.get(url, params=params)
    print(f"Delete User Response: {response.json()}")

if __name__ == "__main__":
    """
    # Testando as funções
    add_user("Bruno", "aabb")
    add_user("Leticia", "jjg")
    
    print("\nFetching all users:")
    get_all_users()
    
    print("\nUpdating user Bruno's password:")
    update_user("Bruno", "newpassword123")
    
    print("\nFetching all users again:")
    get_all_users()
    
    print("\nDeleting user Leticia:")
    delete_user("Leticia")
    
    print("\nFetching all users after deletion:")
    get_all_users()
    """

    add_user("Teste", "testpass")
    add_user_ticker("Teste", "VALE3")