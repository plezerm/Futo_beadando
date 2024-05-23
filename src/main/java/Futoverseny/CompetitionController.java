package Futoverseny;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CompetitionController {

    private final CompetitionRepository competitionRepository;
    private final RunnerRepository runnerRepository;
    private final ResultRepository resultRepository;

    public CompetitionController(CompetitionRepository competitionRepository, RunnerRepository runnerRepository,
                                 ResultRepository resultRepository) {
        this.competitionRepository = competitionRepository;
        this.runnerRepository = runnerRepository;
        this.resultRepository = resultRepository;
    }

    @GetMapping("/competitions")    //Versenyek megjelenítése
    public String showCompetitions(Model model) {
        model.addAttribute("competitions", competitionRepository.findAll());
        return "competitions";
    }

    @GetMapping("/comp/{id}")       //kiválasztott verseny eredményeinek megjelenítése
    public String showCompetitionResults(@PathVariable int id, Model model) {
        CompetitionEntity competition = competitionRepository.findById((long)id).orElse(null);
        model.addAttribute("competition", competition);
        if (competition != null) {
            List<ResultEntity> sortedResults = resultRepository.findByCompetition(competition)
                    .stream()                                                           //rendezés növekvő sorrendbe
                    .sorted(Comparator.comparing(ResultEntity::getResult))
                    .collect(Collectors.toList());
            if (!sortedResults.isEmpty()) {
                long totalTime = 0;
                for (ResultEntity result : sortedResults) {
                    totalTime += result.getResult();
                }
                double average = Math.round(((double) totalTime / sortedResults.size())*100)/100.0; //átlagolás
                model.addAttribute("average", average);                     //átlagolás
                model.addAttribute("averagehhmm", (String.format("%02d óra %02d perc", (int)average / 60, (int)average % 60)));
            } else {
                model.addAttribute("average", 0);               //0 átlag ha nem volt eredmény
                model.addAttribute("averagehhmm", "00:00");
            }
            model.addAttribute("results", sortedResults);
        } else {
            model.addAttribute("results", List.of());
        }
        return "comp";
    }

    @GetMapping("/comp/{id}/addcompres")    //új eredmény hozzáadásakor kiválasztjuk a versenyt és betöltjük a futók listáját
    public String showAddResultForm(@PathVariable int id, Model model) {
        CompetitionEntity competition = competitionRepository.findById((long)id).orElse(null);
        model.addAttribute("competition", competition);
        model.addAttribute("runners", runnerRepository.findAll());
        model.addAttribute("result", new ResultEntity());
        return "addcompres";
    }

    @PostMapping("/comp/{id}/addcompres")
    public String handleAddResultForm(@PathVariable int id, @ModelAttribute ResultEntity result) {
        ResultEntity newResult = new ResultEntity();        //az új eredmény példány létrehozása, hogy új ID-t kapjon
        newResult.setCompetition(competitionRepository.findById((long) id).orElse(null));
        newResult.setRunner(result.getRunner());
        newResult.setResult(result.getResult());
        resultRepository.save(newResult);
        return "redirect:/comp/" + id;
    }

    @GetMapping("/addcompetition")
    public String showAddCompetitionForm(Model model) {
        model.addAttribute("competition", new CompetitionEntity());
    return "addcompetition";
    }

    @PostMapping("/addcompetition")
    public String handleAddCompetitionForm(@ModelAttribute CompetitionEntity competition, BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes) {
        //validálás
        if (competition.getLength() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Ide egy pozitív számot kell beírni!");
            return "redirect:/addcompetition";
        }
        if (competition.getCompName().length() < 3) {
            redirectAttributes.addFlashAttribute("error", "A verseny neve legalább 3 karakter hosszú legyen!");
            return "redirect:/addcompetition";
        }
        if (competitionRepository.findByCompName(competition.getCompName()) != null) {
            redirectAttributes.addFlashAttribute("error", "Ez a verseny már létezik!");
            return "redirect:/addcompetition";
        }
        CompetitionEntity newCompetition = new CompetitionEntity();     //új verseny példány létrehozása szintén az ID miatt
        newCompetition.setCompName(competition.getCompName());
        newCompetition.setLength(competition.getLength());
        competitionRepository.save(newCompetition);
    return "redirect:/competitions";
    }
}
