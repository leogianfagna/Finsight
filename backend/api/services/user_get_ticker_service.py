from repositories.user_repository import UserRepository

class UserService:

    @staticmethod
    def get_tickers(username, ticker_type=None):
        if not UserRepository.find_by_username(username):
            return None
        if ticker_type == "wishlist":
            return UserRepository.get_user_wishlist_tickers(username)
        return UserRepository.get_user_tickers(username)
    
    