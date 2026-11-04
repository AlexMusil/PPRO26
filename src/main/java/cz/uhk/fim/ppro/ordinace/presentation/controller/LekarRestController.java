package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.LekarService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.LekarDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.OrdinacniDobaDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lekari")
public class LekarRestController {

    private final LekarService lekarService;

    public LekarRestController(LekarService lekarService) {
        this.lekarService = lekarService;
    }

    @GetMapping
    public ResponseEntity<List<LekarDto>> getAllLekari() {
        return ResponseEntity.ok(lekarService.getAllAktivniLekari());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LekarDto> getLekarById(@PathVariable UUID id) {
        return ResponseEntity.ok(lekarService.getLekarById(id));
    }

    @GetMapping("/{id}/ordinacni-doby")
    public ResponseEntity<List<OrdinacniDobaDto>> getOrdinacniDoby(@PathVariable UUID id) {
        return ResponseEntity.ok(lekarService.getOrdinacniDobyByLekar(id));
    }
}

