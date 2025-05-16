import pymongo
import os
from dotenv import load_dotenv
load_dotenv()

client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
db = client['users']
collection = db['acoes']  

class Acao:
    @staticmethod
    def get_all_acoes():
        acoes = collection.find({}, {"_id": 0, "ticker": 1, "data_com": 1})
        return list(acoes)
