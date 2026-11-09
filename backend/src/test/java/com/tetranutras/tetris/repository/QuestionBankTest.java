package com.tetranutras.tetris.repository;

import com.tetranutras.tetris.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QuestionBankTest {

    @Autowired
    QuestionRepository repository;

    private List<Question> all() {
        return repository.findAll();
    }

    @Test
    void banco_noEstaVacio() {
        assertFalse(all().isEmpty());
    }

    @Test
    void todasLasPreguntasTienenCuatroOpciones() {
        assertTrue(all().stream().allMatch(q -> q.options().size() == 4));
    }

    @Test
    void todasLasPreguntasTienenIndiceCorrectoEntre0Y3() {
        assertTrue(all().stream().allMatch(q -> q.correctIndex() >= 0 && q.correctIndex() <= 3));
    }

    @Test
    void losIdsSonUnicos() {
        Set<Integer> ids = all().stream().map(Question::id).collect(Collectors.toSet());

        assertEquals(all().size(), ids.size());
    }

    @Test
    void todasLasPreguntasTienenTextoNoVacio() {
        assertTrue(all().stream().allMatch(q -> !q.text().isBlank()));
    }

    @Test
    void todasLasPreguntasTienenExplicacion() {
        assertTrue(all().stream().allMatch(q -> !q.explanation().isBlank()));
    }

    @Test
    void ningunaPreguntaTieneOpcionesRepetidas() {
        assertTrue(all().stream().allMatch(q -> new HashSet<>(q.options()).size() == 4));
    }

    @Test
    void existenLasTresDificultades() {
        Set<String> dificultades = all().stream().map(Question::difficulty).collect(Collectors.toSet());

        assertTrue(dificultades.containsAll(Set.of("EASY", "MEDIUM", "HARD")));
    }
}