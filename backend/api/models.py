from django.db import models
import pymongo
from django.conf import settings

import os
from dotenv import load_dotenv
load_dotenv()

# Conexão com o MongoDB
client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
db = client['users']
collection = db['users']

class User(models.Model):
    username = models.CharField(max_length=100)
    password = models.CharField(max_length=100)
    tickers = models.JSONField(default=list)

    # Manipulação de usuários

    @staticmethod
    def add_user(username, password):
        user_data = {"username": username, "password": password}
        collection.insert_one(user_data)

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
    def add_user_ticker(username, ticker):
        user_data = collection.find_one({"username": username})
        
        if user_data:
            collection.update_one(
                {"username": username},
                {"$addToSet": {"tickers": ticker}}
            )
            return "Ticker added successfully"
        else:
            return "User not found"
        
    @staticmethod
    def delete_user_ticker(username, ticker):
        user_data = collection.find_one({"username": username})
        
        if user_data:
            collection.update_one(
                {"username": username},
                {"$pull": {"tickers": ticker}}
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
            return user_data.get("tickers", [])
        else:
            return None