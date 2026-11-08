package com.tetranutras.tetris.service;

import com.tetranutras.tetris.dto.AnswerResult;
import com.tetranutras.tetris.dto.QuestionDto;
import com.tetranutras.tetris.model.Question;
import com.tetranutras.tetris.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    /**
     * Devuelve una pregunta aleatoria pública (sin correctIndex ni explanation)
     * para evitar que el cliente conozca la respuesta de antemano.
     */
    public QuestionDto randomPublic(String difficulty) {
        List<Question> pool = repository.findAll().stream()
                .filter(q -> q.difficulty().equalsIgnoreCase(difficulty))
                .toList();
        if (pool.isEmpty()) {
            pool = repository.findAll();
        }
        Question q = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        return toDto(q);
    }

    public List<QuestionDto> allPublic() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Valida la respuesta enviada por el usuario. Devuelve si fue correcta,
     * el índice correcto, el índice seleccionado y la explicación.
     */
    public AnswerResult check(int id, int selectedIndex) {
        Question q = repository.findAll().stream()
                .filter(question -> question.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pregunta no encontrada: " + id));

        boolean correct = q.correctIndex() == selectedIndex;
        return new AnswerResult(correct, q.correctIndex(), selectedIndex, q.explanation());
    }

    private QuestionDto toDto(Question q) {
        return new QuestionDto(q.id(), q.category(), q.difficulty(), q.text(), q.options());
    }
}
