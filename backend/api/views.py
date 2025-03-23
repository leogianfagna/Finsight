from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import User
from finance_api.dividends_history import *
import json
from bson import ObjectId 

# Manipulação de usuários
@csrf_exempt
def add_user(request):
    data = json.loads(request.body)  # Obtém os dados do corpo da requisição JSON
    full_name = data.get("full_name")
    username = data.get("username")   
    password = data.get("password")
    cpf = data.get("cpf")

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
    if username:
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
    
    if username and ticker:
        result = User.add_user_ticker(username, ticker)
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

        all_dividend_dates = []
        print(f"Tickers encontrados: {tickers}")

        for user_added_ticker in tickers:
            ticker_name_formated = user_added_ticker + ".SA"
            next_dividend_date = fetch_next_dividend(ticker_name_formated)

            if next_dividend_date is not None:
                all_dividend_dates.append(next_dividend_date)

        closest_dividend_date = min(all_dividend_dates)
        return JsonResponse({
            "username": username,
            "closest_dividend_date": closest_dividend_date.strftime('%Y-%m-%d')
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
