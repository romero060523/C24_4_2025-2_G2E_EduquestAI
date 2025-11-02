"""
Custom password hasher para compatibilidad con Spring Boot
Guarda las contraseñas en formato BCrypt puro (sin prefijo de Django)
"""
from django.contrib.auth.hashers import BasePasswordHasher
from django.utils.crypto import get_random_string
import bcrypt


class SpringBootBCryptPasswordHasher(BasePasswordHasher):
    """
    Hasher personalizado que guarda contraseñas en formato BCrypt puro
    Compatible con Spring Boot BCryptPasswordEncoder
    
    IMPORTANTE: Guarda el hash SIN prefijo de algoritmo para compatibilidad total con Spring Boot
    Django identifica este hasher porque el hash empieza con $2a$, $2b$ o $2y$
    """
    algorithm = "bcrypt_pure"
    library = "bcrypt"
    
    def encode(self, password, salt=None):
        """
        Encode password usando bcrypt directamente
        Retorna el hash con prefijo bcrypt_pure$ para que Django lo reconozca
        IMPORTANTE: Fuerza revisión $2a$ para compatibilidad con jBCrypt de Spring Boot
        """
        bcrypt_module = self._load_library()
        password_bytes = password.encode('utf-8')
        
        # Generar hash BCrypt con revisión $2a$ (compatible con jBCrypt)
        # bcrypt.gensalt() por defecto usa $2b$, pero podemos forzar $2a$
        salt_bytes = bcrypt_module.gensalt(rounds=12, prefix=b'2a')
        hashed = bcrypt_module.hashpw(password_bytes, salt_bytes)
        
        # Retornar con prefijo para que Django lo identifique
        return f"{self.algorithm}${hashed.decode('ascii')}"
    
    def decode(self, encoded):
        """
        Decodifica el hash almacenado
        """
        algorithm, hash_value = encoded.split('$', 1)
        assert algorithm == self.algorithm
        return {
            'algorithm': algorithm,
            'hash': hash_value,
        }
    
    def verify(self, password, encoded):
        """
        Verifica contraseña contra hash BCrypt
        """
        bcrypt_module = self._load_library()
        
        # Extraer el hash puro (sin el prefijo bcrypt_pure$)
        if '$' in encoded and encoded.count('$') >= 4:  # bcrypt_pure$$2b$12$hash
            # Tiene nuestro prefijo, extraer el hash BCrypt puro
            _, bcrypt_hash = encoded.split('$', 1)
        else:
            # Ya es BCrypt puro
            bcrypt_hash = encoded
        
        password_bytes = password.encode('utf-8')
        encoded_bytes = bcrypt_hash.encode('ascii')
        
        try:
            return bcrypt_module.checkpw(password_bytes, encoded_bytes)
        except (ValueError, TypeError):
            return False
    
    def safe_summary(self, encoded):
        """
        Retorna un resumen seguro del hash para debugging
        """
        decoded = self.decode(encoded)
        return {
            'algorithm': decoded['algorithm'],
            'hash': decoded['hash'][:20] + '...' if len(decoded['hash']) > 20 else decoded['hash'],
        }
    
    def must_update(self, encoded):
        """
        Indica si el hash debe actualizarse
        """
        return False


