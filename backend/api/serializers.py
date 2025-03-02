# api/serializers.py
from rest_framework import serializers
from .models import Users

class UserSerializer(serializers.Serializer):
    id = serializers.CharField(read_only=True)
    user = serializers.CharField(max_length=100)
    password = serializers.CharField(max_length=255)

    def to_representation(self, instance):
        return {
            'id': str(instance.id),  # Converta o ObjectId para string
            'user': instance.user,
            'password': instance.password
        }