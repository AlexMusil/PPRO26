package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.Lekar;
import cz.uhk.fim.ppro.ordinace.application.model.OrdinacniDoba;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.LekarRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.OrdinacniDobaRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.LekarDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.OrdinacniDobaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LekarService {

    private final LekarRepository lekarRepository;
    private final OrdinacniDobaRepository ordinacniDobaRepository;

    public LekarService(LekarRepository lekarRepository, OrdinacniDobaRepository ordinacniDobaRepository) {
        this.lekarRepository = lekarRepository;
        this.ordinacniDobaRepository = ordinacniDobaRepository;
    }

    public List<LekarDto> getAllAktivniLekari() {
        return lekarRepository.findAllByAktivniTrueOrderByPrijmeniAscJmenoAsc()
                .stream()
                .map(LekarDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LekarDto getLekarById(UUID id) {
        return lekarRepository.findById(id)
                .map(LekarDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Lékař s ID " + id + " nebyl nalezen."));
    }

    public Lekar getLekarEntity(UUID id) {
        return lekarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lékař s ID " + id + " nebyl nalezen."));
    }

    public List<OrdinacniDobaDto> getOrdinacniDobyByLekar(UUID lekarId) {
        return ordinacniDobaRepository.findByLekarIdOrderByDenVTydnuAscCasOdAsc(lekarId)
                .stream()
                .map(OrdinacniDobaDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrdinacniDoba> getOrdinacniDobyEntityByLekar(UUID lekarId) {
        return ordinacniDobaRepository.findByLekarIdOrderByDenVTydnuAscCasOdAsc(lekarId);
    }
}

