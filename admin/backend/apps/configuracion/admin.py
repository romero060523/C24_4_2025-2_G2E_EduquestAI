from django.contrib import admin
from .models import ConfiguracionVisual


@admin.register(ConfiguracionVisual)
class ConfiguracionVisualAdmin(admin.ModelAdmin):
    list_display = ['nombre_institucion', 'activo', 'color_primario', 'color_secundario', 'fecha_actualizacion']
    list_filter = ['activo', 'fecha_creacion']
    search_fields = ['nombre_institucion']
    
    fieldsets = (
        ('Identidad Visual', {
            'fields': ('nombre_institucion', 'logo_url', 'activo')
        }),
        ('Colores del Tema', {
            'fields': ('color_primario', 'color_secundario', 'color_acento', 'color_fondo')
        }),
        ('Información', {
            'fields': ('fecha_creacion', 'fecha_actualizacion'),
            'classes': ('collapse',)
        }),
    )
    
    readonly_fields = ('fecha_creacion', 'fecha_actualizacion')

