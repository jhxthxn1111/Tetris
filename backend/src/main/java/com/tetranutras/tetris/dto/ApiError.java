package com.tetranutras.tetris.dto;

/**
 * Cuerpo estructurado de respuestas de error. No contiene trazas de pila ni
 * datos internos de la aplicación.
 */
public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}