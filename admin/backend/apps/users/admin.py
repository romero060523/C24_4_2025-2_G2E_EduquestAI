from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User

# Register your models here.

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ('email', 'username', 'nombre_completo', 'rol', 'activo', 'fecha_creacion')
    list_filter = ('rol', 'activo', 'fecha_creacion')
    search_fields = ('email', 'username', 'nombre_completo')
    ordering = ('-fecha_creacion',)
    
    fieldsets = (
        (None, {'fields': ('email', 'password')}),
        ('Información Personal', {'fields': ('username', 'nombre_completo', 'avatar_url')}),
        ('Permisos', {'fields': ('rol', 'activo', 'is_staff', 'is_superuser')}),
        ('Fechas', {'fields': ('ultimo_acceso', 'fecha_creacion', 'fecha_actualizacion')}),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'username', 'nombre_completo', 'password1', 'password2', 'rol'),
        }),
    )
    
    readonly_fields = ('fecha_creacion', 'fecha_actualizacion', 'ultimo_acceso')
