from rest_framework import serializers
from .models import Curso, Inscripcion, CursoProfesor
from django.contrib.auth import get_user_model

User = get_user_model()

class CursoListSerializer(serializers.ModelSerializer):
    total_estudiantes = serializers.SerializerMethodField()
    
    class Meta:
        model = Curso
        fields = ['id', 'codigo_curso', 'nombre', 'descripcion',
                   'imagen_portada', 'fecha_inicio', 'fecha_fin', 
                   'activo', 'fecha_creacion', 'fecha_actualizacion',
                   'total_estudiantes'
                   ]
        
    def get_total_estudiantes(self, obj):
        return obj.inscripciones.filter(estado='activo').count()
    
class CursoDetailSerializer(serializers.ModelSerializer):
    total_estudiantes = serializers.SerializerMethodField()
    estudiantes_inscritos = serializers.SerializerMethodField()

    class Meta:
        model = Curso
        fields = ['id', 'codigo_curso', 'nombre', 'descripcion',
                  'imagen_portada', 'fecha_inicio', 'fecha_fin', 
                  'activo', 'fecha_creacion', 'fecha_actualizacion',
                  'total_estudiantes', 'estudiantes_inscritos'
                  ]
    
    def get_total_estudiantes(self, obj):
        return obj.inscripciones.filter(estado='activo').count()
    
    def get_estudiantes_inscritos(self, obj):
        inscripciones = obj.inscripciones.filter(estado='activo').select_related('estudiante')
        return [{
            'id': str(insc.estudiante.id),
            'nombre': insc.estudiante.nombre_completo,
            'email': insc.estudiante.email,
            'username': insc.estudiante.username,
            'fecha_inscripcion': insc.fecha_inscripcion
        } for insc in inscripciones]


class CursoCreateUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Curso
        fields = ['codigo_curso', 'nombre', 'descripcion',
                  'imagen_portada', 'fecha_inicio', 'fecha_fin', 'activo'
                  ]
        
    def validate_codigo_curso(self, value):
        # Validar que el código sea único (excepto en update)
        if self.instance:  # Update
            if Curso.objects.exclude(pk=self.instance.pk).filter(codigo_curso=value).exists():
                raise serializers.ValidationError("Este código de curso ya existe.")
        else:  # Create
            if Curso.objects.filter(codigo_curso=value).exists():
                raise serializers.ValidationError("Este código de curso ya existe.")
        return value.upper() 
    
class InscripcionSerializer(serializers.ModelSerializer):
    estudiante_nombre = serializers.CharField(source='estudiante.nombre_completo', read_only=True)
    estudiante_email = serializers.EmailField(source='estudiante.email', read_only=True)
    curso_nombre = serializers.CharField(source='curso.nombre', read_only=True)
    curso_codigo = serializers.CharField(source='curso.codigo_curso', read_only=True)
    
    class Meta:
        model = Inscripcion
        fields = [
            'id', 'estudiante', 'estudiante_nombre', 'estudiante_email',
            'curso', 'curso_nombre', 'curso_codigo',
            'fecha_inscripcion', 'estado', 'fecha_completado',
            'fecha_actualizacion'
        ]
        read_only_fields = ['fecha_inscripcion', 'fecha_actualizacion']
    
    def validate(self, data):
        # Solo validar en creación
        if self.instance is None:
            # Validar que el usuario sea estudiante
            estudiante = data.get('estudiante')
            if estudiante and estudiante.rol != 'estudiante':
                raise serializers.ValidationError({
                    'estudiante': 'Solo se pueden inscribir estudiantes.'
                })
            
            # Validar que el curso esté activo
            curso = data.get('curso')
            if curso and not curso.activo:
                raise serializers.ValidationError({
                    'curso': 'No se puede inscribir a un curso inactivo.'
                })
            
            # Validar que no exista inscripción previa activa
            if estudiante and curso:
                existe = Inscripcion.objects.filter(
                    estudiante=estudiante,
                    curso=curso,
                    estado='activo'
                ).exists()
                
                if existe:
                    raise serializers.ValidationError(
                        'El estudiante ya está inscrito en este curso.'
                    )
        
        return data


class InscripcionBulkSerializer(serializers.Serializer):
    curso_id = serializers.UUIDField()
    estudiantes_ids = serializers.ListField(
        child=serializers.UUIDField(),
        min_length=1
    )

# Serializers para CursoProfesor
class CursoProfesorSerializer(serializers.ModelSerializer):
    profesor_nombre = serializers.CharField(source='profesor.nombre_completo', read_only=True)
    profesor_email = serializers.EmailField(source='profesor.email', read_only=True)
    profesor_username = serializers.CharField(source='profesor.username', read_only=True)
    curso_nombre = serializers.CharField(source='curso.nombre', read_only=True)
    curso_codigo = serializers.CharField(source='curso.codigo_curso', read_only=True)
    rol_profesor_display = serializers.CharField(source='get_rol_profesor_display', read_only=True)
    
    class Meta:
        model = CursoProfesor
        fields = [
            'id', 'curso', 'curso_nombre', 'curso_codigo',
            'profesor', 'profesor_nombre', 'profesor_email', 'profesor_username',
            'rol_profesor', 'rol_profesor_display', 'fecha_asignacion'
        ]
        read_only_fields = ['fecha_asignacion']
    
    def validate(self, data):
        if self.instance is None:
            profesor = data.get('profesor')
            if profesor and profesor.rol != 'profesor':
                raise serializers.ValidationError({
                    'profesor': 'Solo se pueden asignar usuarios con rol de profesor.'
                })
            
            curso = data.get('curso')
            if curso and not curso.activo:
                raise serializers.ValidationError({
                    'curso': 'No se puede asignar profesores a un curso inactivo.'
                })
            
            if profesor and curso:
                existe = CursoProfesor.objects.filter(
                    profesor=profesor,
                    curso=curso
                ).exists()
                
                if existe:
                    raise serializers.ValidationError(
                        'El profesor ya está asignado a este curso.'
                    )
        
        return data


class CursoProfesorBulkSerializer(serializers.Serializer):
    curso_id = serializers.UUIDField()
    profesores = serializers.ListField(
        child=serializers.DictField(
            child=serializers.CharField()
        ),
        min_length=1
    )
    
    def validate_profesores(self, value):
        for item in value:
            if 'profesor_id' not in item or 'rol_profesor' not in item:
                raise serializers.ValidationError(
                    'Cada profesor debe tener profesor_id y rol_profesor'
                )
            if item['rol_profesor'] not in ['titular', 'asistente']:
                raise serializers.ValidationError(
                    'rol_profesor debe ser titular o asistente'
                )
        return value
