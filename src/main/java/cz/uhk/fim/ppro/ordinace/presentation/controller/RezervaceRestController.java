package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rezervace")
public class RezervaceRestController {

    private final RezervaceService rezervaceService;

    public RezervaceRestController(RezervaceService rezervaceService) {
        this.rezervaceService = rezervaceService;
    }

    @GetMapping
    public ResponseEntity<List<RezervaceResponseDto>> getAllRezervace(
            @RequestParam(required = false) UUID pacientId,
            @RequestParam(required = false) UUID lekarId) {
        if (pacientId != null) {
            return ResponseEntity.ok(rezervaceService.getRezervaceByPacient(pacientId));
        }
        if (lekarId != null) {
            return ResponseEntity.ok(rezervaceService.getRezervaceByLekarAndObdobi(
                    lekarId,
                    java.time.LocalDateTime.now().minusMonths(6),
                    java.time.LocalDateTime.now().plusMonths(6)
            ));
        }
        return ResponseEntity.ok(rezervaceService.getAllRezervace());
    }

    @PostMapping
    public ResponseEntity<RezervaceResponseDto> createRezervace(@Valid @RequestBody RezervaceCreateDto dto) {
        RezervaceResponseDto response = rezervaceService.createRezervace(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RezervaceResponseDto> getRezervaceById(@PathVariable UUID id) {
        return ResponseEntity.ok(rezervaceService.getRezervaceById(id));
    }

    @PostMapping("/{id}/zrusit")
    public ResponseEntity<RezervaceResponseDto> zrusitRezervaci(@PathVariable UUID id,
                                                                @Valid @RequestBody(required = false) RezervaceZrusitDto dto) {
        if (dto == null) {
            dto = new RezervaceZrusitDto();
        }
        return ResponseEntity.ok(rezervaceService.zrusitRezervaci(id, dto));
    }

    @GetMapping("/pacient/{pacientId}")
    public ResponseEntity<List<RezervaceResponseDto>> getByPacient(@PathVariable UUID pacientId) {
        return ResponseEntity.ok(rezervaceService.getRezervaceByPacient(pacientId));
    }

    @GetMapping("/lekar/{lekarId}/sloty")
    public ResponseEntity<List<SlotDto>> getSlotyProDen(
            @PathVariable UUID lekarId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return ResponseEntity.ok(rezervaceService.generujSlotyProDen(lekarId, datum));
    }

    @GetMapping("/call-list")
    public ResponseEntity<List<RezervaceResponseDto>> getCallList() {
        return ResponseEntity.ok(rezervaceService.getCallList());
    }

    @PostMapping("/nahlasit-nepritomnost")
    public ResponseEntity<List<RezervaceResponseDto>> nahlasitNepritomnost(@Valid @RequestBody NahlasitNepritomnostDto dto) {
        List<RezervaceResponseDto> dotcene = rezervaceService.nahlasitNepritomnost(dto);
        return ResponseEntity.ok(dotcene);
    }
}

