from django.contrib.auth.backends import ModelBackend
from .models import User
import logging

logger = logging.getLogger(__name__)

class EmailBackend(ModelBackend):
    def authenticate(self, request, username=None, password=None, **kwargs):
        try:
            logger.info(f"🔍 Intentando autenticar con email: {username}")
            user = User.objects.get(email=username)
            logger.info(f"✅ Usuario encontrado: {user.username} - Activo: {user.activo}")
            
            if user.check_password(password) and user.activo:
                logger.info(f"✅ Contraseña correcta para {user.username}")
                return user
            else:
                logger.warning(f"❌ Contraseña incorrecta o usuario inactivo para {user.username}")
                return None
        except User.DoesNotExist:
            logger.warning(f"❌ No existe usuario con email: {username}")
            return None
        except Exception as e:
            logger.error(f"❌ Error inesperado en autenticación: {str(e)}")
            return None