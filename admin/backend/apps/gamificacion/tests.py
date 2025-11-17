from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
from django.contrib.auth import get_user_model
from .models import ReglaGamificacion, ConfiguracionNivel

User = get_user_model()


class ReglaGamificacionTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.admin_user = User.objects.create_superuser(
            email='admin@test.com',
            username='admin',
            password='testpass123'
        )
        self.client.force_authenticate(user=self.admin_user)
    
    def test_crear_regla(self):
        """Test para crear una regla de gamificación"""
        data = {
            'tipo_regla': 'puntos_completar_mision',
            'valor': '100.00',
            'descripcion': 'Puntos por completar una misión',
            'activo': True
        }
        response = self.client.post('/api/gamificacion/reglas/', data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(ReglaGamificacion.objects.count(), 1)
    
    def test_listar_reglas(self):
        """Test para listar reglas de gamificación"""
        ReglaGamificacion.objects.create(
            tipo_regla='puntos_completar_mision',
            valor=100.00,
            descripcion='Test'
        )
        response = self.client.get('/api/gamificacion/reglas/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)


class ConfiguracionNivelTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.admin_user = User.objects.create_superuser(
            email='admin@test.com',
            username='admin',
            password='testpass123'
        )
        self.client.force_authenticate(user=self.admin_user)
    
    def test_crear_nivel(self):
        """Test para crear una configuración de nivel"""
        data = {
            'nivel': 1,
            'nombre': 'Principiante',
            'puntos_minimos': 0,
            'puntos_maximos': 99,
            'icono': '🌱',
            'descripcion': 'Nivel inicial'
        }
        response = self.client.post('/api/gamificacion/niveles/', data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(ConfiguracionNivel.objects.count(), 1)

