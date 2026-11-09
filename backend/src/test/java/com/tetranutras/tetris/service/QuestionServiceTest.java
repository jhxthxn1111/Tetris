package com.tetranutras.tetris.service;

import com.tetranutras.tetris.model.Question;
import com.tetranutras.tetris.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    QuestionRepository repository;

    @InjectMocks
    QuestionService service;

    private List<Question> questions;

    @BeforeEach
    void setUp() {
        questions = List.of(
                new Question(1, "Historia", "EASY", "Pregunta facil", List.of("a", "b", "c", "d"), 1, "Explicacion facil"),
                new Question(2, "Ciencia", "MEDIUM", "Pregunta media", List.of("a", "b", "c", "d"), 2, "Explicacion media"),
                new Question(3, "Matematicas", "HARD", "Pregunta dificil", List.of("a", "b", "c", "d"), 3, "Explicacion dificil")
        );
        when(repository.findAll()).thenReturn(questions);
    }

    @Test
    void randomPublic_conDificultadHard_devuelveSoloPreguntaHard() {
        var dto = service.randomPublic("HARD");

        assertEquals("HARD", dto.difficulty());
    }

    @Test
    void randomPublic_ignoraMayusculasYMinusculas() {
        var dto = service.randomPublic("hard");

        assertEquals(3, dto.id());
    }

    @Test
    void randomPublic_conDificultadDesconocida_devuelveAlgunaPregunta() {
        var dto = service.randomPublic("XYZ");

        assertNotNull(dto);
    }

    @Test
    void allPublic_devuelveTodasLasPreguntas() {
        var lista = service.allPublic();

        assertEquals(3, lista.size());
    }

    @Test
    void check_conRespuestaCorrecta_devuelveCorrectTrue() {
        var result = service.check(1, 1);

        assertTrue(result.correct());
    }

    @Test
    void check_conRespuestaIncorrecta_devuelveCorrectFalse() {
        var result = service.check(1, 0);

        assertFalse(result.correct());
    }

    @Test
    void check_conRespuestaIncorrecta_informaElIndiceCorrecto() {
        var result = service.check(1, 0);

        assertEquals(1, result.correctIndex());
    }

    @Test
    void check_devuelveElIndiceSeleccionado() {
        var result = service.check(2, 3);

        assertEquals(3, result.selectedIndex());
    }

    @Test
    void check_devuelveLaExplicacion() {
        var result = service.check(2, 0);

        assertEquals("Explicacion media", result.explanation());
    }

    @Test
    void check_conIdInexistente_lanza404() {
        var ex = assertThrows(ResponseStatusException.class, () -> service.check(999, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}