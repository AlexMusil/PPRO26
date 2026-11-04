package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.LekarService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.LekarDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.OrdinacniDobaDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/lekari")
public class LekarWebController {

    private final LekarService lekarService;

    public LekarWebController(LekarService lekarService) {
        this.lekarService = lekarService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) UUID lekarId, Model model) {
        List<LekarDto> lekari = lekarService.getAllAktivniLekari();
        model.addAttribute("lekari", lekari);

        UUID vybranyLekarId = lekarId;
        if (vybranyLekarId == null && !lekari.isEmpty()) {
            vybranyLekarId = lekari.get(0).getId();
        }

        if (vybranyLekarId != null) {
            LekarDto vybranyLekar = lekarService.getLekarById(vybranyLekarId);
            List<OrdinacniDobaDto> rozvrh = lekarService.getOrdinacniDobyByLekar(vybranyLekarId);
            model.addAttribute("vybranyLekar", vybranyLekar);
            model.addAttribute("rozvrh", rozvrh);
        }

        return "lekari";
    }
}

