from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ReglaGamificacionViewSet, ConfiguracionNivelViewSet, ReportesViewSet

router = DefaultRouter()
router.register(r'reglas', ReglaGamificacionViewSet, basename='regla-gamificacion')
router.register(r'niveles', ConfiguracionNivelViewSet, basename='configuracion-nivel')
router.register(r'reportes', ReportesViewSet, basename='reportes')

urlpatterns = [
    path('', include(router.urls)),
]

