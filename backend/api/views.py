from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import User
import json
from bson import ObjectId 
import pandas as pd

from finance_api.dividends_history import *
from finance_api.ticker_informations import *

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
    username = request.GET.get('username')
    ticker = request.GET.get('ticker')
    destination = request.GET.get('destination')
    purchase_price = request.GET.get('purchase_price')
    purchase_quantity = request.GET.get('purchase_quantity')
    purchase_date = request.GET.get('purchase_date')
    
    if username and ticker and destination:
        purchase_info = [purchase_price, purchase_quantity, purchase_date]
        result = User.add_user_ticker(username, ticker, destination, purchase_info)
        
        if result == "Ticker added successfully":
            return JsonResponse({"message": "Ticker added successfully"})
        else:
            return JsonResponse({"message": "User not found"}, status=404)
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

def delete_user_ticker(request):
    username = request.GET.get('username')
    ticker = request.GET.get('ticker')
    
    if username and ticker:
        result = User.delete_user_ticker(username, ticker)
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
        return JsonResponse({"message": "Data retrieved successfully", "dividends": ticker_data})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)
    
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
    print(f">>>>>>>>>>>>>>>>{request}")
    ticker_name = request.GET.get('ticker')
    print(f">>>>>>>>>>>>>>>>{ticker_name}")

    if ticker_name:
        print("chegou até aqui <<<<<<<<<<<<<<<<<<")
        result = is_ticker_valid(ticker_name)
        print(f"result = {result}")
        return JsonResponse({"message": "Ticker validation successfully", "boolean": result})
    else:
        return JsonResponse({"message": "Ticker name is required"}, status=400)