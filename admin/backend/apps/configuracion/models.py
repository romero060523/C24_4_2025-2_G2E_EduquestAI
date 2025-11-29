import uuid
from django.db import models


class ConfiguracionVisual(models.Model):
    """
    Modelo para almacenar la configuración visual del sistema.
    Solo debe existir una instancia activa.
    """
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    
    # Logo e identidad visual
    logo_url = models.CharField(
        max_length=500,
        blank=True,
        null=True,
        help_text="URL del logo de la institución"
    )
    nombre_institucion = models.CharField(
        max_length=200,
        default="EduQuest",
        help_text="Nombre de la institución"
    )
    
    # Colores del tema
    color_primario = models.CharField(
        max_length=7,
        default="#3B82F6",
        help_text="Color primario en formato hexadecimal (ej: #3B82F6)"
    )
    color_secundario = models.CharField(
        max_length=7,
        default="#6366F1",
        help_text="Color secundario en formato hexadecimal (ej: #6366F1)"
    )
    color_acento = models.CharField(
        max_length=7,
        default="#8B5CF6",
        help_text="Color de acento en formato hexadecimal (ej: #8B5CF6)"
    )
    color_fondo = models.CharField(
        max_length=7,
        default="#F9FAFB",
        help_text="Color de fondo en formato hexadecimal (ej: #F9FAFB)"
    )
    
    # Configuración adicional
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'configuracion_visual'
        verbose_name = 'Configuración Visual'
        verbose_name_plural = 'Configuraciones Visuales'
        ordering = ['-fecha_actualizacion']
    
    def __str__(self):
        return f"Configuración Visual - {self.nombre_institucion}"
    
    def save(self, *args, **kwargs):
        # Si esta configuración se marca como activa, desactivar las demás
        if self.activo:
            ConfiguracionVisual.objects.filter(activo=True).exclude(pk=self.pk).update(activo=False)
        super().save(*args, **kwargs)

