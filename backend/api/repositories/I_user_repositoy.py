from abc import ABC, abstractmethod

class IUserRepository(ABC):
    @abstractmethod
    def insert_user(self, full_name, username, password, cpf):
        pass

    @abstractmethod
    def find_by_username(self, username):
        pass

    @abstractmethod
    def get_all_users(self):
        pass

    @abstractmethod
    def update_password(self, username, password):
        pass

    @abstractmethod
    def delete_user(self, username):
        pass

    @abstractmethod
    def get_user_tickers(self, username):
        pass

    @abstractmethod
    def get_user_wishlist_tickers(self, username):
        pass
