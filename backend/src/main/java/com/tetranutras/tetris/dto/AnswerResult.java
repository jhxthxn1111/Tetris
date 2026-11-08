package com.tetranutras.tetris.dto;

/**
 * Resultado de la validación de una respuesta enviada por el usuario.
 */
public record AnswerResult(
        boolean correct,
        int correctIndex,
        int selectedIndex,
        String explanation
) {}