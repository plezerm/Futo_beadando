package Futoverseny;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RunnerController {

    @Autowired
    private RunnerRepository runnerRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private RunnerRestController runnerRestController;

    @GetMapping("/runners")
    public String getRunners(Model model) {
        List<RunnerEntity> runners = runnerRepository.findAll();

        model.addAttribute("runners", runners);
        double averageAge = Math.round(getAverageAge()*100.0)/100.0;
        model.addAttribute("averageAge", averageAge);
        return "runners";
    }

    @GetMapping("/runner/{id}")
    public String getRunnerById(@PathVariable int id, Model model) {
        RunnerEntity runner = runnerRepository.findById((long)id).orElse(null);
        if (runner != null) {
            model.addAttribute("runner", runner);
            List<ResultEntity> results = resultRepository.findByRunner(runner);
            model.addAttribute("results", results);
            return "runner";
        } else {
            return "error";
        }
    }

    @GetMapping("/runner/{id}/addrunres")
    public String showAddResultForm(@PathVariable Long id, Model model) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        model.addAttribute("runner", runner);                                   //adott a futó
        model.addAttribute("competitions", competitionRepository.findAll());    //és listából kiválasztható a verseny
        model.addAttribute("result", new ResultEntity());
        return "addrunres";
    }

    @PostMapping("/runner/{id}/addrunres")
    public String handleAddResultForm(@PathVariable Long id, @ModelAttribute ResultEntity result) {
        ResultEntity newResult = new ResultEntity();
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {                                           //itt megspóroltam a beírt adatok ellenőrzését,
            newResult.setRunner(runner);                                //ez további fejlesztési lehetőség
            newResult.setCompetition(result.getCompetition());          //pl. ugyanazt a versenyt csak egyszer futhatja le
            newResult.setResult(result.getResult());                    //az idő ne lehessen negatív stb.
            resultRepository.save(newResult);
        }
        return "redirect:/runner/" + id;
    }

    @GetMapping("/addrunner")
    public String showAddRunnerForm(Model model) {
        model.addAttribute("runner", new RunnerEntity());
        return "addrunner";
    }

    @PostMapping("/addrunner")
    public String handleAddRunnerForm(@ModelAttribute RunnerEntity runner, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (runner.getAge() <= 2 || runner.getAge() > 120) {
            redirectAttributes.addFlashAttribute("error", "Ennyi idősen nem lehet futni.");
            return "redirect:/addrunner";
        }
        if (runner.getRunnerName().length() < 3) {
            redirectAttributes.addFlashAttribute("error", "A futó neve túl rövid.");
            return "redirect:/addrunner";
        }
        if (runnerRepository.findByRunnerName(runner.getRunnerName()) != null) {
            redirectAttributes.addFlashAttribute("error", "Ez a futó már létezik.");
            return "redirect:/addrunner";
        }
        RunnerEntity newRunner = new RunnerEntity();
        newRunner.setRunnerName(runner.getRunnerName());
        newRunner.setAge(runner.getAge());
        newRunner.setGender(runner.getGender());
        runnerRepository.save(newRunner);
        return "redirect:/runners";
    }

    public double getAverageAge() {
        List<RunnerEntity> runners = runnerRepository.findAll();
        double totalAge = 0;
        for (RunnerEntity runner : runners) {
            totalAge += runner.getAge();
        }
        return (double) totalAge / runners.size();
    }
    
}
