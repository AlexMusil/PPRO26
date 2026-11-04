package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.service.LekarService;
import cz.uhk.fim.ppro.ordinace.application.service.PacientService;
import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller
@RequestMapping("/kalendar")
public class KalendarWebController {

    private final RezervaceService rezervaceService;
    private final LekarService lekarService;
    private final PacientService pacientService;

    public KalendarWebController(RezervaceService rezervaceService,
                                 LekarService lekarService,
                                 PacientService pacientService) {
        this.rezervaceService = rezervaceService;
        this.lekarService = lekarService;
        this.pacientService = pacientService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) UUID lekarId,
            @RequestParam(required = false) UUID pacientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum,
            Model model) {

        List<LekarDto> lekari = lekarService.getAllAktivniLekari();
        model.addAttribute("lekari", lekari);

        UUID vybranyLekarId = lekarId;
        if (vybranyLekarId == null && !lekari.isEmpty()) {
            vybranyLekarId = lekari.get(0).getId();
        }

        LocalDate vybraneDatum = (datum != null) ? datum : LocalDate.now();
        LocalDate pondeli = vybraneDatum.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        model.addAttribute("vybranyLekarId", vybranyLekarId);
        model.addAttribute("vybraneDatum", vybraneDatum);
        model.addAttribute("pondeli", pondeli);
        model.addAttribute("predchoziTyden", pondeli.minusWeeks(1));
        model.addAttribute("dalsiTyden", pondeli.plusWeeks(1));

        if (vybranyLekarId != null) {
            LekarDto vybranyLekar = lekarService.getLekarById(vybranyLekarId);
            model.addAttribute("vybranyLekar", vybranyLekar);

            // Generování slotů pro 5 pracovních dnů v týdnu (Po-Pá)
            Map<LocalDate, List<SlotDto>> tydenSloty = new LinkedHashMap<>();
            for (int i = 0; i < 5; i++) {
                LocalDate den = pondeli.plusDays(i);
                List<SlotDto> slotyDne = rezervaceService.generujSlotyProDen(vybranyLekarId, den);
                tydenSloty.put(den, slotyDne);
            }
            model.addAttribute("tydenSloty", tydenSloty);
        }

        List<PacientResponseDto> pacienti = pacientService.getAllPacienti();
        model.addAttribute("pacienti", pacienti);

        if (!model.containsAttribute("novaRezervace")) {
            RezervaceCreateDto dto = new RezervaceCreateDto();
            dto.setLekarId(vybranyLekarId);
            if (pacientId != null) {
                dto.setPacientId(pacientId);
            }
            model.addAttribute("novaRezervace", dto);
        }

        return "kalendar";
    }

    @PostMapping("/rezervace/nova")
    public String vytvoritRezervaci(
            @Valid @ModelAttribute("novaRezervace") RezervaceCreateDto novaRezervace,
            BindingResult bindingResult,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.novaRezervace", bindingResult);
            redirectAttributes.addFlashAttribute("novaRezervace", novaRezervace);
            redirectAttributes.addFlashAttribute("chybaFormulare", "Zkontrolujte prosím zadané údaje rezervace.");
            return "redirect:/kalendar?lekarId=" + novaRezervace.getLekarId() + (datum != null ? "&datum=" + datum : "");
        }

        try {
            rezervaceService.createRezervace(novaRezervace);
            redirectAttributes.addFlashAttribute("uspechZprava", "Rezervace byla úspěšně vytvořena.");
        } catch (BusinessRuleException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("chybaFormulare", e.getMessage());
            redirectAttributes.addFlashAttribute("novaRezervace", novaRezervace);
        }

        return "redirect:/kalendar?lekarId=" + novaRezervace.getLekarId() +
                (novaRezervace.getCasOd() != null ? "&datum=" + novaRezervace.getCasOd().toLocalDate() : "");
    }

    @PostMapping("/rezervace/{id}/zrusit")
    public String zrusitRezervaci(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID lekarId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum,
            @RequestParam(required = false, defaultValue = "ZRUSENA_PACIENTEM") String typZruseni,
            @RequestParam(required = false) String poznamka,
            RedirectAttributes redirectAttributes) {

        try {
            RezervaceZrusitDto dto = new RezervaceZrusitDto(
                    "ZRUSENA_LEKAREM".equalsIgnoreCase(typZruseni) ? cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.ZRUSENA_LEKAREM : cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.ZRUSENA_PACIENTEM,
                    poznamka
            );
            rezervaceService.zrusitRezervaci(id, dto);
            redirectAttributes.addFlashAttribute("uspechZprava", "Rezervace byla zrušena a slot byl uvolněn.");
        } catch (BusinessRuleException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("chybaFormulare", e.getMessage());
        }

        return "redirect:/kalendar?lekarId=" + (lekarId != null ? lekarId : "") + (datum != null ? "&datum=" + datum : "");
    }
}

