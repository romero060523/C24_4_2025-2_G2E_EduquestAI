from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny
from .models import ConfiguracionVisual
from .serializers import ConfiguracionVisualSerializer


class ConfiguracionVisualViewSet(viewsets.ModelViewSet):
    """
    ViewSet para gestionar la configuración visual del sistema.
    Solo los administradores pueden modificar la configuración.
    """
    queryset = ConfiguracionVisual.objects.all()
    serializer_class = ConfiguracionVisualSerializer
    
    def get_permissions(self):
        """
        Permite acceso público solo al endpoint 'activa'.
        Requiere autenticación para crear/editar/eliminar.
        """
        if self.action == 'activa':
            return [AllowAny()]
        return [IsAuthenticated()]
    
    def get_queryset(self):
        # Por defecto, obtener solo la configuración activa
        if self.action == 'list':
            return ConfiguracionVisual.objects.filter(activo=True)
        return ConfiguracionVisual.objects.all()
    
    @action(detail=False, methods=['get'])
    def activa(self, request):
        """
        Obtener la configuración visual activa.
        GET /api/configuracion-visual/activa/
        """
        config = ConfiguracionVisual.objects.filter(activo=True).first()
        if not config:
            # Retornar configuración por defecto si no existe ninguna
            config_data = {
                'id': None,
                'logo_url': '',
                'nombre_institucion': 'EduQuest',
                'color_primario': '#3B82F6',
                'color_secundario': '#6366F1',
                'color_acento': '#8B5CF6',
                'color_fondo': '#F9FAFB',
                'activo': True,
                'fecha_creacion': None,
                'fecha_actualizacion': None
            }
            return Response(config_data)
        
        serializer = self.get_serializer(config)
        return Response(serializer.data)
    
    def perform_create(self, serializer):
        # Al crear una nueva configuración, desactivar las anteriores
        ConfiguracionVisual.objects.filter(activo=True).update(activo=False)
        serializer.save(activo=True)
    
    def perform_update(self, serializer):
        # Si se marca como activa, desactivar las demás
        instance = serializer.instance
        if serializer.validated_data.get('activo', instance.activo):
            ConfiguracionVisual.objects.filter(activo=True).exclude(pk=instance.pk).update(activo=False)
        serializer.save()

