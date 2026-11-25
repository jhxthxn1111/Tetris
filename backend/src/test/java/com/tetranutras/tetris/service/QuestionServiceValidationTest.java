package com.tetranutras.tetris.service;

import com.tetranutras.tetris.dto.QuestionDto;
import com.tetranutras.tetris.model.Question;
import com.tetranutras.tetris.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceValidationTest {

    @Mock
    QuestionRepository repository;

    @InjectMocks
    QuestionService service;

    private Question pregunta() {
        return new Question(1, "Historia", "EASY", "Pregunta", List.of("a", "b", "c", "d"), 1, "Explicacion");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4, 99, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void check_conIndiceInvalido_lanzaBadRequest(int indice) {
        var ex = assertThrows(ResponseStatusException.class, () -> service.check(1, indice));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4, 99, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void check_conIndiceInvalido_noConsultaElRepositorio(int indice) {
        assertThrows(ResponseStatusException.class, () -> service.check(1, indice));

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void check_conIndiceValido_noLanzaExcepcion(int indice) {
        when(repository.findAll()).thenReturn(List.of(pregunta()));

        assertDoesNotThrow(() -> service.check(1, indice));
    }

    @Test
    void check_conIdInexistente_incluyeElIdEnElMensaje() {
        when(repository.findAll()).thenReturn(List.of());

        var ex = assertThrows(ResponseStatusException.class, () -> service.check(999, 0));

        assertEquals("Pregunta no encontrada: 999", ex.getReason());
    }

    @Test
    void check_conIndiceInvalidoEIdInexistente_respondeBadRequest() {
        var ex = assertThrows(ResponseStatusException.class, () -> service.check(999, 9));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void dto_noExponeCorrectIndex() {
        boolean loExpone = Arrays.stream(QuestionDto.class.getRecordComponents())
                .anyMatch(c -> c.getName().equals("correctIndex"));

        assertFalse(loExpone);
    }

    @Test
    void dto_noExponeExplanation() {
        boolean loExpone = Arrays.stream(QuestionDto.class.getRecordComponents())
                .anyMatch(c -> c.getName().equals("explanation"));

        assertFalse(loExpone);
    }
}