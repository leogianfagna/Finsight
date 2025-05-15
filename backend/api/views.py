from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import User
import json
import pymongo
from bson import ObjectId 
import pandas as pd
import os

from finance_api.dividends_history import *
from finance_api.ticker_informations import *
from finance_api.math_operations import *


# Conexão com o MongoDB
client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
db = client['users']
collection = db['users']


# Manipulação de usuários
@csrf_exempt
def add_user(request):
    full_name = request.GET.get('full_name')
    username = request.GET.get('username')
    password = request.GET.get('password')
    cpf = request.GET.get('cpf')

    if username and password:
        result = User.add_user(full_name, username, password, cpf)
        
        # Checa se já existe um username com esse nome
        if result == "User registred successfully":
            return JsonResponse({"message": "User registered successfully"})
        else:
            return JsonResponse({"message": result}, status=400)
    else:
        return JsonResponse({"message": "Username and password are required"}, status=400)

def get_all_users(request):
    users = User.get_all_users()
    user_list = []

    for user in users:
        user_list.append({
            "id": str(user.get("_id")),  
            "username": user.get("username"),
            "password": user.get("password")
        })

    return JsonResponse(user_list, safe=False)

@csrf_exempt
def update_user(request):
    username = request.GET.get('username')
    password = request.GET.get('password')
    
    if username and password:
        User.update_user(username, password)
        return JsonResponse({"message": "User updated successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

def delete_user(request):
    username = request.GET.get('username')
    
    if username:
        User.delete_user(username)
        return JsonResponse({"message": "User deleted successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

# Manipulação de papéis na conta do usuário
def get_user_tickers(request):
    username = request.GET.get('username')
    ticker_type = request.GET.get('ticker_type')

    if username:
        if ticker_type == "wishlist":
            tickers = User.get_user_wishlist_tickers(username)
        else:
            tickers = User.get_user_tickers(username)

        if tickers is not None:
            return JsonResponse({"username": username, "tickers": tickers})
        else:
            return JsonResponse({"message": "User not found"}, status=404)
    else:
        return JsonResponse({"message": "Username is required"}, status=400)
    
@csrf_exempt
def add_user_ticker(request):
    if request.method != 'POST':
        return JsonResponse({"message": "Método não permitido"}, status=405)

    try:
        data = json.loads(request.body)
    except json.JSONDecodeError:
        return JsonResponse({"message": "JSON inválido"}, status=400)

    username = data.get('username')
    ticker = data.get('ticker')
    quantity = data.get('purchase_quantity')

    if username and ticker and quantity:
        try:
            quantity = int(quantity)
        except ValueError:
            return JsonResponse({"message": "Quantidade inválida"}, status=400)

        result = User.add_user_ticker(username, ticker, quantity)

        if result == "Ticker added successfully":
            return JsonResponse({"message": result})
        else:
            return JsonResponse({"message": result}, status=404 if "not found" in result.lower() else 400)
    else:
        return JsonResponse({"message": "Parâmetros faltando"}, status=400)

def delete_user_ticker(request):
    username = request.GET.get('username')
    ticker = request.GET.get('ticker')
    price = request.GET.get('price')
    quantity = request.GET.get('quantity')
    date = request.GET.get('date')

    if username and ticker and price and quantity and date:
        result = User.delete_user_ticker(username, ticker, price, quantity, date)
        if result == "Ticker removed successfully":
            return JsonResponse({"message": "Ticker removed successfully"})
        else:
            return JsonResponse({"message": "User not found"}, status=404)
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)


def clear_user_tickers(request):
    username = request.GET.get('username')
    
    if username:
        result = User.clear_user_tickers(username)
        if result == "Tickers cleared successfully":
            return JsonResponse({"message": "Ticker cleared successfully"})
        else:
            return JsonResponse({"message": "User not found"}, status=404)
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

# Manipulação de dados de ações
def get_dividend_history(request):
    ticker_name = request.GET.get('ticker')
    
    if ticker_name:
        ticker_data = fetch_dividend_history(ticker_name)
        return JsonResponse({"message": "Data retrieved successfully", "dividends": ticker_data.to_json(orient='index')})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)
    
def get_username_dividend_history(request):
    username = request.GET.get('username')
    
    if username:
        username_tickers = User.get_user_tickers(username)
        df_dividend_history = pd.DataFrame(columns=["date", "value", "ticker"])
        
        for unique_ticker in username_tickers:
            ticker_name_formatted = unique_ticker[0] + ".SA"
            serie = fetch_dividend_history(ticker_name_formatted)

            df = serie.reset_index()
            df.columns = ["date", "value"]
            df["ticker"] = ticker_name_formatted
            
            df_dividend_history = pd.concat([df_dividend_history, df], ignore_index=True)

        df_dividend_history = df_dividend_history.sort_values(by="date")
        print(df_dividend_history)
        return JsonResponse({"message": "Data retrieved successfully", "history": df_dividend_history.to_json(orient='index')})
    
    else:
        return JsonResponse({"message": "Username is required"}, status=400)

def get_next_ticker_dividend(request):
    ticker_name = request.GET.get('ticker')

    if ticker_name:
        dividend_date = fetch_next_dividend(ticker_name)
        return JsonResponse({"message": "Data retrieved successfully", "next_date": dividend_date})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)
    
def get_next_dividend(request):
    username = request.GET.get('username')

    if username:
        tickers = User.get_user_tickers(username)

        if tickers is None:
            return JsonResponse({"message": "User not found"}, status=404)

        next_dividends = pd.DataFrame(columns=["ticker", "dividend_date"])

        for user_added_ticker in tickers:
            ticker_name_formated = user_added_ticker + ".SA"
            next_dividend_date = fetch_next_dividend(ticker_name_formated)

            if next_dividend_date is not None:
                formatted_infos = [ticker_name_formated, next_dividend_date]
                next_dividends.loc[len(next_dividends)] = formatted_infos

        closest_dividend_index = next_dividends.dividend_date.idxmin()
        closest_dividend_ticker = next_dividends.loc[closest_dividend_index].ticker
        closest_dividend_date = next_dividends.loc[closest_dividend_index].dividend_date
        return JsonResponse({
            "username": username,
            "closest_dividend_date": closest_dividend_date.strftime('%Y-%m-%d'),
            "closest_dividend_ticker": closest_dividend_ticker
        })

    else:
        return JsonResponse({"message": "Username is required"}, status=400)

def get_full_name_by_id(request):
    user_id = request.GET.get('id')
    if not user_id:
        return JsonResponse({"message": "User ID is required"}, status=400)

    try:
        user_id = ObjectId(user_id)
        user = User.get_full_name_by_id(user_id)
        if user:
            return JsonResponse({"id": str(user_id), "full_name": user.get("full_name")})
        else:
            return JsonResponse({"message": "User not found"}, status=404)

    except Exception as e:
        return JsonResponse({"message": str(e)}, status=500)
    
def get_ticker_validation(request):
    ticker_name = request.GET.get('ticker')

    if ticker_name:
        result = is_ticker_valid(ticker_name)
        return JsonResponse({"message": "Ticker validation successfully", "boolean": result})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)
    
def get_ticker_price(request):
    ticker_name = request.GET.get('ticker')

    if ticker_name:
        ticker_price = get_ticker_value(ticker_name)
        return JsonResponse({"message": "Data retrieved successfully", "price": ticker_price})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)
    
def get_mean_price(request):
    username = request.GET.get('username')
    
    if username:
        username_tickers = User.get_user_tickers(username)
        mean_price = calculate_mean_price(username_tickers)
        return JsonResponse({"message": "Data retrieved successfully", "data": mean_price.to_json(orient='index')})
    
    else:
        return JsonResponse({"message": "User not found"}, status=404)
    
def get_account_balance(request):
    user_id = request.GET.get('id')
    
    if user_id:
        try:
            user_id = ObjectId(user_id)
            
            user_data = collection.find_one({"_id": user_id}, {"_id": 1, "username": 1})
            
            if not user_data:
                return JsonResponse({"message": "User not found"}, status=404)

            user = User(id=user_id, username=user_data['username'])

            username = user_data['username']

        except Exception as e:
            return JsonResponse({"message": f"Error retrieving user: {str(e)}"}, status=500)

        try:
            username_tickers = User.get_user_tickers(username) 
            total_balance = 0

            for ticker_list in username_tickers:
                total_balance += ticker_list[1] * ticker_list[2]

            total_balance = round(total_balance, 2)

            collection.update_one({"_id": user_id}, {"$set": {"balance": total_balance}})

            return JsonResponse({"message": "Data retrieved and saved successfully", "balance": total_balance})
        
        except Exception as e:
            return JsonResponse({"message": f"Error calculating balance: {str(e)}"}, status=500)

    else:
        return JsonResponse({"message": "User ID parameter not provided"}, status=400)

def get_username_by_id(request):
    user_id = request.GET.get('id')
    if not user_id:
        return JsonResponse({"message": "User ID is required"}, status=400)

    try:
        user_id = ObjectId(user_id)
        user = User.get_username_by_id(user_id)
        if user:
            return JsonResponse({"id": str(user_id), "username": user.get("username")})
        else:
            return JsonResponse({"message": "User not found"}, status=404)

    except Exception as e:
        return JsonResponse({"message": str(e)}, status=500)
    
def get_balance_by_id(request):
    user_id = request.GET.get('id')
    if not user_id:
        return JsonResponse({"message": "User ID is required"}, status=400)

    try:
        user_id = ObjectId(user_id)
        user = User.get_balance_by_id(user_id)
        if user:
            return JsonResponse({"id": str(user_id), "balance": user.get("balance")})
        else:
            return JsonResponse({"message": "User not found"}, status=404)

    except Exception as e:
        return JsonResponse({"message": str(e)}, status=500)

def get_future_balance_by_id(request):
    user_id = request.GET.get('id')
    if not user_id:
        return JsonResponse({"message": "User ID is required"}, status=400)

    try:
        user_id = ObjectId(user_id)
        user = User.get_future_balance_by_id(user_id)
        if user:
            return JsonResponse({"id": str(user_id), "future_balance": user.get("future_balance")})
        else:
            return JsonResponse({"message": "User not found"}, status=404)

    except Exception as e:
        return JsonResponse({"message": str(e)}, status=500)

