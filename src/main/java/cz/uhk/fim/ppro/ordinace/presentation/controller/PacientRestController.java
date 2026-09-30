package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.PacientService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacienti")
public class PacientRestController {

    private final PacientService pacientService;

    public PacientRestController(PacientService pacientService) {
        this.pacientService = pacientService;
    }

    @GetMapping
    public ResponseEntity<List<PacientResponseDto>> getPacienti(
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(pacientService.searchPacienti(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacientResponseDto> getPacient(@PathVariable UUID id) {
        return ResponseEntity.ok(pacientService.getPacientById(id));
    }

    @PostMapping
    public ResponseEntity<PacientResponseDto> createPacient(@Valid @RequestBody PacientCreateDto dto) {
        PacientResponseDto created = pacientService.createPacient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePacient(@PathVariable UUID id) {
        pacientService.deletePacient(id);
        return ResponseEntity.noContent().build();
    }
}
