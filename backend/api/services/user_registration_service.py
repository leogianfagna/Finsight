from repositories.user_repository import UserRepository

class UserRegistrationService:

    @staticmethod
    def register_user(full_name, username, password, cpf):
        if UserRepository.find_by_username(username):
            return {"success": False, "message": "Username already in use"}
        UserRepository.insert_user(full_name, username, password, cpf)
        return {"success": True, "message": "User registered successfully"}
    
    
