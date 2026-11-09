# Tetris Modo Aprende

Tetris web con preguntas A/B/C/D. Backend Java (Spring Boot, MVC) + Frontend Vue 3.

## Correr
Backend, puerto 8080:
    Windows:    cd backend  y luego  .\mvnw.cmd spring-boot:run
    Mac o Linux: cd backend  y luego  ./mvnw spring-boot:run

Frontend (puerto 5173):
    cd frontend && npm install && npm run dev

Abrir http://localhost:5173

## Para el equipo
- Agregar preguntas en backend/src/main/resources/questions.json
- Motor del juego: frontend/src/game/tetris.js
- Logica de partida: frontend/src/views/GameView.vue
