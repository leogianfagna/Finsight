from repositories.user_repository import UserRepository
from repositories.user_repository import ExtendedUserRepository
from repositories.I_user_repositoy import IUserRepository

class UserService:
    def __init__(self, repository: IUserRepository):
        self.repository = repository

    def register_user(self, full_name, username, password, cpf):
        if self.repository.find_by_username(username):
            return {"success": False, "message": "Username already in use"}
        self.repository.insert_user(full_name, username, password, cpf)
        return {"success": True, "message": "User registered successfully"}

    def list_users(self):
        return self.repository.get_all_users()

    def update_user(self, username, password):
        if not self.repository.find_by_username(username):
            return {"success": False, "message": "User not found"}
        self.repository.update_password(username, password)
        return {"success": True, "message": "User updated successfully"}

    def delete_user(self, username):
        if not self.repository.find_by_username(username):
            return {"success": False, "message": "User not found"}
        self.repository.delete_user(username)
        return {"success": True, "message": "User deleted successfully"}

    def get_tickers(self, username, ticker_type=None):
        if not self.repository.find_by_username(username):
            return None
        if ticker_type == "wishlist":
            return self.repository.get_user_wishlist_tickers(username)
        return self.repository.get_user_tickers(username)



class ExtendedUserService(UserService):
    @staticmethod
    def register_user_with_contact(full_name, username, password, cpf, email, phone):
        if UserRepository.find_by_username(username):
            return {"success": False, "message": "Username already in use"}
        
        ExtendedUserRepository.insert_user_with_contact(full_name, username, password, cpf, email, phone)
        return {"success": True, "message": "User with contact registered successfully"}
