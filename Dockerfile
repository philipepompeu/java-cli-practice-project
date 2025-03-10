# Usa uma imagem base com JDK 17
FROM openjdk:17-jdk-slim as builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven
COPY /target/app-shell.jar app-shell.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app-shell.jar"]
