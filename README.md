# App README

- [ ] TODO Replace or update this README with instructions relevant to your application

## Project Structure

The sources of your App have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
App implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.

---

## ⚙️ CI/CD Pipeline (GitHub Actions)

Este projeto inclui uma **pipeline GitHub Actions** que automatiza o processo de build e publicação do ficheiro JAR.

### 🔁 Funcionalidade da pipeline

1. É executada automaticamente sempre que há um **push** na branch `main`.
2. Faz o **checkout** do repositório e configura o **Java 21 (Temurin)**.
3. **Compila** o projeto com o Maven (`mvn clean package`).
4. **Copia** o ficheiro `.jar` gerado para a **raiz do repositório**, tornando-o acessível diretamente via interface web do GitHub.
5. **Publica** o ficheiro `.jar` como **artefacto** no GitHub Actions.

### 🧩 Excerto do ficheiro `build.yml`

```yaml
name: Build and Publish JAR
on:
  push:
    branches:
      - main  # (a) corre sempre que houver push na branch principal
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # (1) Fazer checkout do código
      - name: Checkout repository
        uses: actions/checkout@v4

      # (2) Configurar Java (b)
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

