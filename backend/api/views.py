from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from .models import User
from .action import Acao  
import pymongo
from bson import ObjectId 
import pandas as pd
import os
from finance_api.dividends_history import *
from finance_api.ticker_informations import *
from finance_api.math_operations import *
from machine_learning.data_com import previsao_com_ajuste_curva
from django.http import HttpResponse
import io
from matplotlib.backends.backend_agg import FigureCanvasAgg as FigureCanvas
from bson import json_util
import json
from services.user_service import UserService, ExtendedUserService
from repositories.user_repository import UserRepository

# Instanciar o repositório concreto
user_repository = UserRepository()

# Instanciar os serviços injetando o repositório
user_service = UserService(user_repository)
extended_user_service = ExtendedUserService(user_repository)

@csrf_exempt
def add_user(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body.decode('utf-8'))

            full_name = data.get('full_name')
            username = data.get('username')
            password = data.get('password')
            cpf = data.get('cpf')

            if not username or not password:
                return JsonResponse({"message": "Username and password are required"}, status=400)

            result = user_service.register_user(full_name, username, password, cpf)
            status = 201 if result["success"] else 400
            return JsonResponse({"message": result["message"]}, status=status)

        except json.JSONDecodeError:
            return JsonResponse({"message": "Invalid JSON"}, status=400)

    return JsonResponse({"message": "Method not allowed"}, status=405)

def get_all_users(request):
    users = user_service.list_users()
    user_list = [{"id": str(u.get("_id")), "username": u.get("username"), "password": u.get("password")} for u in users]
    return JsonResponse(user_list, safe=False)

@csrf_exempt
def update_user(request):
    username = request.GET.get('username')
    password = request.GET.get('password')

    if not username or not password:
        return JsonResponse({"message": "Invalid data"}, status=400)

    result = user_service.update_user(username, password)
    status = 200 if result["success"] else 404
    return JsonResponse({"message": result["message"]}, status=status)

def delete_user(request):
    username = request.GET.get('username')

    if not username:
        return JsonResponse({"message": "Invalid data"}, status=400)

    result = user_service.delete_user(username)
    status = 200 if result["success"] else 404
    return JsonResponse({"message": result["message"]}, status=status)

def get_user_tickers(request):
    username = request.GET.get('username')
    ticker_type = request.GET.get('ticker_type')

    if not username:
        return JsonResponse({"message": "Username is required"}, status=400)

    tickers = user_service.get_tickers(username, ticker_type)
    if tickers is None:
        return JsonResponse({"message": "User not found"}, status=404)

    return JsonResponse({"username": username, "tickers": tickers})

@csrf_exempt
def add_user_with_contact(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body.decode('utf-8'))

            full_name = data.get('full_name')
            username = data.get('username')
            password = data.get('password')
            cpf = data.get('cpf')
            email = data.get('email')
            phone = data.get('phone')

            if not username or not password:
                return JsonResponse({"message": "Username and password are required"}, status=400)

            result = extended_user_service.register_user_with_contact(full_name, username, password, cpf, email, phone)
            status = 201 if result["success"] else 400
            return JsonResponse({"message": result["message"]}, status=status)

        except json.JSONDecodeError:
            return JsonResponse({"message": "Invalid JSON"}, status=400)

    return JsonResponse({"message": "Method not allowed"}, status=405)

def process_registration(service):
    return service.register_user("Maria", "maria123", "senha", "12345678900")



































    
def add_user_ticker(request):
    if request.method != 'GET':
        return JsonResponse({"message": "Método não permitido"}, status=405)

    username = request.GET.get('username')
    ticker = request.GET.get('ticker')
    quantity = request.GET.get('purchase_quantity')

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
    future_price = request.GET.get('future_price')

    if username and ticker and price and quantity and date:
        result = User.delete_user_ticker(username, ticker, price, quantity, date, future_price)
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

@csrf_exempt
def gerar_grafico_acao(request):
    ticker = request.GET.get("ticker")
    data_com = request.GET.get("data_com")

    if not ticker or not data_com:
        return JsonResponse({"error": "Ticker and data_com are required"}, status=400)

    try:
        fig, ultimo_preco = previsao_com_ajuste_curva(ticker, data_com)
        if fig is None:
            return JsonResponse({"error": "Erro ao gerar previsão."}, status=500)

        csv_dir = os.path.join(os.path.dirname(__file__), '..', '..', 'backend/data')
        csv_dir = os.path.abspath(csv_dir)
        csv_file = os.path.join(csv_dir, f"{ticker.upper()}.csv")

        if not os.path.exists(csv_file):
            return JsonResponse({"error": f"CSV file not found for {ticker}"}, status=404)

        df = pd.read_csv(csv_file, parse_dates=['Date'])
        last_row = df.sort_values(by='Date').iloc[-1]
        preco_atual = round(float(last_row['Close']), 2)

        collection_acoes.update_one(
            {"ticker": ticker},
            {"$set": {
                "ultimo_preco": round(ultimo_preco, 2),
                "preco_atual": preco_atual
            }},
            upsert=True
        )

        buf = io.BytesIO()
        canvas = FigureCanvas(fig)
        canvas.print_png(buf)
        buf.seek(0)

        return HttpResponse(buf.getvalue(), content_type='image/png')

    except Exception as e:
        return JsonResponse({"error": str(e)}, status=500)

@csrf_exempt
def get_acoes(request):
    try:
        acoes = Acao.get_all_acoes()
        return JsonResponse(acoes, safe=False)
    except Exception as e:
        return JsonResponse({"message": str(e)}, status=500)

def get_account_future_balance(request):
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
                total_balance += ticker_list[4] * ticker_list[2]

            total_balance = round(total_balance, 2)

            collection.update_one({"_id": user_id}, {"$set": {"future_balance": total_balance}})

            return JsonResponse({"message": "Data retrieved and saved successfully", "future_balance": total_balance})
        
        except Exception as e:
            return JsonResponse({"message": f"Error calculating balance: {str(e)}"}, status=500)

    else:
        return JsonResponse({"message": "User ID parameter not provided"}, status=400)

@csrf_exempt
def get_acoes_com_valorizacao(request):
    try:
        documentos = collection_acoes.find()

        acoes_valorizadas = []
        for doc in documentos:
            ultimo_preco = doc.get("ultimo_preco")
            preco_atual = doc.get("preco_atual")

            if ultimo_preco is not None and preco_atual is not None:
                diferenca = ultimo_preco - preco_atual
                if diferenca > 0:
                    acoes_valorizadas.append({
                        "ticker": doc.get("ticker"),
                        "data_com": doc.get("data_com"),
                        "ultimo_preco": f"{ultimo_preco:.2f}",
                        "preco_atual": f"{preco_atual:.2f}"
                    })

        response_data = json.loads(json_util.dumps(acoes_valorizadas))
        return JsonResponse(response_data, safe=False, status=200)

    except Exception as e:
        return JsonResponse({"error": str(e)}, status=500)
