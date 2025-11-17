from django.contrib import admin
from .models import ReglaGamificacion, ConfiguracionNivel


@admin.register(ReglaGamificacion)
class ReglaGamificacionAdmin(admin.ModelAdmin):
    list_display = ('tipo_regla', 'valor', 'activo', 'fecha_actualizacion')
    list_filter = ('activo', 'tipo_regla')
    search_fields = ('tipo_regla', 'descripcion')
    readonly_fields = ('id', 'fecha_creacion', 'fecha_actualizacion')
    fieldsets = (
        ('Información General', {
            'fields': ('tipo_regla', 'valor', 'descripcion', 'activo')
        }),
        ('Metadatos', {
            'fields': ('id', 'fecha_creacion', 'fecha_actualizacion'),
            'classes': ('collapse',)
        }),
    )


@admin.register(ConfiguracionNivel)
class ConfiguracionNivelAdmin(admin.ModelAdmin):
    list_display = ('nivel', 'nombre', 'puntos_minimos', 'puntos_maximos', 'activo')
    list_filter = ('activo',)
    search_fields = ('nombre', 'descripcion')
    ordering = ('nivel',)
    readonly_fields = ('id', 'fecha_creacion', 'fecha_actualizacion')
    fieldsets = (
        ('Información del Nivel', {
            'fields': ('nivel', 'nombre', 'puntos_minimos', 'puntos_maximos', 'icono', 'descripcion', 'activo')
        }),
        ('Metadatos', {
            'fields': ('id', 'fecha_creacion', 'fecha_actualizacion'),
            'classes': ('collapse',)
        }),
    )

