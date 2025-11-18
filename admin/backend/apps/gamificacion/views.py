from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from django.db.models import Count, Sum, Avg, Q, F
from django.db import connection
from django.utils import timezone
from datetime import timedelta
from decimal import Decimal
from uuid import uuid4
from .models import ReglaGamificacion, ConfiguracionNivel
from .serializers import (
    ReglaGamificacionSerializer, ConfiguracionNivelSerializer,
    EstadisticasGamificacionSerializer, ReporteEstudiantesSerializer,
    ReporteCursosSerializer
)
from apps.users.models import User
from apps.cursos.models import Curso, Inscripcion


class ReglaGamificacionViewSet(viewsets.ModelViewSet):
    """
    ViewSet para gestionar reglas globales de gamificación.
    Solo usuarios administradores pueden acceder.
    Si no hay datos reales, devuelve datos de prueba.
    """
    queryset = ReglaGamificacion.objects.all()
    serializer_class = ReglaGamificacionSerializer
    permission_classes = [IsAuthenticated]
    
    def list(self, request, *args, **kwargs):
        """
        Lista todas las reglas de gamificación.
        Si no hay reglas, devuelve datos de prueba.
        """
        queryset = self.get_queryset()
        
        # Si no hay reglas, crear datos de prueba usando instancias del modelo
        if queryset.count() == 0:
            from django.utils import timezone
            from decimal import Decimal
            reglas_prueba = [
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='puntos_completar_mision',
                    valor=Decimal('100.00'),
                    descripcion='Puntos otorgados al completar una misión exitosamente',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='puntos_entrega_tardia',
                    valor=Decimal('50.00'),
                    descripcion='Puntos reducidos por entregar una misión después de la fecha límite',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='puntos_entrega_anticipada',
                    valor=Decimal('150.00'),
                    descripcion='Bonificación por entregar una misión antes de la fecha límite',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='multiplicador_dificultad_facil',
                    valor=Decimal('1.00'),
                    descripcion='Multiplicador para misiones de dificultad fácil',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='multiplicador_dificultad_medio',
                    valor=Decimal('1.50'),
                    descripcion='Multiplicador para misiones de dificultad media',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='multiplicador_dificultad_dificil',
                    valor=Decimal('2.00'),
                    descripcion='Multiplicador para misiones de dificultad difícil',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='puntos_bonificacion_primera_vez',
                    valor=Decimal('50.00'),
                    descripcion='Bonificación adicional por completar una misión por primera vez',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ReglaGamificacion(
                    id=uuid4(),
                    tipo_regla='puntos_bonificacion_racha',
                    valor=Decimal('25.00'),
                    descripcion='Bonificación por mantener una racha de misiones completadas consecutivas',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
            ]
            serializer = self.get_serializer(reglas_prueba, many=True)
            return Response(serializer.data)
        
        return super().list(request, *args, **kwargs)
    
    def get_queryset(self):
        queryset = ReglaGamificacion.objects.all()
        activo = self.request.query_params.get('activo')
        if activo is not None:
            queryset = queryset.filter(activo=activo.lower() == 'true')
        return queryset.order_by('tipo_regla')


class ConfiguracionNivelViewSet(viewsets.ModelViewSet):
    """
    ViewSet para gestionar configuraciones de niveles.
    Solo usuarios administradores pueden acceder.
    Si no hay datos reales, devuelve datos de prueba.
    """
    queryset = ConfiguracionNivel.objects.filter(activo=True)
    serializer_class = ConfiguracionNivelSerializer
    permission_classes = [IsAuthenticated]
    ordering = ['nivel']
    
    def list(self, request, *args, **kwargs):
        """
        Lista todas las configuraciones de niveles.
        Si no hay niveles, devuelve datos de prueba.
        """
        queryset = self.filter_queryset(self.get_queryset())
        
        # Si no hay niveles, crear datos de prueba usando instancias del modelo
        if queryset.count() == 0:
            from django.utils import timezone
            niveles_prueba = [
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=1,
                    nombre='Principiante',
                    puntos_minimos=0,
                    puntos_maximos=99,
                    icono='🌱',
                    descripcion='Nivel inicial para nuevos estudiantes',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=2,
                    nombre='Principiante+',
                    puntos_minimos=100,
                    puntos_maximos=499,
                    icono='🌿',
                    descripcion='Estudiante que ha comenzado a ganar experiencia',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=3,
                    nombre='Intermedio',
                    puntos_minimos=500,
                    puntos_maximos=999,
                    icono='🌳',
                    descripcion='Estudiante con conocimiento intermedio',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=4,
                    nombre='Avanzado',
                    puntos_minimos=1000,
                    puntos_maximos=2499,
                    icono='⭐',
                    descripcion='Estudiante avanzado con buen rendimiento',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=5,
                    nombre='Experto',
                    puntos_minimos=2500,
                    puntos_maximos=4999,
                    icono='🌟',
                    descripcion='Estudiante experto con excelente rendimiento',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
                ConfiguracionNivel(
                    id=uuid4(),
                    nivel=6,
                    nombre='Maestro',
                    puntos_minimos=5000,
                    puntos_maximos=None,
                    icono='👑',
                    descripcion='Nivel máximo alcanzable - Maestro del sistema',
                    activo=True,
                    fecha_creacion=timezone.now(),
                    fecha_actualizacion=timezone.now(),
                ),
            ]
            serializer = self.get_serializer(niveles_prueba, many=True)
            return Response(serializer.data)
        
        return super().list(request, *args, **kwargs)


class ReportesViewSet(viewsets.ViewSet):
    """
    ViewSet para generar reportes generales de gamificación.
    Solo usuarios administradores pueden acceder.
    """
    permission_classes = [IsAuthenticated]
    
    @action(detail=False, methods=['get'])
    def estadisticas_generales(self, request):
        """
        Obtiene estadísticas generales del sistema de gamificación.
        Si no hay datos reales, devuelve datos de prueba.
        """
        # Contar usuarios por rol
        total_estudiantes = User.objects.filter(rol='estudiante', activo=True).count()
        total_profesores = User.objects.filter(rol='profesor', activo=True).count()
        
        # Contar cursos y misiones
        total_cursos = Curso.objects.filter(activo=True).count()
        
        # Obtener total de misiones desde la tabla del client backend
        total_misiones = 0
        total_puntos_otorgados = 0
        total_logros_obtenidos = 0
        misiones_completadas_mes = 0
        estudiantes_top_puntos = []
        
        try:
            with connection.cursor() as cursor:
                cursor.execute("""
                    SELECT COUNT(*) FROM grupo_03.misiones WHERE activo = true
                """)
                total_misiones = cursor.fetchone()[0] or 0
                
                # Calcular puntos totales otorgados (desde entregas_mision)
                cursor.execute("""
                    SELECT COALESCE(SUM(puntos_obtenidos), 0) 
                    FROM grupo_03.entrega_mision 
                    WHERE puntos_obtenidos IS NOT NULL AND puntos_obtenidos > 0
                """)
                total_puntos_otorgados = cursor.fetchone()[0] or 0
                
                # Contar logros obtenidos
                cursor.execute("""
                    SELECT COUNT(*) FROM grupo_03.logros_estudiante
                """)
                total_logros_obtenidos = cursor.fetchone()[0] or 0
        except Exception:
            # Si las tablas no existen, usar valores por defecto
            pass
        
        # Calcular promedio de puntos por estudiante
        if total_estudiantes > 0:
            promedio_puntos_por_estudiante = float(total_puntos_otorgados) / total_estudiantes
        else:
            promedio_puntos_por_estudiante = 0.0
        
        # Estudiantes activos este mes
        inicio_mes = timezone.now().replace(day=1, hour=0, minute=0, second=0, microsecond=0)
        estudiantes_activos_mes = User.objects.filter(
            rol='estudiante',
            activo=True,
            ultimo_acceso__gte=inicio_mes
        ).count()
        
        # Misiones completadas este mes
        try:
            with connection.cursor() as cursor:
                cursor.execute("""
                    SELECT COUNT(*) 
                    FROM grupo_03.progreso_mision 
                    WHERE completada = true 
                    AND fecha_completado >= %s
                """, [inicio_mes])
                misiones_completadas_mes = cursor.fetchone()[0] or 0
        except Exception:
            misiones_completadas_mes = 0
        
        # Cursos más activos (por número de estudiantes inscritos)
        try:
            cursos_mas_activos = list(
                Curso.objects.filter(activo=True)
                .annotate(
                    total_estudiantes=Count('inscripciones', filter=Q(inscripciones__estado='activo'))
                )
                .order_by('-total_estudiantes')[:5]
                .values('id', 'nombre', 'codigo_curso', 'total_estudiantes')
            )
        except Exception:
            cursos_mas_activos = []
        
        # Top estudiantes por puntos
        try:
            with connection.cursor() as cursor:
                cursor.execute("""
                    SELECT 
                        e.estudiante_id,
                        u.nombre_completo,
                        COALESCE(SUM(e.puntos_obtenidos), 0) as puntos_totales
                    FROM grupo_03.entrega_mision e
                    INNER JOIN grupo_03.usuario u ON e.estudiante_id = u.id
                    WHERE e.puntos_obtenidos IS NOT NULL 
                    AND e.puntos_obtenidos > 0
                    AND u.rol = 'estudiante'
                    AND u.activo = true
                    GROUP BY e.estudiante_id, u.nombre_completo
                    ORDER BY puntos_totales DESC
                    LIMIT 10
                """)
                estudiantes_top_puntos = [
                    {
                        'estudiante_id': str(row[0]),
                        'nombre': row[1] or 'Sin nombre',
                        'puntos_totales': int(row[2])
                    }
                    for row in cursor.fetchall()
                ]
        except Exception:
            # Si no hay datos reales, devolver lista vacía
            estudiantes_top_puntos = []
        
        estadisticas = {
            'total_estudiantes': total_estudiantes,
            'total_profesores': total_profesores,
            'total_cursos': total_cursos,
            'total_misiones': total_misiones,
            'total_puntos_otorgados': total_puntos_otorgados,
            'promedio_puntos_por_estudiante': promedio_puntos_por_estudiante,
            'total_logros_obtenidos': total_logros_obtenidos,
            'estudiantes_activos_mes': estudiantes_activos_mes,
            'misiones_completadas_mes': misiones_completadas_mes,
            'cursos_mas_activos': cursos_mas_activos,
            'estudiantes_top_puntos': estudiantes_top_puntos,
        }
        
        serializer = EstadisticasGamificacionSerializer(estadisticas)
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['get'])
    def reporte_estudiantes(self, request):
        """
        Genera un reporte detallado de estudiantes con sus estadísticas de gamificación.
        Si no hay datos reales, devuelve datos de prueba.
        """
        estudiantes = User.objects.filter(rol='estudiante', activo=True)
        
        # Obtener datos de gamificación desde las tablas del client backend
        reporte = []
        
        # Procesar estudiantes reales
        with connection.cursor() as cursor:
            for estudiante in estudiantes:
                try:
                    # Obtener puntos totales
                    cursor.execute("""
                        SELECT COALESCE(SUM(puntos_obtenidos), 0)
                        FROM grupo_03.entrega_mision
                        WHERE estudiante_id = %s AND puntos_obtenidos IS NOT NULL
                    """, [estudiante.id])
                    puntos_totales = cursor.fetchone()[0] or 0
                except Exception:
                    puntos_totales = 0
                    
                # Calcular nivel (simplificado - nivel 1-6 basado en puntos)
                nivel_actual = 1
                nombre_nivel = 'Principiante'
                if puntos_totales >= 5000:
                    nivel_actual = 6
                    nombre_nivel = 'Maestro'
                elif puntos_totales >= 2500:
                    nivel_actual = 5
                    nombre_nivel = 'Experto'
                elif puntos_totales >= 1000:
                    nivel_actual = 4
                    nombre_nivel = 'Avanzado'
                elif puntos_totales >= 500:
                    nivel_actual = 3
                    nombre_nivel = 'Intermedio'
                elif puntos_totales >= 100:
                    nivel_actual = 2
                    nombre_nivel = 'Principiante+'
                    
                # Contar misiones completadas
                try:
                    cursor.execute("""
                        SELECT COUNT(*) FROM grupo_03.progreso_mision
                        WHERE estudiante_id = %s AND completada = true
                    """, [estudiante.id])
                    misiones_completadas = cursor.fetchone()[0] or 0
                except Exception:
                    misiones_completadas = 0
                
                # Contar logros obtenidos
                try:
                    cursor.execute("""
                        SELECT COUNT(*) FROM grupo_03.logros_estudiante
                        WHERE estudiante_id = %s
                    """, [estudiante.id])
                    logros_obtenidos = cursor.fetchone()[0] or 0
                except Exception:
                    logros_obtenidos = 0
                
                reporte.append({
                    'estudiante_id': str(estudiante.id),
                    'nombre': estudiante.nombre_completo or estudiante.username,
                    'email': estudiante.email,
                    'puntos_totales': int(puntos_totales),
                    'nivel_actual': nivel_actual,
                    'nombre_nivel': nombre_nivel,
                    'misiones_completadas': misiones_completadas,
                    'logros_obtenidos': logros_obtenidos,
                    'cursos_inscritos': Inscripcion.objects.filter(
                        estudiante=estudiante, estado='activo'
                    ).count(),
                    'ultima_actividad': estudiante.ultimo_acceso,
                })
        
        serializer = ReporteEstudiantesSerializer(reporte, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['get'])
    def reporte_cursos(self, request):
        """
        Genera un reporte detallado de cursos con sus estadísticas de gamificación.
        Si no hay datos reales, devuelve datos de prueba.
        """
        cursos = Curso.objects.filter(activo=True)
        
        reporte = []
        
        # Procesar cursos reales
        for curso in cursos:
            inscripciones_activas = Inscripcion.objects.filter(curso=curso, estado='activo')
            total_estudiantes = inscripciones_activas.count()
            
            # Obtener profesor del curso
            profesor_nombre = 'Sin asignar'
            if hasattr(curso, 'profesor_id') and curso.profesor_id:
                try:
                    profesor = User.objects.get(id=curso.profesor_id)
                    profesor_nombre = profesor.nombre_completo or profesor.username
                except User.DoesNotExist:
                    pass
            
            # Obtener estadísticas de misiones del curso
            try:
                with connection.cursor() as cursor:
                    cursor.execute("""
                        SELECT 
                            COUNT(*) FILTER (WHERE activo = true) as misiones_activas,
                            COUNT(*) FILTER (WHERE activo = false) as misiones_inactivas
                        FROM grupo_03.misiones
                        WHERE curso_id = %s
                    """, [curso.id])
                    row = cursor.fetchone()
                    misiones_activas = row[0] or 0 if row else 0
                    
                    # Contar misiones completadas del curso
                    cursor.execute("""
                        SELECT COUNT(DISTINCT pm.mision_id)
                        FROM grupo_03.progreso_mision pm
                        INNER JOIN grupo_03.misiones m ON pm.mision_id = m.id
                        WHERE m.curso_id = %s AND pm.completada = true
                    """, [curso.id])
                    misiones_completadas = cursor.fetchone()[0] or 0
                    
                    # Calcular promedio de puntos del curso
                    cursor.execute("""
                        SELECT COALESCE(AVG(e.puntos_obtenidos), 0)
                        FROM grupo_03.entrega_mision e
                        INNER JOIN grupo_03.misiones m ON e.mision_id = m.id
                        WHERE m.curso_id = %s 
                        AND e.puntos_obtenidos IS NOT NULL
                    """, [curso.id])
                    promedio_puntos_curso = float(cursor.fetchone()[0] or 0)
                    
                    # Calcular tasa de completación
                    # Tasa = (misiones completadas / total de misiones asignadas) * 100
                    if total_estudiantes > 0 and misiones_activas > 0:
                        cursor.execute("""
                            SELECT COUNT(*)
                            FROM grupo_03.progreso_mision pm
                            INNER JOIN grupo_03.misiones m ON pm.mision_id = m.id
                            WHERE m.curso_id = %s AND pm.completada = true
                        """, [curso.id])
                        total_completadas = cursor.fetchone()[0] or 0
                        total_posibles = total_estudiantes * misiones_activas
                        tasa_completacion = (total_completadas / total_posibles * 100) if total_posibles > 0 else 0.0
                    else:
                        tasa_completacion = 0.0
            except Exception:
                misiones_activas = 0
                misiones_completadas = 0
                promedio_puntos_curso = 0.0
                tasa_completacion = 0.0
            
            reporte.append({
                'curso_id': str(curso.id),
                'nombre': curso.nombre,
                'codigo': curso.codigo_curso or 'N/A',
                'profesor_nombre': profesor_nombre,
                'total_estudiantes': total_estudiantes,
                'misiones_activas': misiones_activas,
                'misiones_completadas': misiones_completadas,
                'promedio_puntos_curso': round(promedio_puntos_curso, 2),
                'tasa_completacion': round(tasa_completacion, 2),
            })
        
        serializer = ReporteCursosSerializer(reporte, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['get'])
    def resumen_mensual(self, request):
        """
        Genera un resumen mensual de actividad de gamificación.
        """
        mes_actual = timezone.now().month
        año_actual = timezone.now().year
        
        inicio_mes = timezone.now().replace(
            day=1, hour=0, minute=0, second=0, microsecond=0
        )
        
        # Nuevos estudiantes este mes
        nuevos_estudiantes = User.objects.filter(
            rol='estudiante',
            fecha_creacion__gte=inicio_mes
        ).count()
        
        # Nuevos cursos este mes
        nuevos_cursos = Curso.objects.filter(
            fecha_creacion__gte=inicio_mes
        ).count()
        
        # Estudiantes activos
        estudiantes_activos = User.objects.filter(
            rol='estudiante',
            activo=True,
            ultimo_acceso__gte=inicio_mes
        ).count()
        
        return Response({
            'mes': mes_actual,
            'año': año_actual,
            'nuevos_estudiantes': nuevos_estudiantes,
            'nuevos_cursos': nuevos_cursos,
            'estudiantes_activos': estudiantes_activos,
        }, status=status.HTTP_200_OK)

