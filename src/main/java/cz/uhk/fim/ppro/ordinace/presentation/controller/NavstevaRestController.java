package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.NavstevaService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/navstevy")
public class NavstevaRestController {

    private final NavstevaService navstevaService;

    public NavstevaRestController(NavstevaService navstevaService) {
        this.navstevaService = navstevaService;
    }

    @GetMapping
    public ResponseEntity<List<NavstevaResponseDto>> getAllNavstevy() {
        return ResponseEntity.ok(navstevaService.getAllNavstevy());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NavstevaResponseDto> getNavstevaById(@PathVariable UUID id) {
        return ResponseEntity.ok(navstevaService.getNavstevaById(id));
    }

    @GetMapping("/rezervace/{rezervaceId}")
    public ResponseEntity<NavstevaResponseDto> getByRezervaceId(@PathVariable UUID rezervaceId) {
        return ResponseEntity.ok(navstevaService.getNavstevaByRezervaceId(rezervaceId));
    }

    @PostMapping
    public ResponseEntity<NavstevaResponseDto> createNavsteva(@Valid @RequestBody NavstevaCreateDto dto) {
        NavstevaResponseDto created = navstevaService.createNavsteva(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

