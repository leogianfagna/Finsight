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

    # To-do: add_tickers

    # To-do: get_tickers
    @staticmethod
    def get_user_tickers(username):
        user_data = collection.find_one({"username": username})
        
        if user_data:
            # Retornando os tickers do usuário
            return user_data.get("tickers", [])
        else:
            return None

    @staticmethod
    def get_all_users():
        # Função para recuperar todos os usuários do MongoDB
        return collection.find()

    @staticmethod
    def update_user(username, password):
        # Função para atualizar o usuário no MongoDB
        collection.update_one({'username': username}, {'$set': {'password': password}})

    @staticmethod
    def delete_user(username):
        # Função para deletar um usuário do MongoDB
        collection.delete_one({'username': username})
