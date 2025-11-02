import uuid
from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin

class UserManager(BaseUserManager):
    def create_user(self, email, username, password=None, **extra_fields):
        if not email:
            raise ValueError("El usuario debe tener un correo electrónico")
        if not username:
            raise ValueError("El usuario debe tener un nombre de usuario")
        email = self.normalize_email(email)
        user = self.model(email=email, username=username, **extra_fields)
        user.set_password(password)  # 🔹 encripta la contraseña
        user.save(using=self._db)
        return user

    def create_superuser(self, email, username, password=None, **extra_fields):
        extra_fields.setdefault("rol", "administrador")
        extra_fields.setdefault("activo", True)
        extra_fields.setdefault("is_staff", True)
        extra_fields.setdefault("is_superuser", True)
        return self.create_user(email, username, password, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):
    ROLES = [
        ('administrador', 'Administrador'),
        ('profesor', 'Profesor'),
        ('estudiante', 'Estudiante'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    username = models.CharField(max_length=50, unique=True)
    email = models.EmailField(max_length=100, unique=True)
    # password lo maneja AbstractBaseUser como password (se mapea a password_hash en DB)
    rol = models.CharField(max_length=20, choices=ROLES, db_column='rol')
    nombre_completo = models.CharField(max_length=100)
    avatar_url = models.CharField(max_length=255, null=True, blank=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    activo = models.BooleanField(default=True)
    ultimo_acceso = models.DateTimeField(null=True, blank=True)
    
    # Campos requeridos por PermissionsMixin para el admin de Django
    is_staff = models.BooleanField(default=False)

    objects = UserManager()

    USERNAME_FIELD = 'email'  # 🔹 usas el correo para autenticar
    REQUIRED_FIELDS = ['username', 'nombre_completo']

    class Meta:
        db_table = 'usuario'
        verbose_name = 'User'
        verbose_name_plural = 'Users'

    def __str__(self):
        return f"{self.nombre_completo} ({self.rol})"
