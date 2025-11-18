# Plan de Implementación: Evaluaciones Gamificadas

## Estructura Propuesta

### Relación Misión ↔ Evaluación
- **Una Misión puede tener UNA Evaluación Gamificada** (relación 1:1)
- La evaluación solo existe si la misión es de categoría **QUIZ**
- Cuando un profesor crea una misión tipo QUIZ, puede crear la evaluación asociada

### Flujo de Trabajo

#### Para el Profesor:
1. Crear misión con categoría "QUIZ"
2. Opción: "Agregar Evaluación Gamificada" (botón en el modal de crear misión)
3. Modal de evaluación con:
   - Configuración: tiempo límite, intentos, puntos
   - Editor de preguntas (agregar/editar/eliminar)
   - Vista previa

#### Para el Estudiante:
1. Ver misión tipo QUIZ en su lista
2. Botón: "Tomar Evaluación" (en lugar de "Completar Misión")
3. Página/Modal de evaluación con:
   - Timer (si hay tiempo límite)
   - Preguntas interactivas (tipo Wordwall)
   - Feedback inmediato (opcional)
   - Resultado final con puntos ganados

## Características de la Evaluación

### Configuración:
- ⏱️ Tiempo límite (opcional)
- 🔄 Intentos permitidos (1, 2, 3, ilimitados)
- 📊 Puntos por pregunta
- ⚡ Bonus por velocidad
- ✅ Mostrar resultados inmediato (sí/no)

### Tipos de Preguntas:
1. **Opción Múltiple** (A, B, C, D)
2. **Verdadero/Falso**
3. **Arrastrar y Soltar** (drag & drop)
4. **Completar Espacios** (fill in the blanks)
5. **Ordenar** (ordenar elementos)
6. **Emparejar** (match pairs)
7. **Selección Múltiple** (varias correctas)

## Recomendación de UI

### Opción 1: Modal Expandido (Recomendado)
- Modal grande con timer arriba
- Preguntas una por una (tipo quiz)
- Navegación: Anterior/Siguiente
- Barra de progreso
- Al final: Resultado con animación

### Opción 2: Página Dedicada
- Página completa `/estudiante/evaluacion/:id`
- Más espacio para preguntas complejas
- Mejor para evaluaciones largas

### Opción 3: Híbrido
- Modal para evaluaciones cortas (< 10 preguntas)
- Página para evaluaciones largas

## Implementación Sugerida

1. **Backend**: Ya creado (entidades, repositorios, DTOs)
2. **Frontend Profesor**: 
   - Agregar botón "Crear Evaluación" en modal de misión QUIZ
   - Editor de preguntas con drag & drop
3. **Frontend Estudiante**:
   - Componente interactivo tipo Wordwall
   - Timer con alertas
   - Animaciones de feedback
   - Resultado final gamificado

## ¿Qué prefieres?

A) **Modal Expandido** - Todo en un modal grande, más rápido de implementar
B) **Página Dedicada** - Más espacio, mejor UX para evaluaciones largas
C) **Híbrido** - Modal para cortas, página para largas


