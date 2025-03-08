# Usa uma imagem base com JDK 17
FROM maven:3.9-eclipse-temurin-17 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para dentro do container
COPY . .

# Compila o projeto usando Maven
RUN mvn clean package -DskipTests

# Usa uma imagem mais leve para rodar a aplicação
FROM eclipse-temurin:17-jre

# Define o diretório de trabalho na imagem final
WORKDIR /app

# Copia o JAR gerado pelo build anterior
COPY --from=builder /app/target/*.jar app-shell.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app-shell.jar"]
