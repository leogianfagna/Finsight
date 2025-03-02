from django.db import models
import pymongo
from django.conf import settings

# Conexão com o MongoDB
client = pymongo.MongoClient("mongodb+srv://pi5:8Mt7LufwcOKOzcw8@investia.k9cc5.mongodb.net/?retryWrites=true&w=majority&appName=investia")
db = client['users']
collection = db['users']

class User(models.Model):
    username = models.CharField(max_length=100)
    password = models.CharField(max_length=100)
    tickers = models.JSONField(default=list)

    @staticmethod
    def add_user(username, password):
        # Função para adicionar um usuário no MongoDB
        user_data = {"username": username, "password": password}
        collection.insert_one(user_data)

    @staticmethod
    def add_user_ticker(username, ticker):
        collection.update_one(
            {'username': username},
            {'$addToSet': {'tickers': ticker}}  # $addToSet garante que não há duplicados na lista
    )

    @staticmethod
    def get_user_tickers(username):
        user_data = collection.find_one({"username": username})

        if user_data:
            return user_data.get("tickers", [])
        else:
            return None

    @staticmethod
    def get_all_users():
        return collection.find()

    @staticmethod
    def update_user(username, password):
        collection.update_one({'username': username}, {'$set': {'password': password}})

    @staticmethod
    def delete_user(username):
        collection.delete_one({'username': username})
