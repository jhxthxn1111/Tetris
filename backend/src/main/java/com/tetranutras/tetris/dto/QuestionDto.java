package com.tetranutras.tetris.dto;

import java.util.List;

/**
 * DTO de pregunta para el cliente. Excluye a propósito correctIndex y explanation
 * para evitar que el frontend conozca la respuesta antes de validarla.
 */
public record QuestionDto(
        int id,
        String category,
        String difficulty,
        String text,
        List<String> options
) {}