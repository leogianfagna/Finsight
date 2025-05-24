from services.user_service import UserService, ExtendedUserService

class UserFacade:
    @staticmethod
    def register_basic_user(full_name: str, username: str, password: str, cpf: str) -> dict:
        return UserService.register_user(full_name, username, password, cpf)

    @staticmethod
    def register_user_with_contact(full_name: str, username: str, password: str, cpf: str, email: str, phone: str) -> dict:
        return ExtendedUserService.register_user_with_contact(full_name, username, password, cpf, email, phone)

    @staticmethod
    def list_all_users():
        return UserService.list_users()
