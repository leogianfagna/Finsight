from django.db import models
import pymongo
from django.conf import settings
from bson import ObjectId
import os
from dotenv import load_dotenv
load_dotenv()

# Conexão com o MongoDB
client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
db = client['users']
collection = db['users']
collection_acoes = db['acoes']

class User(models.Model):
    full_name = models.CharField(max_length=255)
    username = models.CharField(max_length=100)
    password = models.CharField(max_length=100)
    tickers = models.JSONField(default=list)

    # Manipulação de usuários

    @staticmethod
    def add_user(full_name, username, password, cpf):
        user_data = collection.find_one({"username": username})

        if user_data:
            return "Username already in use"
        else:
            user_data = {"full_name": full_name, "username": username, "password": password, "cpf": cpf}
            collection.insert_one(user_data)
            return "User registred successfully"

    @staticmethod
    def get_all_users():
        return collection.find()

    @staticmethod
    def update_user(username, password):
        collection.update_one({'username': username}, {'$set': {'password': password}})

    @staticmethod
    def delete_user(username):
        collection.delete_one({'username': username})


    # Manipulação de papéis na conta do usuário

    @staticmethod
    def add_user_ticker(username, ticker, quantity):
        import pandas as pd
        import os

        user_data = collection.find_one({"username": username})
        if not user_data:
            return "User not found"

        csv_dir = os.path.join(os.path.dirname(__file__), '..', '..', 'dados')
        csv_dir = os.path.abspath(csv_dir)
        csv_file = os.path.join(csv_dir, f"{ticker.upper()}.SA.csv")

        if not os.path.exists(csv_file):
            return f"CSV file for {ticker} not found, {csv_file}"

        try:
            df = pd.read_csv(csv_file, parse_dates=['Date'])
            
            last_date = '2025-05-09'
            row = df[df['Date'] == last_date]

            if row.empty:
                return f"No price data for {last_date} in {ticker}.csv"

            price = round(float(row.iloc[0]['Close']), 2)
            
            date_value = row.iloc[0]['Date']
            if hasattr(date_value, 'strftime'):
                date_str = date_value.strftime('%Y-%m-%d')
            else:
                date_str = str(date_value)

            quantity_int = int(quantity) 

            acao_data = collection_acoes.find_one({"ticker": f"{ticker.upper()}.SA"})
            if not acao_data or 'ultimo_preco' not in acao_data:
                future_price = None
            else:
                future_price = round(float(acao_data['ultimo_preco']), 2)


            obtained_info = [ticker.upper(),price, quantity_int, date_str, future_price]

            collection.update_one(
                {"username": username},
                {"$addToSet": {"obtained_tickers": obtained_info}}
            )
            return "Ticker added successfully"

        except Exception as e:
            return f"Error processing ticker: {str(e)}"

        except Exception as e:
            return f"Error processing ticker: {str(e)}"

    
    @staticmethod
    def delete_user_ticker(username, ticker, price, quantity, date, future_price):
        user_data = collection.find_one({"username": username})
        
        if user_data:
            collection.update_one(
                {"username": username},
                {"$pull": {
                    "obtained_tickers": [ticker, float(price), int(float(quantity)), date, float(future_price)]
                }}
            )
            return "Ticker removed successfully"
        else:
            return "User not found"
        
    @staticmethod
    def clear_user_tickers(username):
        user_data = collection.find_one({"username": username})
        
        if user_data:
            collection.update_one(
                {"username": username},
                {"$unset": {"tickers": ""}}
            )
            return "Tickers cleared successfully"
        else:
            return "User not found"

    @staticmethod
    def get_user_tickers(username):
        user_data = collection.find_one({"username": username})

        if user_data:
            return user_data.get("obtained_tickers", [])
        else:
            return None
        
    @staticmethod
    def get_user_wishlist_tickers(username):
        user_data = collection.find_one({"username": username})

        if user_data:
            return user_data.get("wishlist_tickers", [])
        else:
            return None

    @staticmethod
    def get_full_name_by_id(user_id):
        try:
            user_data = collection.find_one({"_id": ObjectId(user_id)}, {"_id": 1, "full_name": 1})
            return user_data if user_data else None
        except Exception as e:
            return None

    @staticmethod
    def get_username_by_id(user_id):
        try:
            user_data = collection.find_one({"_id": ObjectId(user_id)}, {"_id": 1, "username": 1})
            return user_data if user_data else None
        except Exception as e:
            return None
        
    @staticmethod
    def get_balance_by_id(user_id):
        try:
            user_data = collection.find_one({"_id": ObjectId(user_id)}, {"_id": 1, "balance": 1})
            return user_data if user_data else None
        except Exception as e:
            return None
        
    @staticmethod
    def get_future_balance_by_id(user_id):
        try:
            user_data = collection.find_one({"_id": ObjectId(user_id)}, {"_id": 1, "future_balance": 1})
            return user_data if user_data else None
        except Exception as e:
            return None
