---
description: Documentación del juego educativo, su arquitectura y ejecución local.
---

# Tetris Modo Aprende

## Tetris Modo Aprende

Tetris Modo Aprende combina el Tetris clásico con preguntas de cultura general. El jugador organiza piezas en el tablero y responde preguntas durante la partida.

El proyecto busca convertir una mecánica de juego conocida en una experiencia de aprendizaje interactiva. La lógica del juego y la validación de respuestas se mantienen separadas para proteger los datos de las preguntas.

### Introducción y objetivos

El objetivo principal es completar líneas horizontales antes de que las piezas alcancen la parte superior del tablero. Cada línea completa se elimina y libera espacio para continuar jugando.

La capa educativa añade preguntas y respuestas de cultura general. Este flujo permite reforzar conocimientos sin perder el ritmo de la partida.

El proyecto también permite practicar:

* Desarrollo de mecánicas de juego en el navegador.
* Comunicación entre un cliente web y una API.
* Protección de datos mediante objetos de transferencia.

### Arquitectura y tecnologías

El proyecto se divide en un backend que administra las preguntas y un frontend que ejecuta la experiencia de juego.

#### Backend

El backend utiliza Spring Boot 3.4.2 y se ejecuta con Java 25. Expone los servicios que el cliente necesita para consultar preguntas y validar respuestas.

Las respuestas se validan en el servidor. El cliente no decide si una respuesta es correcta. Esta separación evita exponer datos sensibles de las preguntas.

Los DTOs controlan los datos que circulan entre el backend y el frontend. Así, las respuestas correctas y los detalles internos no se envían innecesariamente al navegador.

No se utiliza una base de datos externa. El archivo estático `questions.json` actúa como repositorio de datos. Contiene 20 preguntas organizadas en cinco categorías y tres niveles de dificultad.

#### Frontend

El frontend se desarrolla con Vue.js y Vite. Se encarga de mostrar el tablero, recibir las acciones del jugador y consumir los servicios del backend.

La lógica implementa las siete piezas clásicas de Tetris:

* I, O y T.
* S y Z.
* J y L.

También gestiona el movimiento, las rotaciones y las colisiones con el tablero y con otras piezas. Cuando corresponde, el cliente solicita una pregunta al backend y muestra el resultado de la validación.

### Guía de ejecución local

Antes de iniciar el proyecto, verifica que Java 25 y Node.js estén disponibles en tu entorno.

{% stepper %}
{% step %}
#### Inicia el backend

Desde el directorio del backend, ejecuta el wrapper de Maven:

```bash
java "-Dmaven.multiModuleProjectDirectory=$PWD" -cp .mvn/wrapper/maven-wrapper.jar org.apache.maven.wrapper.MavenWrapperMain spring-boot:run
```

El servicio queda disponible en `http://localhost:8080`.
{% endstep %}

{% step %}
#### Inicia el frontend

En el directorio del cliente, instala las dependencias si aún no lo hiciste. Luego, inicia el servidor de desarrollo:

```bash
npm run dev
```

Abre `http://localhost:5173` en el navegador.
{% endstep %}
{% endstepper %}

### Flujo de funcionamiento

1. El jugador inicia una partida y recibe una de las siete piezas disponibles.
2. El frontend calcula los desplazamientos, las rotaciones y las colisiones.
3. El jugador encaja piezas para completar líneas y mantener espacio disponible.
4. El juego activa una pregunta de cultura general cuando corresponde.
5. El frontend envía la respuesta al backend para validarla.
6. El backend responde con el resultado sin revelar datos internos innecesarios.
7. El cliente actualiza la interfaz y la partida continúa.

La partida termina cuando una nueva pieza no puede colocarse en el tablero.
