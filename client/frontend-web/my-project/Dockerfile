# Stage 1: Build de la aplicación
FROM node:20-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias (usando --legacy-peer-deps para resolver conflictos con React 19)
RUN npm install --legacy-peer-deps

# Copiar el resto de archivos del proyecto
COPY . .

# Construir la aplicación para producción
RUN npm run build

# Stage 2: Servidor de producción con Nginx
FROM nginx:alpine

# Copiar archivos construidos desde el stage anterior
COPY --from=builder /app/dist /usr/share/nginx/html

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer el puerto 80
EXPOSE 80

# Comando para iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
