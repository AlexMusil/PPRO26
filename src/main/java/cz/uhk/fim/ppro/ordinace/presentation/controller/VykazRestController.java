package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.VykazService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykazMesicniDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/vykazy")
public class VykazRestController {

    private final VykazService vykazService;

    public VykazRestController(VykazService vykazService) {
        this.vykazService = vykazService;
    }

    @GetMapping("/mesicni")
    public ResponseEntity<VykazMesicniDto> getMesicniVykaz(
            @RequestParam(required = false) Integer rok,
            @RequestParam(required = false) Integer mesic,
            @RequestParam(required = false) UUID lekarId) {
        LocalDate today = LocalDate.now();
        int r = (rok != null) ? rok : today.getYear();
        int m = (mesic != null) ? mesic : today.getMonthValue();

        return ResponseEntity.ok(vykazService.generujMesicniVykaz(r, m, lekarId));
    }
}
