package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.Vykon;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.VykonRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykonDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VykonService {

    private final VykonRepository vykonRepository;

    public VykonService(VykonRepository vykonRepository) {
        this.vykonRepository = vykonRepository;
    }

    public List<VykonDto> getAllAktivniVykony() {
        return vykonRepository.findAllByAktivniTrueOrderByKodAsc()
                .stream()
                .map(VykonDto::fromEntity)
                .collect(Collectors.toList());
    }

    public VykonDto getVykonByKod(String kod) {
        return vykonRepository.findByKod(kod)
                .map(VykonDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Výkon s kódem '" + kod + "' nebyl nalezen."));
    }

    public Vykon getVykonEntity(String kod) {
        return vykonRepository.findByKod(kod)
                .orElseThrow(() -> new EntityNotFoundException("Výkon s kódem '" + kod + "' nebyl nalezen."));
    }
}
