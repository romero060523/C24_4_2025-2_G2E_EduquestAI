from django.contrib import admin
from .models import Curso, CursoProfesor


@admin.register(Curso)
class CursoAdmin(admin.ModelAdmin):
    list_display = ['codigo_curso', 'nombre', 'activo', 'fecha_inicio', 'fecha_fin', 'fecha_creacion']
    list_filter = ['activo', 'fecha_inicio', 'fecha_creacion']
    search_fields = ['codigo_curso', 'nombre', 'descripcion']
    readonly_fields = ['id', 'fecha_creacion', 'fecha_actualizacion']
    fieldsets = (
        ('Información Básica', {
            'fields': ('id', 'codigo_curso', 'nombre', 'descripcion', 'imagen_portada')
        }),
        ('Fechas', {
            'fields': ('fecha_inicio', 'fecha_fin')
        }),
        ('Estado', {
            'fields': ('activo',)
        }),
        ('Auditoría', {
            'fields': ('fecha_creacion', 'fecha_actualizacion'),
            'classes': ('collapse',)
        }),
    )


@admin.register(CursoProfesor)
class CursoProfesorAdmin(admin.ModelAdmin):
    list_display = ['profesor', 'curso', 'rol_profesor', 'fecha_asignacion']
    list_filter = ['rol_profesor', 'fecha_asignacion']
    search_fields = ['profesor__nombre_completo', 'profesor__email', 'curso__nombre', 'curso__codigo_curso']
    readonly_fields = ['id', 'fecha_asignacion']
    autocomplete_fields = ['profesor', 'curso']

