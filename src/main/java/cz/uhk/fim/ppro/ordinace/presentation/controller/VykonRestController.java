package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.VykonService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vykony")
public class VykonRestController {

    private final VykonService vykonService;

    public VykonRestController(VykonService vykonService) {
        this.vykonService = vykonService;
    }

    @GetMapping
    public ResponseEntity<List<VykonDto>> getAllVykony() {
        return ResponseEntity.ok(vykonService.getAllAktivniVykony());
    }

    @GetMapping("/{kod}")
    public ResponseEntity<VykonDto> getVykonByKod(@PathVariable String kod) {
        return ResponseEntity.ok(vykonService.getVykonByKod(kod));
    }
}

