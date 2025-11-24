from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ConfiguracionVisualViewSet

router = DefaultRouter()
router.register(r'configuracion-visual', ConfiguracionVisualViewSet, basename='configuracion-visual')

urlpatterns = router.urls

