from repositories.user_repository import UserRepository

class UserDeleteService:
    
    @staticmethod
    def delete_user(username):
        if not UserRepository.find_by_username(username):
            return {"success": False, "message": "User not found"}
        UserRepository.delete_user(username)
        return {"success": True, "message": "User deleted successfully"}
    
    