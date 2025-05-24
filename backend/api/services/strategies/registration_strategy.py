from abc import ABC, abstractmethod

class RegistrationStrategy(ABC):
    @abstractmethod
    def register(self, full_name, username, password, cpf):
        pass

class BasicRegistration(RegistrationStrategy):
    def register(self, full_name, username, password, cpf):
        from services.user_service import UserService
        return UserService.register_user(full_name, username, password, cpf)

class ExtendedRegistration(RegistrationStrategy):
    def register(self, full_name, username, password, cpf):
        from services.user_service import ExtendedUserService
        return ExtendedUserService.register_user_with_contact(full_name, username, password, cpf, "", "")
