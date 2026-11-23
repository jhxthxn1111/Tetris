package com.tetranutras.tetris.dto;

/**
 * Cuerpo de la petición para validar una respuesta de pregunta.
 * Clase plana (no record) para garantizar la deserialización JSON del cuerpo
 * de la petición por parte de Spring MVC / Jackson en cualquier versión.
 */
public class AnswerRequest {

    private int selectedIndex;

    public AnswerRequest() {
    }

    public AnswerRequest(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}