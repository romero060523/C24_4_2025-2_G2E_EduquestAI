from rest_framework.permissions import IsAuthenticated
from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework_simplejwt.views import TokenObtainPairView
from .models import User
from .serializers import UserSerializer, CustomTokenObtainPairSerializer
from datetime import datetime

# Create your views here.

@api_view(['GET'])
@permission_classes([AllowAny])
def health_check(request):
    """Endpoint para verificar el estado del servicio"""
    return Response({
        'status': 'UP',
        'application': 'EduquestAI - Admin Backend',
        'version': '1.0.0',
        'timestamp': datetime.now().isoformat(),
        'database': 'Connected'
    })

class CustomTokenObtainPairView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer

class UserViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = User.objects.all().order_by('-fecha_creacion')
    serializer_class = UserSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        return Response(serializer.data, status=status.HTTP_201_CREATED)