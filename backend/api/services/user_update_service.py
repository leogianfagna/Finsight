from repositories.user_repository import UserRepository

class UserUpdateService:
    
    @staticmethod
    def update_user(username, password):
        if not UserRepository.find_by_username(username):
            return {"success": False, "message": "User not found"}
        UserRepository.update_password(username, password)
        return {"success": True, "message": "User updated successfully"}
    
    