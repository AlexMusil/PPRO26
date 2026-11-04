package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.DuplicateEntityException;
import cz.uhk.fim.ppro.ordinace.application.service.PacientService;
import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceResponseDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class PacientWebController {

    private final PacientService pacientService;
    private final RezervaceService rezervaceService;

    public PacientWebController(PacientService pacientService, RezervaceService rezervaceService) {
        this.pacientService = pacientService;
        this.rezervaceService = rezervaceService;
    }

    @GetMapping({"", "/", "/pacienti"})
    public String index(@RequestParam(required = false) String query, Model model) {
        List<PacientResponseDto> pacienti = pacientService.searchPacienti(query);
        model.addAttribute("pacienti", pacienti);
        model.addAttribute("query", query != null ? query : "");
        if (!model.containsAttribute("novyPacient")) {
            model.addAttribute("novyPacient", new PacientCreateDto());
        }
        return "pacienti";
    }

    @GetMapping("/pacienti/{id}")
    public String detailPacienta(@PathVariable UUID id, Model model) {
        PacientResponseDto pacient = pacientService.getPacientById(id);
        List<RezervaceResponseDto> rezervace = rezervaceService.getRezervaceByPacient(id);
        model.addAttribute("pacient", pacient);
        model.addAttribute("rezervace", rezervace);
        return "pacient-detail";
    }

    @PostMapping("/pacienti/novy")
    public String vytvoritPacienta(
            @Valid @ModelAttribute("novyPacient") PacientCreateDto novyPacient,
            BindingResult bindingResult,
            @RequestParam(required = false) String query,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<PacientResponseDto> pacienti = pacientService.searchPacienti(query);
            model.addAttribute("pacienti", pacienti);
            model.addAttribute("query", query != null ? query : "");
            model.addAttribute("chybaFormulare", "Ve formuláři jsou chyby, zkontrolujte zadané údaje.");
            return "pacienti";
        }

        try {
            pacientService.createPacient(novyPacient);
            redirectAttributes.addFlashAttribute("uspechZprava",
                    "Pacient " + novyPacient.getJmeno() + " " + novyPacient.getPrijmeni() + " byl úspěšně zaevidován.");
            return "redirect:/";
        } catch (DuplicateEntityException e) {
            List<PacientResponseDto> pacienti = pacientService.searchPacienti(query);
            model.addAttribute("pacienti", pacienti);
            model.addAttribute("query", query != null ? query : "");
            model.addAttribute("duplicitaChyba", e.getMessage());
            return "pacienti";
        } catch (BusinessRuleException e) {
            List<PacientResponseDto> pacienti = pacientService.searchPacienti(query);
            model.addAttribute("pacienti", pacienti);
            model.addAttribute("query", query != null ? query : "");
            model.addAttribute("chybaFormulare", e.getMessage());
            return "pacienti";
        }
    }
}
