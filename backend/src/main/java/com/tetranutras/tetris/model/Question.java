package com.tetranutras.tetris.model;

import java.util.List;

public record Question(
        int id,
        String category,
        String difficulty,      // EASY | MEDIUM | HARD
        String text,
        List<String> options,   // 4 opciones: A, B, C, D
        int correctIndex,       // 0..3
        String explanation
) {}
