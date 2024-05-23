package Futoverseny;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// http requesttel elérhetőek az alábbi címen az alábbi végpontok
@RestController
@RequestMapping("/api/v1/result")
public class ResultRestController {

    private final ResultRepository resultRepository;

    @Autowired
    public ResultRestController(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @PostMapping("")
    public ResultEntity addResult(@RequestBody ResultEntity newResult) {
        return resultRepository.save(newResult);
    }
}
