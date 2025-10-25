from rest_framework import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from .models import User
from django.contrib.auth.hashers import make_password
from django.utils import timezone

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'password', 'role', 'full_name', 
                  'avatar_url', 'created_at', 'updated_at', 'active', 'last_access']
        read_only_fields = ('id', 'created_at', 'updated_at', 'last_access')
        extra_kwargs = {
            'password': {
                'write_only': True,
                'required': False
            }
        }

    def create(self, validated_data):
        validated_data['password'] = make_password(validated_data['password'])
        return super().create(validated_data)
    
    def update(self, instance, validated_data):
        password = validated_data.pop('password', None)
        if password:
            validated_data['password'] = make_password(password)
        return super().update(instance, validated_data)

class CustomTokenObtainPairSerializer(TokenObtainPairSerializer):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        # Reemplazar username por email
        self.fields['email'] = serializers.EmailField()
        if 'username' in self.fields:
            del self.fields['username']
    
    @classmethod
    def get_token(cls, user):
        token = super().get_token(user)
        # Agregar campos personalizados al token
        token['email'] = user.email
        token['username'] = user.username
        token['full_name'] = user.full_name
        token['role'] = user.role
        return token
    
    def validate(self, attrs):
        # Usar email como username para la autenticación
        email = attrs.get('email')
        if email:
            attrs['username'] = email
        data = super().validate(attrs)
        
        # Actualizar último acceso
        user = self.user
        user.last_access = timezone.now()
        user.save(update_fields=['last_access'])
        
        return data