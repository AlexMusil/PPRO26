package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.LekarService;
import cz.uhk.fim.ppro.ordinace.application.service.VykazService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.LekarDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykazMesicniDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/vykazy")
public class VykazWebController {

    private final VykazService vykazService;
    private final LekarService lekarService;

    public VykazWebController(VykazService vykazService, LekarService lekarService) {
        this.vykazService = vykazService;
        this.lekarService = lekarService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) Integer rok,
            @RequestParam(required = false) Integer mesic,
            @RequestParam(required = false) UUID lekarId,
            Model model) {

        LocalDate now = LocalDate.now();
        int r = (rok != null) ? rok : now.getYear();
        int m = (mesic != null) ? mesic : now.getMonthValue();

        List<LekarDto> lekari = lekarService.getAllAktivniLekari();
        model.addAttribute("lekari", lekari);
        model.addAttribute("vybranyLekarId", lekarId);
        model.addAttribute("rok", r);
        model.addAttribute("mesic", m);

        VykazMesicniDto vykaz = vykazService.generujMesicniVykaz(r, m, lekarId);
        model.addAttribute("vykaz", vykaz);

        return "vykazy";
    }
}

