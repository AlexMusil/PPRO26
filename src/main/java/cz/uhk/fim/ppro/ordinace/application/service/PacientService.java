package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.DuplicateEntityException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.Pacient;
import cz.uhk.fim.ppro.ordinace.application.validation.RodneCisloValidator;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.PacientRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aplikační služba pro správu pacientů.
 * Implementuje doménová pravidla klienta (ochrana před duplicitami, validace rodného čísla, zákaz mazání historie).
 */
@Service
@Transactional(readOnly = true)
public class PacientService {

    private final PacientRepository pacientRepository;

    public PacientService(PacientRepository pacientRepository) {
        this.pacientRepository = pacientRepository;
    }

    public List<PacientResponseDto> getAllPacienti() {
        return pacientRepository.findAllByOrderByPrijmeniAscJmenoAsc()
                .stream()
                .map(PacientResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PacientResponseDto> searchPacienti(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPacienti();
        }
        return pacientRepository.searchPacienti(query.trim())
                .stream()
                .map(PacientResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PacientResponseDto getPacientById(UUID id) {
        return pacientRepository.findById(id)
                .map(PacientResponseDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Pacient s ID " + id + " nebyl nalezen."));
    }

    @Transactional
    public PacientResponseDto createPacient(PacientCreateDto dto) {
        String cleanCisloPojistence = dto.getCisloPojistence() != null ? dto.getCisloPojistence().trim().replaceAll("\\s+", "") : "";
        String cleanTelefon = dto.getTelefon() != null ? dto.getTelefon().trim() : "";
        String cleanJmeno = dto.getJmeno() != null ? dto.getJmeno().trim() : "";
        String cleanPrijmeni = dto.getPrijmeni() != null ? dto.getPrijmeni().trim() : "";

        // Doménová validace platnosti rodného čísla (formát, kalendářní datum, modulo 11)
        if (!RodneCisloValidator.isValid(cleanCisloPojistence)) {
            throw new BusinessRuleException("Číslo pojištěnce '" + cleanCisloPojistence + "' není platné české rodné číslo (neodpovídá formátu, datu nebo kontrolnímu součtu).");
        }

        // Kontrola souladu roku narození s rodným číslem
        if (dto.getRokNarozeni() != null && !RodneCisloValidator.matchesBirthYear(cleanCisloPojistence, dto.getRokNarozeni())) {
            throw new BusinessRuleException("Rok narození (" + dto.getRokNarozeni() + ") neodpovídá roku uvedenému v rodném čísle '" + cleanCisloPojistence + "'.");
        }

        String formattedRc = RodneCisloValidator.format(cleanCisloPojistence);

        // Pravidlo #4: Unikátnost čísla pojištěnce (rodného čísla)
        if (pacientRepository.existsByCisloPojistence(formattedRc)) {
            throw new DuplicateEntityException("Pacient s číslem pojištěnce '" + formattedRc + "' již v evidenci existuje.");
        }

        // Pravidlo #4: Detekce duplicitního zápisu téže osoby se shodným telefonem
        if (pacientRepository.existsByJmenoIgnoreCaseAndPrijmeniIgnoreCaseAndTelefon(cleanJmeno, cleanPrijmeni, cleanTelefon)) {
            throw new DuplicateEntityException("Pacient se shodným jménem '" + cleanJmeno + " " + cleanPrijmeni + "' a telefonním číslem již v evidenci existuje.");
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

    /**
     * Klientovo striktní pravidlo: „Historie pacienta se nemaže. Když se něco stane, musím doložit, co jsme kdy dělali.“
     */
    public void deletePacient(UUID id) {
        throw new BusinessRuleException("Smazání pacienta je zakázáno. Podle platných lékařských pravidel a zadání klienta se historie nesmí fyzicky odstraňovat.");
    }
}
