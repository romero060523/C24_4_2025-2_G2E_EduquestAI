# 📚 Guía Completa: Historias de Usuario #5 y #8

## 🎯 Historia de Usuario #5: Completar Misiones y Ganar Puntos

### Descripción
Como **estudiante**, quiero **completar misiones y ganar puntos** para motivarme en mi proceso de aprendizaje y ver mi progreso gamificado.

### Criterios de Aceptación
- ✅ El estudiante puede ver todas sus misiones asignadas
- ✅ El estudiante puede completar una misión proporcionando contenido de entrega
- ✅ Los puntos se otorgan automáticamente al completar una misión
- ✅ Los logros se verifican y otorgan automáticamente después de completar una misión
- ✅ El estudiante puede ver el progreso de cada misión (porcentaje completado)
- ✅ El estudiante puede ver los puntos obtenidos por misión completada

---

## 🏆 Historia de Usuario #8: Perfil Gamificado (Nivel, Logros, Ranking)

### Descripción
Como **estudiante**, quiero **visualizar mi perfil gamificado con nivel, logros y ranking** para ver mi progreso y compararme con otros estudiantes.

### Criterios de Aceptación
- ✅ El estudiante puede ver su nivel actual basado en puntos acumulados
- ✅ El estudiante puede ver sus puntos totales y progreso hacia el siguiente nivel
- ✅ El estudiante puede ver todos los logros disponibles y cuáles ha obtenido
- ✅ El estudiante puede ver el ranking global de todos los estudiantes
- ✅ El estudiante puede ver el ranking por curso
- ✅ El ranking se ordena por puntos totales y misiones completadas

---

## 🏗️ Arquitectura del Sistema

### Backend (Spring Boot)

#### 1. Completar Misiones (Historia #5)

**Endpoint Principal:**
```
POST /api/v1/misiones/{misionId}/completar
Headers:
  - X-Estudiante-Id: UUID del estudiante
Body:
  {
    "contenidoEntrega": "string (requerido)",
    "archivoUrl": "string (opcional)",
    "comentariosEstudiante": "string (opcional)"
  }
```

**Controlador:** `MisionController.java`
```java
@PostMapping("/{id}/completar")
public ResponseEntity<ApiResponse<MisionEstudianteResponse>> completarMision(
    @PathVariable UUID id,
    @Valid @RequestBody CompletarMisionRequest request,
    @RequestHeader("X-Estudiante-Id") UUID estudianteId)
```

**Servicio:** `MisionServiceImpl.completarMision()`

**Flujo de Ejecución:**
1. Valida que la misión existe
2. Valida que el estudiante existe
3. Obtiene el progreso de la misión (debe existir)
4. Valida que la misión no esté ya completada
5. Valida que la fecha límite no haya expirado
6. Actualiza el progreso a 100% y marca como completada
7. Crea o actualiza la entrega con el contenido proporcionado
8. Otorga puntos automáticamente (igual a puntosRecompensa de la misión)
9. Verifica y otorga logros automáticamente
10. Retorna la respuesta con los puntos obtenidos

**Archivos Clave:**
- `MisionController.java` - Controlador REST
- `MisionService.java` - Interfaz del servicio
- `MisionServiceImpl.java` - Implementación del servicio
- `CompletarMisionRequest.java` - DTO de request
- `MisionEstudianteResponse.java` - DTO de response
- `ProgresoMision.java` - Entidad de progreso
- `EntregaMision.java` - Entidad de entrega

#### 2. Perfil Gamificado (Historia #8)

**Endpoints:**

1. **Obtener Perfil Gamificado:**
```
GET /api/v1/gamificacion/estudiante/{estudianteId}/perfil
```

2. **Obtener Ranking Global:**
```
GET /api/v1/gamificacion/ranking/global
```

3. **Obtener Ranking por Curso:**
```
GET /api/v1/gamificacion/ranking/curso/{cursoId}
```

**Controlador:** `GamificacionController.java`

**Servicio:** `GamificacionServiceImpl`

**Sistema de Niveles:**
- **Nivel 1 (Principiante):** 0-99 puntos
- **Nivel 2 (Principiante+):** 100-499 puntos
- **Nivel 3 (Intermedio):** 500-999 puntos
- **Nivel 4 (Avanzado):** 1000-2499 puntos
- **Nivel 5 (Experto):** 2500-4999 puntos
- **Nivel 6 (Maestro):** 5000+ puntos

**Sistema de Logros:**
Los logros se otorgan automáticamente cuando el estudiante cumple los requisitos:
- Puntos requeridos
- Nivel requerido
- Misiones completadas requeridas

**Archivos Clave:**
- `GamificacionController.java` - Controlador REST
- `GamificacionService.java` - Interfaz del servicio
- `GamificacionServiceImpl.java` - Implementación del servicio
- `PerfilGamificadoResponse.java` - DTO de perfil
- `RankingResponse.java` - DTO de ranking
- `LogroResponse.java` - DTO de logro
- `Logro.java` - Entidad de logro
- `LogroEstudiante.java` - Relación estudiante-logro

---

### Frontend (React + TypeScript)

#### 1. Completar Misiones (Historia #5)

**Componente Principal:** `MisionesEstudiante.tsx`
- **Ubicación:** `client/frontend-web/my-project/src/pages/estudiante/MisionesEstudiante.tsx`
- **Ruta:** `/estudiante/misiones`

**Funcionalidades:**
- Lista todas las misiones asignadas al estudiante
- Muestra el estado de cada misión (Pendiente, En progreso, Completada)
- Muestra el progreso con barra de porcentaje
- Permite completar misiones mediante un modal
- Muestra puntos obtenidos después de completar

**Estados de Misión:**
- 🎯 **Pendiente:** Porcentaje completado = 0%
- ⏱ **En progreso:** Porcentaje completado > 0% y < 100%
- ✓ **Completada:** Completada = true

**Modal de Completar Misión:**
- Campo de contenido de entrega (requerido)
- Campo de URL de archivo (opcional)
- Campo de comentarios adicionales (opcional)
- Botón para enviar y completar

#### 2. Perfil Gamificado (Historia #8)

**Componente Principal:** `PerfilGamificado.tsx`
- **Ubicación:** `client/frontend-web/my-project/src/pages/estudiante/PerfilGamificado.tsx`
- **Ruta:** `/estudiante/perfil-gamificado`

**Secciones:**
1. **Tarjetas de Estadísticas:**
   - Nivel actual con nombre del nivel
   - Puntos totales
   - Misiones completadas
   - Barra de progreso hacia el siguiente nivel

2. **Logros:**
   - Grid de todos los logros disponibles
   - Logros obtenidos destacados en verde
   - Logros no obtenidos en gris con opacidad
   - Muestra puntos requeridos y fecha de obtención

**Componente de Ranking:** `RankingPage.tsx`
- **Ubicación:** `client/frontend-web/my-project/src/pages/estudiante/RankingPage.tsx`
- **Ruta:** `/estudiante/ranking`

**Funcionalidades:**
- Selector de tipo de ranking (Global o Por Curso)
- Selector de curso (si es ranking por curso)
- Tabla con medallas para los 3 primeros lugares (🥇🥈🥉)
- Información de cada estudiante: nombre, nivel, misiones completadas, puntos

---

## 📊 Modelos de Datos

### Entidades Principales

#### ProgresoMision
```java
@Entity
public class ProgresoMision {
    private UUID id;
    private Mision mision;
    private Usuario estudiante;
    private Integer porcentajeCompletado; // 0-100
    private Boolean completada;
    private LocalDateTime fechaCompletado;
    private LocalDateTime ultimaActividad;
}
```

#### EntregaMision
```java
@Entity
public class EntregaMision {
    private UUID id;
    private Mision mision;
    private Usuario estudiante;
    private EstadoEntrega estado; // PENDIENTE, ENVIADA, REVISANDO, CALIFICADA, RECHAZADA
    private String contenidoEntrega;
    private String archivoUrl;
    private String comentariosEstudiante;
    private Integer puntosObtenidos;
    private LocalDateTime fechaEnvio;
}
```

#### Logro
```java
@Entity
public class Logro {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Integer puntosRequeridos;
    private Integer nivelRequerido;
    private Integer misionesCompletadasRequeridas;
    private Boolean activo;
}
```

#### LogroEstudiante
```java
@Entity
public class LogroEstudiante {
    private UUID id;
    private Usuario estudiante;
    private Logro logro;
    private LocalDateTime fechaObtenido;
}
```

---

## 🔄 Flujos de Trabajo

### Flujo: Completar Misión

```
1. Estudiante navega a "Mis Misiones"
   ↓
2. Sistema carga misiones asignadas (GET /misiones/estudiante/{id})
   ↓
3. Estudiante hace clic en "Completar Misión"
   ↓
4. Se abre modal con formulario
   ↓
5. Estudiante completa formulario:
   - Contenido de entrega (requerido)
   - URL de archivo (opcional)
   - Comentarios (opcional)
   ↓
6. Estudiante envía formulario
   ↓
7. Frontend: POST /misiones/{id}/completar
   ↓
8. Backend valida y procesa:
   - Actualiza progreso a 100%
   - Crea/actualiza entrega
   - Otorga puntos automáticamente
   - Verifica y otorga logros
   ↓
9. Backend retorna respuesta con puntos obtenidos
   ↓
10. Frontend muestra mensaje de éxito
   ↓
11. Frontend recarga lista de misiones
```

### Flujo: Ver Perfil Gamificado

```
1. Estudiante navega a "Mi Perfil Gamificado"
   ↓
2. Frontend: GET /gamificacion/estudiante/{id}/perfil
   ↓
3. Backend calcula:
   - Puntos totales (suma de puntos obtenidos)
   - Nivel actual (basado en puntos)
   - Puntos para siguiente nivel
   - Misiones completadas
   - Logros obtenidos
   ↓
4. Backend retorna PerfilGamificadoResponse
   ↓
5. Frontend muestra:
   - Tarjetas de estadísticas
   - Grid de logros
```

### Flujo: Ver Ranking

```
1. Estudiante navega a "Ranking"
   ↓
2. Estudiante selecciona tipo (Global o Por Curso)
   ↓
3a. Si es Global:
    Frontend: GET /gamificacion/ranking/global
3b. Si es Por Curso:
    Frontend: GET /gamificacion/ranking/curso/{cursoId}
   ↓
4. Backend calcula ranking:
   - Obtiene todos los estudiantes (global) o del curso
   - Calcula puntos y nivel para cada uno
   - Ordena por puntos descendente
   - Asigna posiciones
   ↓
5. Backend retorna RankingResponse
   ↓
6. Frontend muestra tabla con medallas y estadísticas
```

---

## 🛠️ Servicios API (Frontend)

### apiService.completarMision()
```typescript
async completarMision(
  misionId: string,
  request: CompletarMisionRequest
): Promise<MisionEstudianteResponse>
```

**Parámetros:**
- `misionId`: ID de la misión a completar
- `request`: Objeto con contenidoEntrega, archivoUrl (opcional), comentariosEstudiante (opcional)

**Retorna:** MisionEstudianteResponse con información actualizada de la misión

### apiService.obtenerPerfilGamificado()
```typescript
async obtenerPerfilGamificado(estudianteId: string): Promise<PerfilGamificadoResponse>
```

**Retorna:**
```typescript
{
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  puntosParaSiguienteNivel: number;
  misionesCompletadas: number;
  logrosObtenidos: number;
  logros: LogroResponse[];
  posicionRanking?: number;
}
```

### apiService.obtenerRankingGlobal()
```typescript
async obtenerRankingGlobal(): Promise<RankingResponse>
```

**Retorna:** Ranking con todos los estudiantes ordenados por puntos

### apiService.obtenerRankingPorCurso()
```typescript
async obtenerRankingPorCurso(cursoId: string): Promise<RankingResponse>
```

**Retorna:** Ranking con estudiantes del curso especificado

---

## 📝 DTOs y Tipos TypeScript

### CompletarMisionRequest
```typescript
export interface CompletarMisionRequest {
  contenidoEntrega: string;
  archivoUrl?: string;
  comentariosEstudiante?: string;
}
```

### MisionEstudianteResponse
```typescript
export interface MisionEstudianteResponse {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  fechaInicio: string;
  fechaLimite: string;
  activo: boolean;
  cursoNombre: string;
  porcentajeCompletado: number;
  completada: boolean;
  fechaCompletado?: string;
  estadoEntrega: 'PENDIENTE' | 'ENVIADA' | 'REVISANDO' | 'CALIFICADA' | 'RECHAZADA';
  puntosObtenidos: number;
  ultimaActividad: string;
}
```

### PerfilGamificadoResponse
```typescript
export interface PerfilGamificadoResponse {
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  puntosParaSiguienteNivel: number;
  misionesCompletadas: number;
  logrosObtenidos: number;
  logros: LogroResponse[];
  posicionRanking?: number;
}
```

### LogroResponse
```typescript
export interface LogroResponse {
  id: string;
  nombre: string;
  descripcion: string;
  icono?: string;
  puntosRequeridos: number;
  fechaObtenido?: string;
  obtenido: boolean;
}
```

### RankingResponse
```typescript
export interface RankingResponse {
  cursoId?: string;
  cursoNombre: string;
  estudiantes: RankingEstudianteResponse[];
  totalEstudiantes: number;
}
```

### RankingEstudianteResponse
```typescript
export interface RankingEstudianteResponse {
  estudianteId: string;
  nombreEstudiante: string;
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  misionesCompletadas: number;
  posicion: number;
}
```

---

## 🚀 Instrucciones de Uso

### Para el Estudiante

#### Completar una Misión:
1. Inicia sesión como estudiante
2. Navega a "Mis Misiones" desde el menú lateral
3. Revisa las misiones asignadas
4. Haz clic en "Completar Misión" en la misión que deseas completar
5. Completa el formulario:
   - Ingresa el contenido de tu entrega (requerido)
   - Opcionalmente, agrega una URL de archivo
   - Opcionalmente, agrega comentarios adicionales
6. Haz clic en "Completar y Enviar"
7. Verás un mensaje confirmando que ganaste puntos

#### Ver Perfil Gamificado:
1. Navega a "Mi Perfil Gamificado" desde el menú lateral
2. Verás:
   - Tu nivel actual y nombre del nivel
   - Tus puntos totales
   - Progreso hacia el siguiente nivel
   - Cantidad de misiones completadas
   - Todos los logros disponibles y cuáles has obtenido

#### Ver Ranking:
1. Navega a "Ranking" desde el menú lateral
2. Selecciona el tipo de ranking:
   - **Global:** Todos los estudiantes de la plataforma
   - **Por Curso:** Estudiantes de un curso específico
3. Si seleccionaste "Por Curso", elige el curso del dropdown
4. Revisa tu posición y la de otros estudiantes

---

## 🧪 Ejemplos de Uso

### Ejemplo: Completar Misión

**Request:**
```http
POST /api/v1/misiones/123e4567-e89b-12d3-a456-426614174000/completar
Headers:
  X-Estudiante-Id: 456e7890-e89b-12d3-a456-426614174001
Content-Type: application/json

{
  "contenidoEntrega": "He completado la tarea de programación. Implementé todas las funciones requeridas y pasé todos los tests.",
  "archivoUrl": "https://github.com/estudiante/ejercicio-completado",
  "comentariosEstudiante": "Tengo algunas dudas sobre la optimización, pero la funcionalidad está completa."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "titulo": "Ejercicio de Programación",
    "descripcion": "Implementar funciones básicas...",
    "categoria": "EJERCICIO",
    "dificultad": "MEDIO",
    "puntosRecompensa": 50,
    "puntosObtenidos": 50,
    "completada": true,
    "porcentajeCompletado": 100,
    "estadoEntrega": "ENVIADA",
    "fechaCompletado": "2025-01-15T10:30:00"
  },
  "message": "Misión completada exitosamente. ¡Has ganado 50 puntos!"
}
```

### Ejemplo: Obtener Perfil Gamificado

**Request:**
```http
GET /api/v1/gamificacion/estudiante/456e7890-e89b-12d3-a456-426614174001/perfil
```

**Response:**
```json
{
  "success": true,
  "data": {
    "puntosTotales": 350,
    "nivel": 2,
    "nombreNivel": "Principiante+",
    "puntosParaSiguienteNivel": 150,
    "misionesCompletadas": 7,
    "logrosObtenidos": 2,
    "logros": [
      {
        "id": "logro-1",
        "nombre": "Primeros Pasos",
        "descripcion": "Completa tu primera misión",
        "icono": "🎯",
        "puntosRequeridos": 10,
        "obtenido": true,
        "fechaObtenido": "2025-01-10T08:00:00"
      },
      {
        "id": "logro-2",
        "nombre": "Centenario",
        "descripcion": "Alcanza 100 puntos",
        "icono": "💯",
        "puntosRequeridos": 100,
        "obtenido": true,
        "fechaObtenido": "2025-01-12T14:30:00"
      },
      {
        "id": "logro-3",
        "nombre": "Experto",
        "descripcion": "Alcanza 500 puntos",
        "icono": "⭐",
        "puntosRequeridos": 500,
        "obtenido": false
      }
    ]
  },
  "message": "Perfil gamificado obtenido exitosamente"
}
```

### Ejemplo: Obtener Ranking Global

**Request:**
```http
GET /api/v1/gamificacion/ranking/global
```

**Response:**
```json
{
  "success": true,
  "data": {
    "cursoNombre": "Ranking Global",
    "totalEstudiantes": 25,
    "estudiantes": [
      {
        "estudianteId": "abc-123",
        "nombreEstudiante": "Juan Pérez",
        "puntosTotales": 1250,
        "nivel": 4,
        "nombreNivel": "Avanzado",
        "misionesCompletadas": 15,
        "posicion": 1
      },
      {
        "estudianteId": "def-456",
        "nombreEstudiante": "María González",
        "puntosTotales": 980,
        "nivel": 3,
        "nombreNivel": "Intermedio",
        "misionesCompletadas": 12,
        "posicion": 2
      }
    ]
  },
  "message": "Ranking global obtenido exitosamente"
}
```

---

## 🔍 Validaciones y Reglas de Negocio

### Completar Misión:
- ✅ La misión debe existir
- ✅ El estudiante debe existir
- ✅ El estudiante debe tener la misión asignada (progreso debe existir)
- ✅ La misión no debe estar ya completada
- ✅ La fecha límite no debe haber expirado
- ✅ El contenido de entrega es obligatorio
- ✅ Los puntos se otorgan automáticamente igual a puntosRecompensa
- ✅ Los logros se verifican automáticamente después de otorgar puntos

### Sistema de Niveles:
- ✅ Los niveles se calculan basándose en puntos totales
- ✅ El nivel mínimo es 1 (Principiante)
- ✅ El nivel máximo es 6 (Maestro)
- ✅ Los puntos para el siguiente nivel se calculan automáticamente

### Sistema de Logros:
- ✅ Los logros se verifican automáticamente al completar misiones
- ✅ Un logro solo se otorga una vez por estudiante
- ✅ Los logros pueden requerir puntos, nivel o misiones completadas
- ✅ Los logros deben estar activos para ser otorgados

### Ranking:
- ✅ El ranking se ordena por puntos totales (descendente)
- ✅ En caso de empate, se ordena por misiones completadas (descendente)
- ✅ Las posiciones se asignan después de ordenar
- ✅ El ranking global incluye todos los estudiantes activos
- ✅ El ranking por curso incluye solo estudiantes inscritos activamente

---

## 📁 Archivos Relacionados

### Backend
```
client/backend/src/main/java/com/eduquestia/backend/
├── controller/
│   ├── MisionController.java
│   └── GamificacionController.java
├── service/
│   ├── MisionService.java
│   ├── MisionServiceImpl.java
│   ├── GamificacionService.java
│   └── GamificacionServiceImpl.java
├── entity/
│   ├── ProgresoMision.java
│   ├── EntregaMision.java
│   ├── Logro.java
│   └── LogroEstudiante.java
├── dto/
│   ├── request/
│   │   └── CompletarMisionRequest.java
│   └── response/
│       ├── MisionEstudianteResponse.java
│       ├── PerfilGamificadoResponse.java
│       ├── RankingResponse.java
│       ├── RankingEstudianteResponse.java
│       └── LogroResponse.java
└── repository/
    ├── ProgresoMisionRepository.java
    ├── EntregaMisionRepository.java
    ├── LogroRepository.java
    └── LogroEstudianteRepository.java
```

### Frontend
```
client/frontend-web/my-project/src/
├── pages/estudiante/
│   ├── MisionesEstudiante.tsx
│   ├── PerfilGamificado.tsx
│   └── RankingPage.tsx
├── services/
│   └── api.ts
└── types/
    └── index.ts
```

---

## 🎨 Características de UI/UX

### MisionesEstudiante
- **Grid responsivo:** 1 columna en móvil, 2 en tablet, 3 en desktop
- **Badges de estado:** Colores diferenciados (gris, azul, verde)
- **Barra de progreso:** Visualización clara del porcentaje completado
- **Modal intuitivo:** Formulario claro con campos marcados como opcionales/requeridos
- **Feedback inmediato:** Mensajes de éxito después de completar

### PerfilGamificado
- **Tarjetas visuales:** Gradientes de colores para cada estadística
- **Barra de progreso de nivel:** Muestra progreso hacia siguiente nivel
- **Grid de logros:** Visualización clara de logros obtenidos vs no obtenidos
- **Iconos:** Emojis para mejor experiencia visual

### RankingPage
- **Medallas:** Emojis de medallas para los 3 primeros lugares
- **Selector intuitivo:** Botones para cambiar entre Global y Por Curso
- **Tabla clara:** Información organizada y fácil de leer
- **Destacado:** Los 3 primeros lugares con fondo destacado

---

## 🔧 Configuración y Requisitos

### Base de Datos
- PostgreSQL con esquema `grupo_03`
- Tablas requeridas:
  - `progreso_mision`
  - `entrega_mision`
  - `logro`
  - `logro_estudiante`
  - `mision`
  - `curso`
  - `inscripcion`
  - `usuario`

### Variables de Entorno
- `VITE_API_URL`: URL base del backend (default: `http://localhost:8080/api/v1`)

### Dependencias Frontend
- `react`: ^19.1.1
- `react-router-dom`: ^7.9.4
- `axios`: ^1.12.2
- `lucide-react`: ^0.548.0 (para iconos)

---

## 📝 Notas de Implementación

### Puntos Automáticos
- Los puntos se otorgan automáticamente al completar una misión
- El profesor puede ajustar los puntos después de revisar la entrega
- Los puntos iniciales son iguales a `puntosRecompensa` de la misión

### Verificación de Logros
- Los logros se verifican automáticamente después de completar una misión
- Si hay un error al verificar logros, no falla la operación de completar misión
- Los logros se otorgan de forma asíncrona

### Cálculo de Niveles
- Los niveles se calculan en tiempo real basándose en puntos totales
- No se almacena el nivel en la base de datos, se calcula siempre
- El sistema de niveles es progresivo y motivador

### Performance
- El ranking global puede ser costoso con muchos estudiantes
- Considerar implementar caché para rankings si el volumen es alto
- Los cálculos de perfil son eficientes y se hacen en una sola consulta

---

## ✅ Checklist de Funcionalidades

### Historia #5: Completar Misiones
- [x] Endpoint para completar misiones
- [x] Validaciones de negocio
- [x] Otorgamiento automático de puntos
- [x] Verificación automática de logros
- [x] Lista de misiones asignadas
- [x] Estados de misión (Pendiente, En progreso, Completada)
- [x] Modal de completar misión
- [x] Visualización de puntos obtenidos
- [x] Barra de progreso

### Historia #8: Perfil Gamificado
- [x] Endpoint de perfil gamificado
- [x] Cálculo de niveles
- [x] Sistema de logros
- [x] Endpoint de ranking global
- [x] Endpoint de ranking por curso
- [x] Visualización de perfil
- [x] Visualización de logros
- [x] Visualización de ranking
- [x] Selector de tipo de ranking
- [x] Medallas para top 3

---

## 🎯 Próximas Mejoras Sugeridas

1. **Notificaciones:** Notificar cuando se obtiene un logro
2. **Historial:** Historial de puntos ganados
3. **Gráficos:** Gráficos de progreso en el tiempo
4. **Logros especiales:** Logros por categorías de misiones
5. **Badges:** Badges visuales para niveles
6. **Compartir logros:** Compartir logros en redes sociales
7. **Ranking por período:** Ranking mensual, semanal, etc.
8. **Filtros de ranking:** Filtrar por nivel, curso, etc.

---

## 📞 Soporte

Para preguntas o problemas relacionados con estas funcionalidades:
1. Revisar los logs del backend en la consola
2. Verificar que el estudianteId esté en localStorage
3. Verificar que el backend esté corriendo en `http://localhost:8080`
4. Revisar la consola del navegador para errores de frontend

---

**Última actualización:** 15 de Enero, 2025  
**Versión:** 1.0  
**Autor:** Equipo G2E - TECSUP 2025

