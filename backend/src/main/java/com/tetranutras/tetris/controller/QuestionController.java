package com.tetranutras.tetris.controller;

import com.tetranutras.tetris.model.Question;
import com.tetranutras.tetris.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Question> all() {
        return service.all();
    }

    @GetMapping("/random")
    public Question random(@RequestParam(defaultValue = "EASY") String difficulty) {
        return service.random(difficulty);
    }
}
