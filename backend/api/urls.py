from django.urls import path
from . import views

urlpatterns = [
    path('add_user/', views.add_user, name='add_user'),
    path('get_all_users/', views.get_all_users, name='get_all_users'),
    path('update_user/', views.update_user, name='update_user'),
    path('delete_user/', views.delete_user, name='delete_user'),
    path('get_user_tickers/', views.get_user_tickers, name='get_user_tickers'),
    path('add_user_ticker/', views.add_user_ticker, name='add_user_ticker'),
    path('delete_user_ticker/', views.delete_user_ticker, name='delete_user_ticker'),
    path('clear_user_tickers/', views.clear_user_tickers, name='clear_user_tickers'),
    path('get_dividend_history/', views.get_dividend_history, name='get_dividend_history'),
    path('get_next_ticker_dividend/', views.get_next_ticker_dividend, name='get_next_ticker_dividend'),
    path('get_next_dividend/', views.get_next_dividend, name='get_next_dividend'),
    path('get_full_name/', views.get_full_name_by_id, name='get_full_name_by_id'),
    path('get_ticker_validation/', views.get_ticker_validation, name='get_ticker_validation'),
    path('get_ticker_price/', views.get_ticker_price, name='get_ticker_price'),
    path('get_username_dividend_history/', views.get_username_dividend_history, name='get_username_dividend_history'),
    path('get_mean_price/', views.get_mean_price, name='get_mean_price'),
    path('get_account_balance/', views.get_account_balance, name='get_account_balance'),
    path('get_username/', views.get_username_by_id, name='get_username_by_id'),
    path('get_balance/', views.get_balance_by_id, name='get_balance_by_id'),
    path('get_future_balance/', views.get_future_balance_by_id, name='get_future_balance_by_id'),
]
