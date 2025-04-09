# Sử dụng base image có sẵn Java
FROM openjdk:17-jdk-slim

# Tạo thư mục bên trong container
WORKDIR /app

# Copy file JAR đã build sẵn vào container
COPY target/*.jar app.jar

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "app.jar"]
