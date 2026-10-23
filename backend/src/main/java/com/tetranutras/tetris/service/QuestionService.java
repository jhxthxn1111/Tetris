package com.tetranutras.tetris.service;

import com.tetranutras.tetris.model.Question;
import com.tetranutras.tetris.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question random(String difficulty) {
        List<Question> pool = repository.findAll().stream()
                .filter(q -> q.difficulty().equalsIgnoreCase(difficulty))
                .toList();
        if (pool.isEmpty()) pool = repository.findAll();
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    public List<Question> all() {
        return repository.findAll();
    }
}
