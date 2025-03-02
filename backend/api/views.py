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

def delete_user(request):
    username = request.GET.get('username')
    
    if username:
        User.delete_user(username)
        return JsonResponse({"message": "User deleted successfully"})
    else:
        return JsonResponse({"message": "Invalid data"}, status=400)
