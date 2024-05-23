package Futoverseny;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// http requesttel elérhetőek az alábbi címen az alábbi végpontok
@RestController
@RequestMapping("/api/v1/competition")
public class CompetitionRestController {

    private final CompetitionRepository competitionRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public CompetitionRestController(CompetitionRepository competitionRepository, ResultRepository resultRepository) {
        this.competitionRepository = competitionRepository;
        this.resultRepository = resultRepository;
    }

    @PostMapping("")
    public CompetitionEntity addCompetition(@RequestBody CompetitionEntity newCompetition) {
        return competitionRepository.save(newCompetition);
    }

    @GetMapping("/{id}/average")
    public double getAverageTime(@PathVariable int id) {
        List<ResultEntity> results = resultRepository.findByCompetition_CompId(id);
        return results.stream().mapToLong(ResultEntity::getResult).average().orElse(0.0);
    }
}
