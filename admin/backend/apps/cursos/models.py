from django.db import models
import uuid
from django.contrib.auth import get_user_model

# Create your models here.

User = get_user_model()

class Curso(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    codigo_curso = models.CharField(max_length=20, unique=True)
    nombre = models.CharField(max_length=100)
    descripcion = models.TextField(blank=True, null=True)
    imagen_portada = models.CharField(max_length=255, blank=True, null=True)
    fecha_inicio = models.DateField(blank=True, null=True)
    fecha_fin = models.DateField(blank=True, null=True)
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'cursos'
        managed = True
        ordering = ['-fecha_creacion']  # Lista, no set
        verbose_name = 'Curso'
        verbose_name_plural = 'Cursos'

    def __str__(self):
        return f"{self.codigo_curso} - {self.nombre}"
    

class CursoProfesor(models.Model):
    ROLES = [
        ('titular', 'Titular'),
        ('asistente', 'Asistente'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    curso = models.ForeignKey(
        Curso,
        on_delete=models.CASCADE,
        related_name='profesores_asignados',
        db_column='curso_id'
    )
    profesor = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='cursos_asignados',
        limit_choices_to={'rol': 'profesor'},
        db_column='profesor_id'
    )
    rol_profesor = models.CharField(max_length=20, choices=ROLES, default='titular')
    fecha_asignacion = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'cursos_profesores'
        managed = True
        unique_together = [['curso', 'profesor']]
        ordering = ['-fecha_asignacion']
        verbose_name = 'Asignación de Profesor'
        verbose_name_plural = 'Asignaciones de Profesores'
        indexes = [
            models.Index(fields=['curso', 'rol_profesor']),
            models.Index(fields=['profesor']),
        ]

    def __str__(self):
        return f"{self.profesor.nombre_completo} - {self.curso.nombre} ({self.get_rol_profesor_display()})"


class Inscripcion(models.Model):
    ESTADOS = [
        ('activo', 'Activo'),
        ('completado', 'Completado'),
        ('retirado', 'Retirado'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    estudiante = models.ForeignKey(
        User, 
        on_delete=models.CASCADE, 
        related_name='inscripciones',
        limit_choices_to={'rol': 'estudiante'},
        db_column='estudiante_id'
    )
    curso = models.ForeignKey(
        Curso, 
        on_delete=models.CASCADE, 
        related_name='inscripciones',
        db_column='curso_id'
    )
    fecha_inscripcion = models.DateTimeField(auto_now_add=True)
    estado = models.CharField(max_length=20, choices=ESTADOS, default='activo')
    fecha_completado = models.DateTimeField(blank=True, null=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'inscripciones'
        managed = True
        unique_together = [['estudiante', 'curso']]  # Lista de lista
        ordering = ['-fecha_inscripcion']
        verbose_name = 'Inscripción'
        verbose_name_plural = 'Inscripciones'
        indexes = [
            models.Index(fields=['estudiante', 'estado']),
            models.Index(fields=['curso', 'estado']),
        ]

    def __str__(self):
        return f"{self.estudiante.username} - {self.curso.nombre} ({self.estado})"