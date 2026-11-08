package com.tetranutras.tetris.dto;

/**
 * Cuerpo de la petición para validar una respuesta de pregunta.
 */
public record AnswerRequest(
        int selectedIndex
) {}