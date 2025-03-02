from django.http import JsonResponse
from .models import User

def add_user(request):
    username = request.GET.get('username')
    password = request.GET.get('password')
    
    if username and password:
        User.add_user(username, password)
        return JsonResponse({"message": "User added successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

def get_user_tickers(request):
    username = request.GET.get('username')
    if username:
        tickers = User.get_user_tickers(username)
        print(tickers)

        if tickers is not None:
            return JsonResponse({"username": username, "tickers": tickers})
        else:
            return JsonResponse({"message": "User not found"}, status=404)
    else:
        return JsonResponse({"message": "Username is required"}, status=400)

def get_all_users(request):
    # Recuperando todos os usuários como uma lista
    users = User.get_all_users()
    
    # Convertendo o cursor para uma lista de documentos
    user_list = []
    for user in users:
        user_list.append({"username": user.get("username"), "password": user.get("password")})
    
    return JsonResponse(user_list, safe=False)

def update_user(request):
    username = request.GET.get('username')
    password = request.GET.get('password')
    
    if username and password:
        User.update_user(username, password)
        return JsonResponse({"message": "User updated successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)
    
def update_user_ticker(request):
    username = request.GET.get('username')
    ticker = request.GET.get('ticker')
    
    if username and ticker:
        User.add_user_ticker(username, ticker)
        return JsonResponse({"message": "User updated successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)

def delete_user(request):
    username = request.GET.get('username')
    
    if username:
        User.delete_user(username)
        return JsonResponse({"message": "User deleted successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)
