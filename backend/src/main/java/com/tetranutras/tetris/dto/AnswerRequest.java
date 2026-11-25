package com.tetranutras.tetris.dto;

/**
 * Cuerpo de la petición para validar una respuesta de pregunta.
 * Clase plana (no record) para garantizar la deserialización JSON del cuerpo
 * de la petición por parte de Spring MVC / Jackson en cualquier versión.
 */
public class AnswerRequest {

    private Integer selectedIndex;

    public AnswerRequest() {
    }

    public AnswerRequest(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public Integer selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}