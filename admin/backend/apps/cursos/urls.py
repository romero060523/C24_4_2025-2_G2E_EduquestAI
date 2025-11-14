from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import CursoViewSet, InscripcionViewSet, CursoProfesorViewSet

router = DefaultRouter()
router.register(r'cursos', CursoViewSet, basename='curso')
router.register(r'inscripciones', InscripcionViewSet, basename='inscripcion')
router.register(r'cursos-profesores', CursoProfesorViewSet, basename='curso-profesor')

urlpatterns = [
    path('', include(router.urls)),
]
