from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from django.db import transaction
from django.shortcuts import get_object_or_404
from django.utils import timezone
from .models import Curso, Inscripcion, CursoProfesor
from .serializers import (
    CursoListSerializer, CursoDetailSerializer,
    CursoCreateUpdateSerializer, InscripcionSerializer,
    InscripcionBulkSerializer, CursoProfesorSerializer,
    CursoProfesorBulkSerializer
)
from django.contrib.auth import get_user_model

User = get_user_model()


class CursoViewSet(viewsets.ModelViewSet):
    queryset = Curso.objects.all()
    permission_classes = [IsAuthenticated]
    
    def get_serializer_class(self):
        if self.action == 'list':
            return CursoListSerializer
        elif self.action == 'retrieve':
            return CursoDetailSerializer
        return CursoCreateUpdateSerializer
    
    def get_queryset(self):
        queryset = Curso.objects.all()
        
        # Filtrar por activo
        activo = self.request.query_params.get('activo')
        if activo is not None:
            queryset = queryset.filter(activo=activo.lower() == 'true')
        
        return queryset
    
    @action(detail=True, methods=['get'])
    def estudiantes(self, request, pk=None):
        """Obtener estudiantes inscritos en el curso"""
        curso = self.get_object()
        inscripciones = curso.inscripciones.filter(estado='activo').select_related('estudiante')
        
        estudiantes = [{
            'id': str(insc.id),  # ID de la inscripción
            'estudiante': {
                'id': str(insc.estudiante.id),
                'nombre_completo': insc.estudiante.nombre_completo,
                'email': insc.estudiante.email,
                'username': insc.estudiante.username,
                'rol': insc.estudiante.rol,
                'avatar_url': insc.estudiante.avatar_url,
                'activo': insc.estudiante.activo
            },
            'fecha_inscripcion': insc.fecha_inscripcion,
            'estado': insc.estado
        } for insc in inscripciones]
        
        return Response({
            'success': True,
            'data': estudiantes,
            'total': len(estudiantes)
        })
    
    @action(detail=True, methods=['post'])
    def inscribir_estudiantes(self, request, pk=None):
        """Inscribir múltiples estudiantes al curso"""
        curso = self.get_object()
        estudiantes_ids = request.data.get('estudiantes_ids', [])
        
        if not estudiantes_ids:
            return Response({
                'success': False,
                'message': 'Debe proporcionar al menos un estudiante'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        inscritos = []
        errores = []
        
        with transaction.atomic():
            for estudiante_id in estudiantes_ids:
                try:
                    estudiante = User.objects.get(id=estudiante_id, rol='estudiante')
                    
                    # Verificar si ya está inscrito
                    if Inscripcion.objects.filter(
                        estudiante=estudiante,
                        curso=curso,
                        estado='activo'
                    ).exists():
                        errores.append({
                            'estudiante_id': str(estudiante_id),
                            'error': 'Ya está inscrito en este curso'
                        })
                        continue
                    
                    # Crear inscripción
                    inscripcion = Inscripcion.objects.create(
                        estudiante=estudiante,
                        curso=curso
                    )
                    inscritos.append({
                        'id': str(inscripcion.id),
                        'estudiante': estudiante.nombre_completo
                    })
                    
                except User.DoesNotExist:
                    errores.append({
                        'estudiante_id': str(estudiante_id),
                        'error': 'Estudiante no encontrado'
                    })
        
        return Response({
            'success': True,
            'message': f'{len(inscritos)} estudiantes inscritos exitosamente',
            'data': {
                'inscritos': inscritos,
                'errores': errores
            }
        }, status=status.HTTP_201_CREATED)

    @action(detail=True, methods=['get'])
    def profesores(self, request, pk=None):
        """Obtener profesores asignados al curso"""
        curso = self.get_object()
        asignaciones = curso.profesores_asignados.select_related('profesor')
        
        profesores = [{
            'id': str(asig.id),  # ID de la asignación
            'profesor': {
                'id': str(asig.profesor.id),
                'nombre_completo': asig.profesor.nombre_completo,
                'email': asig.profesor.email,
                'username': asig.profesor.username,
                'rol': asig.profesor.rol,
                'avatar_url': asig.profesor.avatar_url,
                'activo': asig.profesor.activo
            },
            'rol_profesor': asig.rol_profesor,
            'fecha_asignacion': asig.fecha_asignacion
        } for asig in asignaciones]
        
        return Response({
            'success': True,
            'data': profesores,
            'total': len(profesores)
        })
    
    @action(detail=True, methods=['post'])
    def asignar_profesores(self, request, pk=None):
        """Asignar múltiples profesores al curso"""
        curso = self.get_object()
        profesores_data = request.data.get('profesores', [])
        
        if not profesores_data:
            return Response({
                'success': False,
                'message': 'Debe proporcionar al menos un profesor'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        asignados = []
        errores = []
        
        with transaction.atomic():
            for prof_data in profesores_data:
                try:
                    profesor_id = prof_data.get('profesor_id')
                    rol_profesor = prof_data.get('rol_profesor', 'titular')
                    
                    if not profesor_id:
                        errores.append({
                            'error': 'profesor_id es requerido'
                        })
                        continue
                    
                    profesor = User.objects.get(id=profesor_id, rol='profesor', activo=True)
                    
                    # Verificar si ya está asignado
                    if CursoProfesor.objects.filter(
                        profesor=profesor,
                        curso=curso
                    ).exists():
                        errores.append({
                            'profesor_id': str(profesor_id),
                            'profesor': profesor.nombre_completo,
                            'error': 'Ya está asignado a este curso'
                        })
                        continue
                    
                    # Crear asignación
                    asignacion = CursoProfesor.objects.create(
                        profesor=profesor,
                        curso=curso,
                        rol_profesor=rol_profesor
                    )
                    asignados.append({
                        'id': str(asignacion.id),
                        'profesor': profesor.nombre_completo,
                        'rol_profesor': rol_profesor
                    })
                    
                except User.DoesNotExist:
                    errores.append({
                        'profesor_id': str(profesor_id),
                        'error': 'Profesor no encontrado o inactivo'
                    })
        
        return Response({
            'success': True,
            'message': f'{len(asignados)} profesores asignados exitosamente',
            'data': {
                'asignados': asignados,
                'errores': errores
            }
        }, status=status.HTTP_201_CREATED)


class InscripcionViewSet(viewsets.ModelViewSet):
    queryset = Inscripcion.objects.all()
    serializer_class = InscripcionSerializer
    permission_classes = [IsAuthenticated]
    
    def get_queryset(self):
        queryset = Inscripcion.objects.select_related('estudiante', 'curso')
        
        # Filtrar por curso
        curso_id = self.request.query_params.get('curso_id')
        if curso_id:
            queryset = queryset.filter(curso_id=curso_id)
        
        # Filtrar por estudiante
        estudiante_id = self.request.query_params.get('estudiante_id')
        if estudiante_id:
            queryset = queryset.filter(estudiante_id=estudiante_id)
        
        # Filtrar por estado
        estado = self.request.query_params.get('estado')
        if estado:
            queryset = queryset.filter(estado=estado)
        
        return queryset
    
    @action(detail=True, methods=['patch'])
    def cambiar_estado(self, request, pk=None):
        """Cambiar estado de la inscripción"""
        inscripcion = self.get_object()
        nuevo_estado = request.data.get('estado')
        
        if nuevo_estado not in ['activo', 'completado', 'retirado']:
            return Response({
                'success': False,
                'message': 'Estado inválido. Valores permitidos: activo, completado, retirado'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        inscripcion.estado = nuevo_estado
        if nuevo_estado == 'completado':
            inscripcion.fecha_completado = timezone.now()
        
        inscripcion.save()
        
        serializer = self.get_serializer(inscripcion)
        return Response({
            'success': True,
            'message': f'Estado cambiado a {nuevo_estado}',
            'data': serializer.data
        })
    
    @action(detail=False, methods=['post'])
    def inscripcion_masiva(self, request):
        """Inscribir múltiples estudiantes a un curso"""
        serializer = InscripcionBulkSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        curso_id = serializer.validated_data['curso_id']
        estudiantes_ids = serializer.validated_data['estudiantes_ids']
        
        try:
            curso = Curso.objects.get(id=curso_id, activo=True)
        except Curso.DoesNotExist:
            return Response({
                'success': False,
                'message': 'Curso no encontrado o inactivo'
            }, status=status.HTTP_404_NOT_FOUND)
        
        inscritos = []
        errores = []
        
        with transaction.atomic():
            for estudiante_id in estudiantes_ids:
                try:
                    estudiante = User.objects.get(id=estudiante_id, rol='estudiante', activo=True)
                    
                    # Verificar si ya está inscrito
                    inscripcion_existente = Inscripcion.objects.filter(
                        estudiante=estudiante,
                        curso=curso
                    ).first()
                    
                    if inscripcion_existente:
                        if inscripcion_existente.estado == 'activo':
                            errores.append({
                                'estudiante_id': str(estudiante_id),
                                'estudiante': estudiante.nombre_completo,
                                'error': 'Ya está inscrito activamente'
                            })
                            continue
                        else:
                            # Reactivar inscripción
                            inscripcion_existente.estado = 'activo'
                            inscripcion_existente.save()
                            inscritos.append({
                                'id': str(inscripcion_existente.id),
                                'estudiante': estudiante.nombre_completo,
                                'accion': 'reactivado'
                            })
                            continue
                    
                    # Crear nueva inscripción
                    inscripcion = Inscripcion.objects.create(
                        estudiante=estudiante,
                        curso=curso
                    )
                    inscritos.append({
                        'id': str(inscripcion.id),
                        'estudiante': estudiante.nombre_completo,
                        'accion': 'creado'
                    })
                    
                except User.DoesNotExist:
                    errores.append({
                        'estudiante_id': str(estudiante_id),
                        'error': 'Estudiante no encontrado o inactivo'
                    })
        
        return Response({
            'success': True,
            'message': f'{len(inscritos)} inscripciones procesadas exitosamente',
            'data': {
                'curso': {
                    'id': str(curso.id),
                    'nombre': curso.nombre,
                    'codigo': curso.codigo_curso
                },
                'inscritos': inscritos,
                'errores': errores,
                'total_inscritos': len(inscritos),
                'total_errores': len(errores)
            }
        }, status=status.HTTP_201_CREATED if inscritos else status.HTTP_400_BAD_REQUEST)


class CursoProfesorViewSet(viewsets.ModelViewSet):
    queryset = CursoProfesor.objects.all()
    serializer_class = CursoProfesorSerializer
    permission_classes = [IsAuthenticated]
    
    def get_queryset(self):
        queryset = CursoProfesor.objects.select_related('profesor', 'curso')
        
        # Filtrar por curso
        curso_id = self.request.query_params.get('curso_id')
        if curso_id:
            queryset = queryset.filter(curso_id=curso_id)
        
        # Filtrar por profesor
        profesor_id = self.request.query_params.get('profesor_id')
        if profesor_id:
            queryset = queryset.filter(profesor_id=profesor_id)
        
        # Filtrar por rol_profesor
        rol_profesor = self.request.query_params.get('rol_profesor')
        if rol_profesor:
            queryset = queryset.filter(rol_profesor=rol_profesor)
        
        return queryset
    
    @action(detail=True, methods=['patch'])
    def cambiar_rol(self, request, pk=None):
        """Cambiar el rol del profesor en el curso"""
        asignacion = self.get_object()
        nuevo_rol = request.data.get('rol_profesor')
        
        if nuevo_rol not in ['titular', 'asistente']:
            return Response({
                'success': False,
                'message': 'Rol inválido. Valores permitidos: titular, asistente'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        asignacion.rol_profesor = nuevo_rol
        asignacion.save()
        
        serializer = self.get_serializer(asignacion)
        return Response({
            'success': True,
            'message': f'Rol cambiado a {nuevo_rol}',
            'data': serializer.data
        })
    
    @action(detail=False, methods=['post'])
    def asignacion_masiva(self, request):
        """Asignar múltiples profesores a un curso"""
        serializer = CursoProfesorBulkSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        curso_id = serializer.validated_data['curso_id']
        profesores_data = serializer.validated_data['profesores']
        
        try:
            curso = Curso.objects.get(id=curso_id, activo=True)
        except Curso.DoesNotExist:
            return Response({
                'success': False,
                'message': 'Curso no encontrado o inactivo'
            }, status=status.HTTP_404_NOT_FOUND)
        
        asignados = []
        errores = []
        
        with transaction.atomic():
            for prof_data in profesores_data:
                try:
                    profesor_id = prof_data['profesor_id']
                    rol_profesor = prof_data['rol_profesor']
                    
                    profesor = User.objects.get(id=profesor_id, rol='profesor', activo=True)
                    
                    # Verificar si ya está asignado
                    asignacion_existente = CursoProfesor.objects.filter(
                        profesor=profesor,
                        curso=curso
                    ).first()
                    
                    if asignacion_existente:
                        # Actualizar rol si es diferente
                        if asignacion_existente.rol_profesor != rol_profesor:
                            asignacion_existente.rol_profesor = rol_profesor
                            asignacion_existente.save()
                            asignados.append({
                                'id': str(asignacion_existente.id),
                                'profesor': profesor.nombre_completo,
                                'rol_profesor': rol_profesor,
                                'accion': 'actualizado'
                            })
                        else:
                            errores.append({
                                'profesor_id': str(profesor_id),
                                'profesor': profesor.nombre_completo,
                                'error': 'Ya está asignado con el mismo rol'
                            })
                        continue
                    
                    # Crear nueva asignación
                    asignacion = CursoProfesor.objects.create(
                        profesor=profesor,
                        curso=curso,
                        rol_profesor=rol_profesor
                    )
                    asignados.append({
                        'id': str(asignacion.id),
                        'profesor': profesor.nombre_completo,
                        'rol_profesor': rol_profesor,
                        'accion': 'creado'
                    })
                    
                except User.DoesNotExist:
                    errores.append({
                        'profesor_id': str(prof_data['profesor_id']),
                        'error': 'Profesor no encontrado o inactivo'
                    })
        
        return Response({
            'success': True,
            'message': f'{len(asignados)} asignaciones procesadas exitosamente',
            'data': {
                'curso': {
                    'id': str(curso.id),
                    'nombre': curso.nombre,
                    'codigo': curso.codigo_curso
                },
                'asignados': asignados,
                'errores': errores,
                'total_asignados': len(asignados),
                'total_errores': len(errores)
            }
        }, status=status.HTTP_201_CREATED if asignados else status.HTTP_400_BAD_REQUEST)
