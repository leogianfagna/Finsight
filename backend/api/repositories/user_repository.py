from repositories.I_user_repositoy import IUserRepository
import pymongo
import os

client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
db = client['users']
collection = db['users']

class UserRepository(IUserRepository):
    def insert_user(self, full_name, username, password, cpf):
        return collection.insert_one({
            "full_name": full_name,
            "username": username,
            "password": password,
            "cpf": cpf
        })

    def find_by_username(self, username):
        return collection.find_one({"username": username})

    def get_all_users(self):
        return list(collection.find({}))

    def update_password(self, username, password):
        return collection.update_one({"username": username}, {"$set": {"password": password}})

    def delete_user(self, username):
        return collection.delete_one({"username": username})

    def get_user_tickers(self, username):
        user = collection.find_one({"username": username})
        return user.get("tickers", []) if user else None

    def get_user_wishlist_tickers(self, username):
        user = collection.find_one({"username": username})
        return user.get("wishlist", []) if user else None



class ExtendedUserRepository(UserRepository):
    @staticmethod
    def insert_user_with_contact(full_name, username, password, cpf, email, phone):
        return collection.insert_one({
            "full_name": full_name,
            "username": username,
            "password": password,
            "cpf": cpf,
            "email": email,
            "phone": phone
        })
    
