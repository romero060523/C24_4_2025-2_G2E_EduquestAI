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
│   └── frontend-mobile/  # (Pendiente)
└── docker-compose.yml    # Orquestación de servicios
```

## 🛠️ Stack Tecnológico

### Admin Backend
- **Framework**: Django 5.2.6
- **Lenguaje**: Python 3.13.3
- **API**: Django REST Framework
- **Autenticación**: JWT (djangorestframework_simplejwt)
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
- **ORM**: Spring Data JPA
- **Template Engine**: Thymeleaf
- **Validación**: Spring Validation

### Client Frontend Web
- **Framework**: React 19.1.1
- **Build Tool**: Vite 7.1.7
- **Lenguaje**: TypeScript 5.9.3
- **Estilos**: Tailwind CSS 4.1.16

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

2. **Levantar todos los servicios**
```bash
docker-compose up -d --build
```

3. **Verificar el estado**
```bash
docker-compose ps
```

4. **Ver logs**
```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f admin-backend
```

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

## 🌐 Acceso a los Servicios

| Servicio | URL | Puerto |
|----------|-----|--------|
| **Admin Backend API** | http://localhost:8000 | 8000 |
| **Admin Frontend** | http://localhost:3000 | 3000 |
| **Client Backend API** | http://localhost:8080 | 8080 |
| **Client Frontend Web** | http://localhost:3001 | 3001 |

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

## 📄 Licencia

Este proyecto es parte del curso CICLO IV - TECSUP.

## 👥 Equipo

- Grupo: G2E
- Ciclo: IV
- Año: 2025-2

---

⭐ **Desarrollado por el equipo G2E** - TECSUP 2025
