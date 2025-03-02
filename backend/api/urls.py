from django.urls import path
from . import views

urlpatterns = [
    path('add_user/', views.add_user, name='add_user'),
    path('get_all_users/', views.get_all_users, name='get_all_users'),
    path('update_user/', views.update_user, name='update_user'),
    path('delete_user/', views.delete_user, name='delete_user'),
    path('get_user_tickers/', views.get_user_tickers, name='get_user_tickers'),
    path('add_user_ticker/', views.update_user_ticker, name='add_user_ticker'),
]
