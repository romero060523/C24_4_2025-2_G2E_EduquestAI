from rest_framework import serializers
from .models import ReglaGamificacion, ConfiguracionNivel


class ReglaGamificacionSerializer(serializers.ModelSerializer):
    tipo_regla_display = serializers.CharField(source='get_tipo_regla_display', read_only=True)
    
    class Meta:
        model = ReglaGamificacion
        fields = [
            'id', 'tipo_regla', 'tipo_regla_display', 'valor', 
            'descripcion', 'activo', 'fecha_creacion', 'fecha_actualizacion'
        ]
        read_only_fields = ('id', 'fecha_creacion', 'fecha_actualizacion')


class ConfiguracionNivelSerializer(serializers.ModelSerializer):
    class Meta:
        model = ConfiguracionNivel
        fields = [
            'id', 'nivel', 'nombre', 'puntos_minimos', 'puntos_maximos',
            'icono', 'descripcion', 'activo', 'fecha_creacion', 'fecha_actualizacion'
        ]
        read_only_fields = ('id', 'fecha_creacion', 'fecha_actualizacion')


class EstadisticasGamificacionSerializer(serializers.Serializer):
    """Serializer para estadísticas generales de gamificación"""
    total_estudiantes = serializers.IntegerField()
    total_profesores = serializers.IntegerField()
    total_cursos = serializers.IntegerField()
    total_misiones = serializers.IntegerField()
    total_puntos_otorgados = serializers.IntegerField()
    promedio_puntos_por_estudiante = serializers.FloatField()
    total_logros_obtenidos = serializers.IntegerField()
    estudiantes_activos_mes = serializers.IntegerField()
    misiones_completadas_mes = serializers.IntegerField()
    cursos_mas_activos = serializers.ListField(child=serializers.DictField())
    estudiantes_top_puntos = serializers.ListField(child=serializers.DictField())


class ReporteEstudiantesSerializer(serializers.Serializer):
    """Serializer para reporte de estudiantes"""
    estudiante_id = serializers.UUIDField()
    nombre = serializers.CharField()
    email = serializers.EmailField()
    puntos_totales = serializers.IntegerField()
    nivel_actual = serializers.IntegerField()
    nombre_nivel = serializers.CharField()
    misiones_completadas = serializers.IntegerField()
    logros_obtenidos = serializers.IntegerField()
    cursos_inscritos = serializers.IntegerField()
    ultima_actividad = serializers.DateTimeField()


class ReporteCursosSerializer(serializers.Serializer):
    """Serializer para reporte de cursos"""
    curso_id = serializers.UUIDField()
    nombre = serializers.CharField()
    codigo = serializers.CharField()
    profesor_nombre = serializers.CharField()
    total_estudiantes = serializers.IntegerField()
    misiones_activas = serializers.IntegerField()
    misiones_completadas = serializers.IntegerField()
    promedio_puntos_curso = serializers.FloatField()
    tasa_completacion = serializers.FloatField()

