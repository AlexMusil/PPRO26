package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.service.LekarService;
import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.LekarDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NahlasitNepritomnostDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceResponseDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/call-list")
public class CallListWebController {

    private final RezervaceService rezervaceService;
    private final LekarService lekarService;

    public CallListWebController(RezervaceService rezervaceService, LekarService lekarService) {
        this.rezervaceService = rezervaceService;
        this.lekarService = lekarService;
    }

    @GetMapping
    public String index(Model model) {
        List<RezervaceResponseDto> callList = rezervaceService.getCallList();
        model.addAttribute("callList", callList);

        List<LekarDto> lekari = lekarService.getAllAktivniLekari();
        model.addAttribute("lekari", lekari);

        if (!model.containsAttribute("novaNepritomnost")) {
            NahlasitNepritomnostDto dto = new NahlasitNepritomnostDto();
            dto.setOdCas(LocalDateTime.now().withMinute(0).withSecond(0).plusHours(1));
            dto.setDoCas(LocalDateTime.now().withMinute(0).withSecond(0).plusDays(1));
            model.addAttribute("novaNepritomnost", dto);
        }

        return "call-list";
    }

    @PostMapping("/nahlasit-nepritomnost")
    public String nahlasitNepritomnost(
            @Valid @ModelAttribute("novaNepritomnost") NahlasitNepritomnostDto novaNepritomnost,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.novaNepritomnost", bindingResult);
            redirectAttributes.addFlashAttribute("novaNepritomnost", novaNepritomnost);
            redirectAttributes.addFlashAttribute("chybaFormulare", "Zkontrolujte prosím zadané údaje o nepřítomnosti.");
            return "redirect:/call-list";
        }

        try {
            List<RezervaceResponseDto> dotcene = rezervaceService.nahlasitNepritomnost(novaNepritomnost);
            redirectAttributes.addFlashAttribute("uspechZprava", "Nepřítomnost lékaře byla zaevidována. Celkem " +
                    dotcene.size() + " rezervací bylo převedeno do Call-listu k přeobjednání.");
        } catch (BusinessRuleException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("chybaFormulare", e.getMessage());
            redirectAttributes.addFlashAttribute("novaNepritomnost", novaNepritomnost);
        }

        return "redirect:/call-list";
    }
}
