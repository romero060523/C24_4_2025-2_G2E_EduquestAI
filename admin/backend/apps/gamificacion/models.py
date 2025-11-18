import uuid
from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator


class ReglaGamificacion(models.Model):
    """
    Modelo para almacenar reglas globales de gamificación.
    Solo debe existir una instancia activa de cada tipo de regla.
    """
    TIPO_REGLA_CHOICES = [
        ('puntos_completar_mision', 'Puntos por Completar Misión'),
        ('puntos_entrega_tardia', 'Puntos por Entrega Tardía'),
        ('puntos_entrega_anticipada', 'Puntos por Entrega Anticipada'),
        ('multiplicador_dificultad_facil', 'Multiplicador Dificultad Fácil'),
        ('multiplicador_dificultad_medio', 'Multiplicador Dificultad Medio'),
        ('multiplicador_dificultad_dificil', 'Multiplicador Dificultad Difícil'),
        ('puntos_bonificacion_primera_vez', 'Bonificación Primera Vez'),
        ('puntos_bonificacion_racha', 'Bonificación por Racha'),
    ]
    
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    tipo_regla = models.CharField(max_length=50, choices=TIPO_REGLA_CHOICES, unique=True)
    valor = models.DecimalField(
        max_digits=10, 
        decimal_places=2,
        validators=[MinValueValidator(0)],
        help_text="Valor numérico de la regla (puntos o multiplicador)"
    )
    descripcion = models.TextField(blank=True, help_text="Descripción de la regla")
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'reglas_gamificacion'
        verbose_name = 'Regla de Gamificación'
        verbose_name_plural = 'Reglas de Gamificación'
        ordering = ['tipo_regla']
    
    def __str__(self):
        return f"{self.get_tipo_regla_display()}: {self.valor}"


class ConfiguracionNivel(models.Model):
    """
    Modelo para configurar los niveles del sistema de gamificación.
    Define el rango de puntos para cada nivel.
    """
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    nivel = models.IntegerField(
        unique=True,
        validators=[MinValueValidator(1), MaxValueValidator(20)],
        help_text="Número del nivel (1-20)"
    )
    nombre = models.CharField(max_length=100, help_text="Nombre del nivel (ej: Principiante, Intermedio)")
    puntos_minimos = models.IntegerField(
        validators=[MinValueValidator(0)],
        help_text="Puntos mínimos requeridos para alcanzar este nivel"
    )
    puntos_maximos = models.IntegerField(
        null=True,
        blank=True,
        validators=[MinValueValidator(0)],
        help_text="Puntos máximos para este nivel (null si es el nivel máximo)"
    )
    icono = models.CharField(max_length=50, blank=True, help_text="Emoji o código de icono")
    descripcion = models.TextField(blank=True)
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'configuracion_niveles'
        verbose_name = 'Configuración de Nivel'
        verbose_name_plural = 'Configuraciones de Niveles'
        ordering = ['nivel']
    
    def __str__(self):
        return f"Nivel {self.nivel}: {self.nombre} ({self.puntos_minimos}+ pts)"
    
    def clean(self):
        from django.core.exceptions import ValidationError
        if self.puntos_maximos is not None and self.puntos_maximos <= self.puntos_minimos:
            raise ValidationError("Los puntos máximos deben ser mayores que los puntos mínimos")
    
    def save(self, *args, **kwargs):
        self.full_clean()
        super().save(*args, **kwargs)

