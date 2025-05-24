import pymongo
import os
from dotenv import load_dotenv
load_dotenv()

class MongoConnection:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance.client = pymongo.MongoClient(os.getenv("MONGODB_CONNECTION", default=""))
            cls._instance.db = cls._instance.client["users"]
        return cls._instance

    def get_db(self):
        return self._instance.db
