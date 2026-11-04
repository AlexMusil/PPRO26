package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.*;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.NavstevaRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.VykonRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaVykonPolozkaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NavstevaService {

    private final NavstevaRepository navstevaRepository;
    private final RezervaceRepository rezervaceRepository;
    private final VykonRepository vykonRepository;

    public NavstevaService(NavstevaRepository navstevaRepository,
                           RezervaceRepository rezervaceRepository,
                           VykonRepository vykonRepository) {
        this.navstevaRepository = navstevaRepository;
        this.rezervaceRepository = rezervaceRepository;
        this.vykonRepository = vykonRepository;
    }

    public List<NavstevaResponseDto> getAllNavstevy() {
        return navstevaRepository.findAll()
                .stream()
                .map(NavstevaResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public NavstevaResponseDto getNavstevaById(UUID id) {
        return navstevaRepository.findById(id)
                .map(NavstevaResponseDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Návštěva s ID " + id + " nebyla nalezena."));
    }

    public NavstevaResponseDto getNavstevaByRezervaceId(UUID rezervaceId) {
        return navstevaRepository.findByRezervaceId(rezervaceId)
                .map(NavstevaResponseDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Pro rezervaci " + rezervaceId + " nebyla nalezena žádná realizovaná návštěva."));
    }

    @Transactional
    public NavstevaResponseDto createNavsteva(NavstevaCreateDto dto) {
        Rezervace rezervace = rezervaceRepository.findById(dto.getRezervaceId())
                .orElseThrow(() -> new EntityNotFoundException("Rezervace s ID " + dto.getRezervaceId() + " nebyla nalezena."));

        if (rezervace.getStav() == StavRezervace.PROBEHLA || navstevaRepository.findByRezervaceId(rezervace.getId()).isPresent()) {
            throw new BusinessRuleException("K této rezervaci již byla návštěva zaevidována.");
        }

        if (rezervace.getStav() == StavRezervace.ZRUSENA_PACIENTEM || rezervace.getStav() == StavRezervace.ZRUSENA_LEKAREM) {
            throw new BusinessRuleException("Nelze zaevidovat návštěvu ke zrušené rezervaci.");
        }

        // Obchodní pravidlo: Zákaz vykazování do budoucna
        if (rezervace.getCasOd().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Zdravotní výkon a návštěvu nelze přiřadit k termínu v budoucnosti, který se ještě neuskutečnil.");
        }

        LocalDateTime casNavstevy = dto.getDatumCas() != null ? dto.getDatumCas() : LocalDateTime.now();
        if (casNavstevy.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Datum a čas návštěvy nemůže být v budoucnosti.");
        }

        Navsteva navsteva = new Navsteva(
                UUID.randomUUID(),
                rezervace,
                casNavstevy,
                dto.getLekarskaZprava() != null ? dto.getLekarskaZprava().trim() : "",
                dto.getDoporuceni() != null ? dto.getDoporuceni().trim() : null
        );

        // Zpracování výkonů (M:N vazba s atributem pocet)
        if (dto.getVykony() != null) {
            for (NavstevaVykonPolozkaDto polozka : dto.getVykony()) {
                if (polozka.getVykonKod() == null || polozka.getVykonKod().isBlank()) {
                    continue;
                }
                Vykon vykon = vykonRepository.findByKod(polozka.getVykonKod().trim())
                        .orElseThrow(() -> new EntityNotFoundException("Výkon s kódem '" + polozka.getVykonKod() + "' nebyl nalezen."));

                int pocet = polozka.getPocet() != null && polozka.getPocet() > 0 ? polozka.getPocet() : 1;
                String poznamka = polozka.getPoznamka() != null ? polozka.getPoznamka().trim() : null;

                navsteva.addVykon(vykon, pocet, poznamka);
            }
        }

        Navsteva savedNavsteva = navstevaRepository.save(navsteva);

        // Aktualizace stavu rezervace na PROBEHLA
        rezervace.setStav(StavRezervace.PROBEHLA);
        rezervace.setNavsteva(savedNavsteva);
        rezervaceRepository.save(rezervace);

        return NavstevaResponseDto.fromEntity(savedNavsteva);
    }
}

