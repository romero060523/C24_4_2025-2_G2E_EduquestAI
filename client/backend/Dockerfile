# Stage 1: Build de la aplicación
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (se cachea esta capa si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación (saltando tests para build más rápido)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime con JRE
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no privilegiado para mayor seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR construido desde el stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar al usuario no privilegiado
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno opcionales
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Deshabilitar JPA si no hay base de datos configurada
ENV SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
