from django.contrib import admin
from .models import Curso, Inscripcion, CursoProfesor


@admin.register(Curso)
class CursoAdmin(admin.ModelAdmin):
    list_display = ['codigo_curso', 'nombre', 'activo', 'fecha_inicio', 
                    'fecha_fin', 'total_estudiantes', 'fecha_creacion']
    list_filter = ['activo', 'fecha_inicio', 'fecha_creacion']
    search_fields = ['codigo_curso', 'nombre', 'descripcion']
    readonly_fields = ['id', 'fecha_creacion', 'fecha_actualizacion']
    ordering = ['-fecha_creacion']
    
    fieldsets = (
        ('Información Básica', {
            'fields': ('codigo_curso', 'nombre', 'descripcion', 'imagen_portada')
        }),
        ('Fechas del Curso', {
            'fields': ('fecha_inicio', 'fecha_fin', 'activo')
        }),
        ('Metadata', {
            'fields': ('id', 'fecha_creacion', 'fecha_actualizacion'),
            'classes': ('collapse',)
        }),
    )
    
    def total_estudiantes(self, obj):
        return obj.inscripciones.filter(estado='activo').count()
    total_estudiantes.short_description = 'Estudiantes Activos'


@admin.register(Inscripcion)
class InscripcionAdmin(admin.ModelAdmin):
    list_display = ['estudiante', 'curso', 'estado', 'fecha_inscripcion', 'fecha_completado']
    list_filter = ['estado', 'fecha_inscripcion', 'curso']
    search_fields = ['estudiante__username', 'estudiante__email', 
                     'estudiante__nombre_completo', 'curso__nombre', 'curso__codigo_curso']
    readonly_fields = ['id', 'fecha_inscripcion', 'fecha_actualizacion']
    raw_id_fields = ['estudiante', 'curso']
    ordering = ['-fecha_inscripcion']
    
    fieldsets = (
        ('Inscripción', {
            'fields': ('estudiante', 'curso', 'estado')
        }),
        ('Fechas', {
            'fields': ('fecha_inscripcion', 'fecha_completado', 'fecha_actualizacion')
        }),
        ('Metadata', {
            'fields': ('id',),
            'classes': ('collapse',)
        }),
    )
    
    def get_queryset(self, request):
        qs = super().get_queryset(request)
        return qs.select_related('estudiante', 'curso')


@admin.register(CursoProfesor)
class CursoProfesorAdmin(admin.ModelAdmin):
    list_display = ['profesor', 'curso', 'rol_profesor', 'fecha_asignacion']
    list_filter = ['rol_profesor', 'fecha_asignacion']
    search_fields = ['profesor__username', 'profesor__email', 
                     'profesor__nombre_completo', 'curso__nombre', 'curso__codigo_curso']
    readonly_fields = ['id', 'fecha_asignacion']
    raw_id_fields = ['profesor', 'curso']
    ordering = ['-fecha_asignacion']
    
    fieldsets = (
        ('Asignación', {
            'fields': ('curso', 'profesor', 'rol_profesor')
        }),
        ('Metadata', {
            'fields': ('id', 'fecha_asignacion'),
            'classes': ('collapse',)
        }),
    )
    
    def get_queryset(self, request):
        qs = super().get_queryset(request)
        return qs.select_related('profesor', 'curso')
