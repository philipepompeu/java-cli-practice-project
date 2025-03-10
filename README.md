![Build](https://github.com/philipepompeu/java-cli-practice-project/actions/workflows/ci.yml/badge.svg)

# Java CLI Practice Project

## 📌 Objetivo
Este projeto tem como objetivo praticar conceitos fundamentais da linguagem Java por meio de uma aplicação de linha de comando interativa. Serão abordados conceitos como sintaxe da linguagem, streams, manipulação de arquivos, condições, threads, loops e expressões lambda. O projeto utiliza **Spring Shell** para facilitar a interação.

## 🛠️ Pré-requisitos
Antes de começar, certifique-se de ter os seguintes requisitos instalados:

- **Java 17+** ([Baixar aqui](https://adoptopenjdk.net/))
- **Maven** ([Baixar aqui](https://maven.apache.org/download.cgi)) ou **Gradle** ([Baixar aqui](https://gradle.org/install/))
- **Git** ([Baixar aqui](https://git-scm.com/downloads))
- **Docker** ([Baixar aqui](https://www.docker.com/get-started/))

## 🚀 Como configurar e executar o projeto

### 1️⃣ Clonar o repositório
```sh
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 2️⃣ Compilar o projeto
Se estiver utilizando **Maven**:
```sh
mvn clean install
```

Se estiver utilizando **Gradle**:
```sh
gradle build
```

### 3️⃣ Executar a aplicação
Se estiver usando **Maven**:
```sh
mvn spring-boot:run
```

Se estiver usando **Gradle**:
```sh
gradle bootRun
```

Agora, o terminal estará pronto para receber comandos interativos! 🎯

## 📜 Funcionalidades iniciais

- `hello <nome>` → Exibe "Olá, <nome>!"
- `menu` → Exibe as opções disponíveis no programa
- `soma <num1> <num2>` → Retorna a soma de dois números

## 📦 Executando via Docker
Se preferir rodar a aplicação via Docker, utilize a imagem disponível no Docker Hub.

### 🔹 Baixar e rodar o container
```sh
docker run -it philipepompeu/java-cli-practice-project
```

Isso iniciará a aplicação no modo interativo, permitindo a entrada de comandos.

### 🔹 Passando comandos diretamente
Para executar um comando sem entrar no shell interativo, basta passá-lo diretamente:
```sh
docker run -it philipepompeu/java-cli-practice-project hello Mundo
```

Isso imprimirá:
```
Olá, Mundo!
```

---
## 📌 Próximos Passos
O projeto será expandido gradativamente para incluir novas funcionalidades que explorem manipulação de arquivos, multithreading, expressões lambda e muito mais.
Desenvolvido para praticar Java de forma interativa! 🚀

