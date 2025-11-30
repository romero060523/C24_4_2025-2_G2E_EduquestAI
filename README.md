# 🎓 EduquestAI

**Sistema educativo integral con panel de administración y plataforma cliente**

## 📋 Descripción

EduquestAI es una plataforma educativa completa que consta de dos sistemas independientes:

- **Sistema Admin**: Panel de administración para gestión de la plataforma
- **Sistema Client**: Aplicación web y móvil para usuarios finales

## 🏗️ Arquitectura del Proyecto

```
EduquestAI/
├── admin/
│   ├── backend/          # Django (Python) - API REST
│   └── frontend/         # React + Vite + Tailwind CSS
│       └── my-project/
├── client/
│   ├── backend/          # Spring Boot (Java) - API REST
│   ├── frontend-web/     # React + Vite + Tailwind CSS
│   │   └── my-project/
│   └── frontend-mobile/   # Android (Kotlin + Jetpack Compose)
└── docker-compose.yml    # Orquestación de servicios
```

## 🛠️ Stack Tecnológico

### Base de Datos

- **DBMS**: PostgreSQL 16 (Alpine)
- **Esquema**: `grupo_03`
- **Gestión de Usuarios**: Django Migrations
- **ORM**: Django ORM (Admin) + Spring Data JPA (Client)

### Admin Backend

- **Framework**: Django 5.2.6
- **Lenguaje**: Python 3.13.3
- **API**: Django REST Framework
- **Autenticación**: JWT (djangorestframework_simplejwt)
- **Hashing**: BCrypt con `SpringBootBCryptPasswordHasher` (compatible con jBCrypt)
- **Documentación**: drf-yasg (Swagger)

### Admin Frontend

- **Framework**: React 19.1.1
- **Build Tool**: Vite 7.1.7
- **Lenguaje**: TypeScript 5.9.3
- **Estilos**: Tailwind CSS 4.1.16
- **Linting**: ESLint 9.36.0

### Client Backend

- **Framework**: Spring Boot 3.5.7
- **Lenguaje**: Java 21
- **Build Tool**: Maven 3.9
- **ORM**: Spring Data JPA (Hibernate)
- **Password Hashing**: jBCrypt 0.4
- **Template Engine**: Thymeleaf
- **Validación**: Spring Validation

### Client Frontend Web

- **Framework**: React 19.1.1
- **Build Tool**: Vite 7.1.7
- **Lenguaje**: TypeScript 5.9.3
- **Estilos**: Tailwind CSS 4.1.16

### Client Frontend Mobile

- **Framework**: Android (Kotlin)
- **UI**: Jetpack Compose
- **Arquitectura**: MVVM + Clean Architecture
- **Networking**: Retrofit 2.11.0 + OkHttp 4.12.0
- **Serialization**: Kotlinx Serialization 1.7.3
- **Storage**: DataStore 1.1.1 (tokens)
- **Firebase**: Storage (subida de archivos)
- **Material**: Material 3

### DevOps

- **Containerización**: Docker
- **Orquestación**: Docker Compose
- **Servidor Web**: Nginx (producción)

## 🚀 Inicio Rápido

### Requisitos Previos

- Docker Desktop
- Docker Compose
- Git

### Instalación con Docker (Recomendado)

1. **Clonar el repositorio**

```bash
git clone https://github.com/romero060523/C24_4_2025-2_G2E_EduquestAI.git
cd C24_4_2025-2_G2E_EduquestAI
```

2. **Levantar servicios en el orden correcto**

```bash
# ⚠️ IMPORTANTE: Levantar en este orden para que Django cree primero las tablas de usuario

# 1. Base de datos
docker-compose up -d postgres

# 2. Admin backend (Django - crea tabla usuario y M2M)
docker-compose up -d admin-backend

# Esperar ~10 segundos para que Django ejecute migraciones

# 3. Client backend (Spring Boot - crea sus tablas y referencia usuario)
docker-compose up -d client-backend

# 4. Frontends
docker-compose up -d admin-frontend client-frontend-web
```

**Alternativa rápida** (si la BD ya está inicializada correctamente):

```bash
docker-compose up -d --build
```

3. **Crear superusuario para el panel de admin**

```bash
docker exec -it eduquest-admin-backend python manage.py createsuperuser
```

4. **Verificar el estado**

```bash
docker-compose ps
```

5. **Ver logs**

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f admin-backend
```

### 🗄️ Base de Datos Compartida

⚠️ **IMPORTANTE**: Ambos backends (admin y client) comparten la **misma base de datos PostgreSQL** y la **misma tabla de usuarios**.

- **Base de datos**: `eduquest_db`
- **Esquema**: `grupo_03`
- **Tabla de usuarios**: `grupo_03.usuario` (gestionada por Django)

### 📋 Orden de Inicialización de Base de Datos

**CRÍTICO**: Para evitar problemas con las tablas M2M de Django, sigue este orden:

1. **Primero**: Levantar `postgres` y `admin-backend` (Django)

   - Django creará la tabla `usuario` y todas sus relaciones M2M
   - Tablas creadas: `usuario`, `usuario_groups`, `usuario_user_permissions`, `auth_*`, `django_*`

2. **Después**: Levantar `client-backend` (Spring Boot)
   - Spring Boot creará sus tablas y referenciará a `usuario` mediante FK
   - Tablas creadas: `misiones`, `cursos`, `inscripciones`, `entregas_mision`, etc.

```bash
# Orden correcto de inicialización
docker-compose up -d postgres
sleep 5  # Esperar a que PostgreSQL esté listo
docker-compose up -d admin-backend
sleep 10  # Esperar a que Django ejecute migraciones
docker-compose up -d client-backend
docker-compose up -d admin-frontend client-frontend-web
```

**¿Por qué es importante?**

- Django necesita crear las tablas M2M (`usuario_groups`, `usuario_user_permissions`) para gestionar grupos y permisos
- Si Spring Boot crea primero la tabla `usuario`, Django usará `--fake-initial` y NO creará las M2M
- Esto causará errores al eliminar usuarios desde el panel de admin

### Instalación Manual (Desarrollo)

#### Admin Backend

```bash
cd admin/backend
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver
```

#### Admin Frontend

```bash
cd admin/frontend/my-project
npm install
npm run dev
```

#### Client Backend

```bash
cd client/backend
./mvnw spring-boot:run
```

#### Client Frontend Web

```bash
cd client/frontend-web/my-project
npm install
npm run dev
```

#### Client Frontend Mobile

```bash
cd client/frontend-mobile
./gradlew assembleDebug  # Compilar
./gradlew installDebug   # Instalar en dispositivo/emulador
```

**Requisitos:**
- Android Studio (Arctic Fox o superior)
- JDK 21
- Android SDK (API 34)
- Dispositivo Android o Emulador

**Configuración:**
1. Abrir el proyecto en Android Studio
2. Sincronizar Gradle
3. Configurar dispositivo/emulador
4. Ejecutar la app

## 🌐 Acceso a los Servicios

| Servicio                | URL                   | Puerto |
| ----------------------- | --------------------- | ------ |
| **Admin Backend API**   | http://localhost:8000 | 8000   |
| **Admin Frontend**      | http://localhost:3000 | 3000   |
| **Client Backend API**  | http://localhost:8080 | 8080   |
| **Client Frontend Web** | http://localhost:3001 | 3001   |
| **Client Frontend Mobile** | Android App | -      |
| **PostgreSQL**          | localhost:5432        | 5432   |

## 📦 Comandos Docker Compose

```bash
# Levantar todos los servicios
docker-compose up -d

# Reconstruir y levantar
docker-compose up -d --build

# Detener todos los servicios
docker-compose down

# Ver logs
docker-compose logs -f

# Reiniciar un servicio específico
docker-compose restart admin-backend

# Reconstruir un servicio específico
docker-compose up -d --build admin-backend
```

## 🗄️ Gestión de Base de Datos

### Comandos Útiles

````bash
# Acceder a la consola de PostgreSQL
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db

# Ver todas las tablas del esquema grupo_03
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "\dt grupo_03.*"

# Ver estructura de la tabla usuario
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "\d grupo_03.usuario"

# Ejecutar migraciones de Django
docker exec -it eduquest-admin-backend python manage.py migrate

# Crear superusuario de Django
docker exec -it eduquest-admin-backend python manage.py createsuperuser

### Backup y Restore

```bash
# Crear backup
docker exec eduquest-postgres pg_dump -U postgres eduquest_db > backup.sql

# Restaurar backup
docker exec -i eduquest-postgres psql -U postgres -d eduquest_db < backup.sql
````

## 🧪 Testing

### Admin Backend (Django)

```bash
cd admin/backend
python manage.py test
```

### Client Backend (Spring Boot)

```bash
cd client/backend
./mvnw test
```

### Frontends

```bash
cd admin/frontend/my-project  # o client/frontend-web/my-project
npm run test
```

## 📝 Scripts Disponibles

### Admin Frontend & Client Frontend Web

- `npm run dev` - Servidor de desarrollo
- `npm run build` - Build para producción
- `npm run preview` - Preview del build
- `npm run lint` - Linting del código

### Admin Backend

- `python manage.py runserver` - Servidor de desarrollo
- `python manage.py migrate` - Aplicar migraciones
- `python manage.py makemigrations` - Crear migraciones
- `python manage.py createsuperuser` - Crear superusuario

### Client Backend

- `./mvnw spring-boot:run` - Servidor de desarrollo
- `./mvnw clean package` - Compilar proyecto
- `./mvnw test` - Ejecutar tests

## 🔧 Configuración

### Variables de Entorno

Cada servicio puede configurarse mediante variables de entorno. Consulta los archivos `.env.example` en cada directorio.

### Puertos Personalizados

Si necesitas cambiar los puertos, edita el archivo `docker-compose.yml`:

```yaml
ports:
  - "PUERTO_HOST:PUERTO_CONTENEDOR"
```

## 🐛 Solución de Problemas

### Error al eliminar usuarios desde el panel de admin

**Síntoma**: Error 400/500 al intentar eliminar un usuario con mensaje "relation usuario_groups does not exist"

**Causa**: Las tablas M2M de Django no fueron creadas porque Spring Boot creó primero la tabla `usuario`

**Solución**: Regenerar la base de datos en el orden correcto

```bash
# 1. Detener todos los servicios
docker-compose down

# 2. Limpiar la base de datos
docker-compose up -d postgres
sleep 5
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "DROP SCHEMA grupo_03 CASCADE; CREATE SCHEMA grupo_03; GRANT ALL PRIVILEGES ON SCHEMA grupo_03 TO postgres;"

# 3. Levantar servicios en orden correcto
docker-compose up -d admin-backend
sleep 10
docker-compose up -d client-backend
docker-compose up -d admin-frontend client-frontend-web

# 4. Crear superusuario
docker exec -it eduquest-admin-backend python manage.py createsuperuser
```

### Verificar que las tablas se crearon correctamente

```bash
# Ver todas las tablas del schema
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "SELECT tablename FROM pg_tables WHERE schemaname = 'grupo_03' ORDER BY tablename;"

# Verificar que existen las tablas M2M
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "SELECT tablename FROM pg_tables WHERE schemaname = 'grupo_03' AND tablename LIKE 'usuario_%';"

# Deberías ver: usuario, usuario_groups, usuario_user_permissions
```

### Problemas de autenticación entre Django y Spring Boot

**Síntoma**: Usuarios creados en Django no pueden hacer login en la aplicación cliente

**Causa**: Incompatibilidad de formatos de hash de contraseñas

**Solución**: El sistema usa un hasher personalizado (`SpringBootBCryptPasswordHasher`) que genera hashes BCrypt con revisión `$2a$` compatibles con jBCrypt de Spring Boot. Asegúrate de que:

1. Las contraseñas se crean desde Django (panel de admin)
2. El hash almacenado tiene el formato: `bcrypt_pure$$2a$12$...`
3. Spring Boot puede verificar el hash correctamente

## 📄 Licencia

CICLO IV - TECSUP.

## 👥 Equipo

- Grupo: G2E
- Ciclo: IV
- Año: 2025-2

---

⭐ **Desarrollado por el equipo G2E** - TECSUP 2025

# Verificar que existen las tablas M2M
docker exec -it eduquest-postgres psql -U postgres -d eduquest_db -c "SELECT tablename FROM pg_tables WHERE schemaname = 'grupo_03' AND tablename LIKE 'usuario_%';"

# Deberías ver: usuario, usuario_groups, usuario_user_permissions
```

### Problemas de autenticación entre Django y Spring Boot

**Síntoma**: Usuarios creados en Django no pueden hacer login en la aplicación cliente

**Causa**: Incompatibilidad de formatos de hash de contraseñas

**Solución**: El sistema usa un hasher personalizado (`SpringBootBCryptPasswordHasher`) que genera hashes BCrypt con revisión `$2a$` compatibles con jBCrypt de Spring Boot. Asegúrate de que:

1. Las contraseñas se crean desde Django (panel de admin)
2. El hash almacenado tiene el formato: `bcrypt_pure$$2a$12$...`
3. Spring Boot puede verificar el hash correctamente

## 📄 Licencia

CICLO IV - TECSUP.

## 👥 Equipo

- Grupo: G2E
- Ciclo: IV
- Año: 2025-2

---

⭐ **Desarrollado por el equipo G2E** - TECSUP 2025
