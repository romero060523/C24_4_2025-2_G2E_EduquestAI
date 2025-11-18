import uuid
from django.db import models
from django.core.validators import MinLengthValidator
from apps.users.models import User


class Curso(models.Model):
    """Modelo para cursos"""
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    codigo_curso = models.CharField(
        max_length=20,
        unique=True,
        validators=[MinLengthValidator(3)],
        help_text="Código único del curso (ej: MAT101)"
    )
    nombre = models.CharField(max_length=100, help_text="Nombre del curso")
    descripcion = models.TextField(blank=True, null=True, help_text="Descripción del curso")
    imagen_portada = models.CharField(
        max_length=255,
        blank=True,
        null=True,
        help_text="URL de la imagen de portada"
    )
    fecha_inicio = models.DateField(blank=True, null=True, help_text="Fecha de inicio del curso")
    fecha_fin = models.DateField(blank=True, null=True, help_text="Fecha de fin del curso")
    activo = models.BooleanField(default=True, help_text="Indica si el curso está activo")
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'cursos'
        managed = True  # Django gestiona esta tabla
        verbose_name = 'Curso'
        verbose_name_plural = 'Cursos'
        ordering = ['-fecha_creacion']

    def __str__(self):
        return f"{self.codigo_curso} - {self.nombre}"


class CursoProfesor(models.Model):
    """Relación N:M entre cursos y profesores"""
    ROL_PROFESOR_CHOICES = [
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
    rol_profesor = models.CharField(
        max_length=20,
        choices=ROL_PROFESOR_CHOICES,
        default='titular',
        help_text="Rol del profesor en el curso"
    )
    fecha_asignacion = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'cursos_profesores'
        managed = True
        unique_together = ['curso', 'profesor']
        verbose_name = 'Asignación Profesor-Curso'
        verbose_name_plural = 'Asignaciones Profesor-Curso'

    def __str__(self):
        return f"{self.profesor.nombre_completo} - {self.curso.nombre} ({self.rol_profesor})"


class Inscripcion(models.Model):
    """Modelo para inscripciones de estudiantes en cursos"""
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
        unique_together = ['estudiante', 'curso']
        ordering = ['-fecha_inscripcion']
        verbose_name = 'Inscripción'
        verbose_name_plural = 'Inscripciones'
        indexes = [
            models.Index(fields=['estudiante', 'estado']),
            models.Index(fields=['curso', 'estado']),
        ]

    def __str__(self):
        return f"{self.estudiante.username} - {self.curso.nombre} ({self.estado})"
