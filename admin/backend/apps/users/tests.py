from rest_framework.test import APITestCase
from rest_framework import status
from django.urls import reverse
from .models import User

class UserAPITestCase(APITestCase):
    def setUp(self):
        # Crear un usuario administrador para autenticación
        self.admin = User.objects.create(
            name="Admin Test",
            email="admin@test.com",
            role="admin",
            is_active=True
        )
        self.admin.set_password("230506")
        self.admin.save()

        # Obtener el token JWT del admin
        url = reverse('token_obtain_pair')
        response = self.client.post(url, {'email': 'admin@test.com', 'password': '230506'}, format='json')
        self.token = response.data['access']

        # Header de autorización
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token}')

    def test_create_user(self):
        """Debe crear un nuevo usuario correctamente"""
        url = reverse('user-list')  # nombre del ViewSet en urls.py
        data = {
            'name': 'Estudiante Test',
            'email': 'student@test.com',
            'password': '230506',
            'role': 'student'
        }
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(User.objects.count(), 2)
        self.assertEqual(User.objects.last().email, 'student@test.com')

    def test_get_users_requires_auth(self):
        """Debe devolver lista de usuarios solo si está autenticado"""
        url = reverse('user-list')
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_token_obtain(self):
        """Debe obtener token JWT válido"""
        url = reverse('token_obtain_pair')
        response = self.client.post(url, {'email': 'admin@test.com', 'password': '230506'}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('access', response.data)
        self.assertIn('refresh', response.data)