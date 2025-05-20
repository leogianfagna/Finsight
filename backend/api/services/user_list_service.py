from repositories.user_repository import UserRepository

class UserListService:
    
    @staticmethod
    def list_users():
        return UserRepository.get_all_users()
    
    