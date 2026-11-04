package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;
import cz.uhk.fim.ppro.ordinace.application.service.NavstevaService;
import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.application.service.VykonService;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaVykonPolozkaDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykonDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/navstevy")
public class NavstevaWebController {

    private final NavstevaService navstevaService;
    private final RezervaceRepository rezervaceRepository;
    private final VykonService vykonService;

    public NavstevaWebController(NavstevaService navstevaService,
                                 RezervaceRepository rezervaceRepository,
                                 VykonService vykonService) {
        this.navstevaService = navstevaService;
        this.rezervaceRepository = rezervaceRepository;
        this.vykonService = vykonService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) UUID rezervaceId,
            Model model) {

        List<NavstevaResponseDto> navstevy = navstevaService.getAllNavstevy();
        model.addAttribute("navstevy", navstevy);

        List<VykonDto> vykony = vykonService.getAllAktivniVykony();
        model.addAttribute("vykony", vykony);

        // Najít rezervace způsobilé k realizaci: stav PLANOVANA s časem od <= now()
        List<RezervaceResponseDto> zpusobileRezervace = rezervaceRepository.findAll()
                .stream()
                .filter(r -> r.getStav() == StavRezervace.PLANOVANA && !r.getCasOd().isAfter(LocalDateTime.now()))
                .map(RezervaceResponseDto::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("zpusobileRezervace", zpusobileRezervace);

        if (!model.containsAttribute("novaNavsteva")) {
            NavstevaCreateDto dto = new NavstevaCreateDto();
            if (rezervaceId != null) {
                dto.setRezervaceId(rezervaceId);
            }
            // Přidáme 3 prázdné položky pro výběr výkonů ve formuláři
            dto.getVykony().add(new NavstevaVykonPolozkaDto("", 1, ""));
            dto.getVykony().add(new NavstevaVykonPolozkaDto("", 1, ""));
            dto.getVykony().add(new NavstevaVykonPolozkaDto("", 1, ""));
            model.addAttribute("novaNavsteva", dto);
        }

        return "navstevy";
    }

    @PostMapping("/nova")
    public String vytvoritNavstevu(
            @Valid @ModelAttribute("novaNavsteva") NavstevaCreateDto novaNavsteva,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.novaNavsteva", bindingResult);
            redirectAttributes.addFlashAttribute("novaNavsteva", novaNavsteva);
            redirectAttributes.addFlashAttribute("chybaFormulare", "Zkontrolujte prosím zadané údaje návštěvy.");
            return "redirect:/navstevy";
        }

        try {
            navstevaService.createNavsteva(novaNavsteva);
            redirectAttributes.addFlashAttribute("uspechZprava", "Návštěva a zdravotní výkony byly úspěšně zaevidovány.");
        } catch (BusinessRuleException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("chybaFormulare", e.getMessage());
            redirectAttributes.addFlashAttribute("novaNavsteva", novaNavsteva);
        }

        return "redirect:/navstevy";
    }
}

