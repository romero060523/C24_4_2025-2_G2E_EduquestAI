from rest_framework import serializers
from .models import ConfiguracionVisual


class ConfiguracionVisualSerializer(serializers.ModelSerializer):
    class Meta:
        model = ConfiguracionVisual
        fields = [
            'id',
            'logo_url',
            'nombre_institucion',
            'color_primario',
            'color_secundario',
            'color_acento',
            'color_fondo',
            'activo',
            'fecha_creacion',
            'fecha_actualizacion'
        ]
        read_only_fields = ['id', 'fecha_creacion', 'fecha_actualizacion']

