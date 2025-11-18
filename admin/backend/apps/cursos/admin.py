from django.contrib import admin
from .models import Curso, Inscripcion, CursoProfesor


class InscripcionInline(admin.TabularInline):
    """Inline para agregar estudiantes directamente desde el curso"""
    model = Inscripcion
    extra = 1
    fields = ['estudiante', 'estado', 'fecha_inscripcion']
    readonly_fields = ['fecha_inscripcion']
    autocomplete_fields = ['estudiante']
    verbose_name = 'Estudiante Inscrito'
    verbose_name_plural = 'Estudiantes Inscritos'


@admin.register(Curso)
class CursoAdmin(admin.ModelAdmin):
    list_display = ['codigo_curso', 'nombre', 'activo', 'fecha_inicio', 
                    'fecha_fin', 'total_estudiantes', 'fecha_creacion']
    list_filter = ['activo', 'fecha_inicio', 'fecha_creacion']
    search_fields = ['codigo_curso', 'nombre', 'descripcion']
    readonly_fields = ['id', 'fecha_creacion', 'fecha_actualizacion']
    ordering = ['-fecha_creacion']
    inlines = [InscripcionInline]
    
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
    
    def total_estudiantes(self, obj):
        return obj.inscripciones.filter(estado='activo').count()
    total_estudiantes.short_description = 'Estudiantes Activos'


@admin.register(Inscripcion)
class InscripcionAdmin(admin.ModelAdmin):
    list_display = ['estudiante', 'curso', 'estado', 'fecha_inscripcion', 'fecha_completado']
    list_filter = ['estado', 'fecha_inscripcion', 'curso']
    search_fields = ['estudiante__username', 'estudiante__email', 
                     'estudiante__nombre_completo', 'curso__nombre', 'curso__codigo_curso']
    readonly_fields = ['id', 'fecha_inscripcion']
    autocomplete_fields = ['estudiante', 'curso']
    ordering = ['-fecha_inscripcion']
    
    fieldsets = (
        ('Inscripción', {
            'fields': ('estudiante', 'curso', 'estado')
        }),
        ('Fechas', {
            'fields': ('fecha_inscripcion', 'fecha_completado')
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
    search_fields = ['profesor__nombre_completo', 'profesor__email', 'curso__nombre', 'curso__codigo_curso']
    readonly_fields = ['id', 'fecha_asignacion']
    autocomplete_fields = ['profesor', 'curso']
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
