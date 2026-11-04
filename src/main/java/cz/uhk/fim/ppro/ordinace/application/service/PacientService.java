package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.DuplicateEntityException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.Pacient;
import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;
import cz.uhk.fim.ppro.ordinace.application.validation.RodneCisloValidator;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.PacientRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PacientService {

    private final PacientRepository pacientRepository;
    private final RezervaceRepository rezervaceRepository;

    public PacientService(PacientRepository pacientRepository, RezervaceRepository rezervaceRepository) {
        this.pacientRepository = pacientRepository;
        this.rezervaceRepository = rezervaceRepository;
    }

    public List<PacientResponseDto> getAllPacienti() {
        return pacientRepository.findAllByOrderByPrijmeniAscJmenoAsc()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PacientResponseDto> searchPacienti(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPacienti();
        }
        return pacientRepository.searchPacienti(query.trim())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public PacientResponseDto getPacientById(UUID id) {
        return pacientRepository.findById(id)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Pacient s ID " + id + " nebyl nalezen."));
    }

    private PacientResponseDto mapToResponseDto(Pacient p) {
        long zruseno = 0;
        if (rezervaceRepository != null) {
            zruseno = rezervaceRepository.countByPacientIdAndStavIn(p.getId(), List.of(StavRezervace.ZRUSENA_PACIENTEM));
        }
        return PacientResponseDto.fromEntity(p, zruseno);
    }

    @Transactional
    public PacientResponseDto createPacient(PacientCreateDto dto) {
        String cleanCisloPojistence = dto.getCisloPojistence() != null ? dto.getCisloPojistence().trim().replaceAll("\\s+", "") : "";
        String cleanTelefon = dto.getTelefon() != null ? dto.getTelefon().trim() : "";
        String cleanJmeno = dto.getJmeno() != null ? dto.getJmeno().trim() : "";
        String cleanPrijmeni = dto.getPrijmeni() != null ? dto.getPrijmeni().trim() : "";

        if (!RodneCisloValidator.isValid(cleanCisloPojistence)) {
            throw new BusinessRuleException("Číslo pojištěnce '" + cleanCisloPojistence + "' není platné rodné číslo.");
        }

        if (dto.getRokNarozeni() != null && !RodneCisloValidator.matchesBirthYear(cleanCisloPojistence, dto.getRokNarozeni())) {
            throw new BusinessRuleException("Rok narození (" + dto.getRokNarozeni() + ") neodpovídá rodnému číslu '" + cleanCisloPojistence + "'.");
        }

        String formattedRc = RodneCisloValidator.format(cleanCisloPojistence);

        if (pacientRepository.existsByCisloPojistence(formattedRc)) {
            throw new DuplicateEntityException("Pacient s číslem pojištěnce '" + formattedRc + "' již v evidenci existuje.");
        }

        if (pacientRepository.existsByJmenoIgnoreCaseAndPrijmeniIgnoreCaseAndTelefon(cleanJmeno, cleanPrijmeni, cleanTelefon)) {
            throw new DuplicateEntityException("Pacient se jménem '" + cleanJmeno + " " + cleanPrijmeni + "' a telefonním číslem již existuje.");
        }

        Pacient pacient = new Pacient(
                UUID.randomUUID(),
                cleanJmeno,
                cleanPrijmeni,
                dto.getRokNarozeni(),
                cleanTelefon,
                formattedRc,
                dto.getPojistovna() != null ? dto.getPojistovna().trim() : "",
                dto.getPoznamka() != null ? dto.getPoznamka().trim() : null
        );

        Pacient saved = pacientRepository.save(pacient);
        return PacientResponseDto.fromEntity(saved);
    }

    public void deletePacient(UUID id) {
        throw new BusinessRuleException("Záznam pacienta nelze smazat.");
    }
}
