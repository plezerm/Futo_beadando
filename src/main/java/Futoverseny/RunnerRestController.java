package Futoverseny;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// http requesttel elérhetőek az alábbi címen az alábbi végpontok
@RestController
@RequestMapping("/api/v1/runner")
public class RunnerRestController {

    @Autowired
    private CompetitionRepository competitionRepository;
    private RunnerRepository runnerRepository;

    @Autowired
    public RunnerRestController(RunnerRepository runnerRepository, CompetitionRepository competitionRepository) {
        this.runnerRepository = runnerRepository;
        this.competitionRepository = competitionRepository;
    }

    @GetMapping("/{id}")
    public RunnerEntity getRunner(@PathVariable Long id) {
        return runnerRepository.findById(id).orElse(null);
    }

    @GetMapping("")
    public List<RunnerEntity> getAllRunners() {
        return runnerRepository.findAll();
    }

    @PostMapping("")
    public RunnerEntity addRunner(@RequestBody RunnerEntity newRunner) {
        return runnerRepository.save(newRunner);
    }

}
