package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.EntityNotFoundException;
import cz.uhk.fim.ppro.ordinace.application.model.*;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.LekarRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.OrdinacniDobaRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.PacientRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RezervaceService {

    private final RezervaceRepository rezervaceRepository;
    private final PacientRepository pacientRepository;
    private final LekarRepository lekarRepository;
    private final OrdinacniDobaRepository ordinacniDobaRepository;

    public RezervaceService(RezervaceRepository rezervaceRepository,
                            PacientRepository pacientRepository,
                            LekarRepository lekarRepository,
                            OrdinacniDobaRepository ordinacniDobaRepository) {
        this.rezervaceRepository = rezervaceRepository;
        this.pacientRepository = pacientRepository;
        this.lekarRepository = lekarRepository;
        this.ordinacniDobaRepository = ordinacniDobaRepository;
    }

    public RezervaceResponseDto getRezervaceById(UUID id) {
        return rezervaceRepository.findById(id)
                .map(RezervaceResponseDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Rezervace s ID " + id + " nebyla nalezena."));
    }

    public List<RezervaceResponseDto> getRezervaceByPacient(UUID pacientId) {
        return rezervaceRepository.findByPacientIdOrderByCasOdDesc(pacientId)
                .stream()
                .map(RezervaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RezervaceResponseDto> getRezervaceByLekarAndObdobi(UUID lekarId, LocalDateTime od, LocalDateTime doCas) {
        return rezervaceRepository.findByLekarAndObdobi(lekarId, od, doCas)
                .stream()
                .map(RezervaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public long getPocetZrusenychRezervaciPacienta(UUID pacientId) {
        return rezervaceRepository.countByPacientIdAndStavIn(
                pacientId,
                List.of(StavRezervace.ZRUSENA_PACIENTEM)
        );
    }

    public List<RezervaceResponseDto> getCallList() {
        return rezervaceRepository.findVsechnyKPreobjednani()
                .stream()
                .map(RezervaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RezervaceResponseDto createRezervace(RezervaceCreateDto dto) {
        Pacient pacient = pacientRepository.findById(dto.getPacientId())
                .orElseThrow(() -> new EntityNotFoundException("Pacient s ID " + dto.getPacientId() + " nebyl nalezen."));

        Lekar lekar = lekarRepository.findById(dto.getLekarId())
                .orElseThrow(() -> new EntityNotFoundException("Lékař s ID " + dto.getLekarId() + " nebyl nalezen."));

        if (!lekar.isAktivni()) {
            throw new BusinessRuleException("Nelze vytvářet rezervace pro neaktivního lékaře.");
        }

        LocalDateTime casOd = dto.getCasOd();
        int delkaMinut = dto.getDelkaMinut() != null ? dto.getDelkaMinut() : 15;

        validujCasovouMrizku(casOd, delkaMinut);

        LocalDateTime casDo = casOd.plusMinutes(delkaMinut);

        validujOrdinacniHodiny(lekar.getId(), casOd, casDo);
        validujZadnyPrekryv(lekar.getId(), casOd, casDo, null);

        Rezervace rezervace = new Rezervace(
                UUID.randomUUID(),
                pacient,
                lekar,
                casOd,
                casDo,
                StavRezervace.PLANOVANA,
                dto.getDuvodNavstevy() != null ? dto.getDuvodNavstevy().trim() : ""
        );

        Rezervace saved = rezervaceRepository.save(rezervace);
        return RezervaceResponseDto.fromEntity(saved);
    }

    @Transactional
    public RezervaceResponseDto zrusitRezervaci(UUID id, RezervaceZrusitDto dto) {
        Rezervace rezervace = rezervaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rezervace s ID " + id + " nebyla nalezena."));

        if (rezervace.getStav() == StavRezervace.PROBEHLA) {
            throw new BusinessRuleException("Proběhlou rezervaci již nelze zrušit.");
        }

        StavRezervace novyStav = dto.getTypZruseni() != null ? dto.getTypZruseni() : StavRezervace.ZRUSENA_PACIENTEM;
        rezervace.setStav(novyStav);
        rezervace.setPoznamkaZruseni(dto.getPoznamka() != null ? dto.getPoznamka().trim() : null);

        Rezervace saved = rezervaceRepository.save(rezervace);
        return RezervaceResponseDto.fromEntity(saved);
    }

    @Transactional
    public List<RezervaceResponseDto> nahlasitNepritomnost(NahlasitNepritomnostDto dto) {
        if (dto.getOdCas().isAfter(dto.getDoCas())) {
            throw new BusinessRuleException("Čas začátku nepřítomnosti musí předcházet času konce.");
        }

        List<Rezervace> kolidujici = rezervaceRepository.findPlatneRezervaceVIntervalu(
                dto.getLekarId(),
                dto.getOdCas(),
                dto.getDoCas()
        );

        String duvodText = dto.getDuvod() != null && !dto.getDuvod().isBlank() ? dto.getDuvod().trim() : "Nemoc lékaře";

        for (Rezervace r : kolidujici) {
            r.setStav(StavRezervace.K_PREOBJEDNANI);
            r.setPoznamkaZruseni("Nepřítomnost lékaře: " + duvodText);
            rezervaceRepository.save(r);
        }

        return kolidujici.stream()
                .map(RezervaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void validujCasovouMrizku(LocalDateTime casOd, int delkaMinut) {
        if (delkaMinut < 15 || delkaMinut % 15 != 0) {
            throw new BusinessRuleException("Délka rezervace musí být celočíselným násobkem 15 minut (např. 15, 30, 45, 60 minut).");
        }
        int minuta = casOd.getMinute();
        if (minuta != 0 && minuta != 15 && minuta != 30 && minuta != 45) {
            throw new BusinessRuleException("Čas začátku rezervace musí začínat na 15minutové mřížce (např. :00, :15, :30, :45).");
        }
    }

    public void validujOrdinacniHodiny(UUID lekarId, LocalDateTime casOd, LocalDateTime casDo) {
        int denVTydnu = casOd.getDayOfWeek().getValue(); // 1 = Pondělí, ..., 7 = Neděle
        LocalTime casOdTime = casOd.toLocalTime();
        LocalTime casDoTime = casDo.toLocalTime();

        List<OrdinacniDoba> rozvrh = ordinacniDobaRepository.findByLekarIdAndDenVTydnu(lekarId, denVTydnu);

        if (rozvrh.isEmpty()) {
            throw new BusinessRuleException("Lékař v zadaný den v týdnu neordinuje.");
        }

        boolean vRameciHodin = false;
        for (OrdinacniDoba od : rozvrh) {
            if (!od.isLzeObjednavat()) {
                // Pracoviště kde nelze objednávat přes recepci (např. externí ambulance)
                if (casOdTime.isBefore(od.getCasDo()) && casDoTime.isAfter(od.getCasOd())) {
                    throw new BusinessRuleException("V tomto čase lékař ordinuje na externím pracovišti (" + od.getPracoviste() + "), kde nelze přijímat objednávky přes tuto ordinaci.");
                }
            } else {
                if (!casOdTime.isBefore(od.getCasOd()) && !casDoTime.isAfter(od.getCasDo())) {
                    vRameciHodin = true;
                    break;
                }
            }
        }

        if (!vRameciHodin) {
            throw new BusinessRuleException("Rezervaci lze vytvořit pouze v rámci platných ordinačních hodin lékaře pro tuto ordinaci.");
        }
    }

    public void validujZadnyPrekryv(UUID lekarId, LocalDateTime casOd, LocalDateTime casDo, UUID excludeId) {
        List<Rezervace> kolize;
        if (excludeId != null) {
            kolize = rezervaceRepository.findKolizeRezervaceMimoId(lekarId, casOd, casDo, excludeId);
        } else {
            kolize = rezervaceRepository.findKolizeRezervace(lekarId, casOd, casDo);
        }

        if (!kolize.isEmpty()) {
            Rezervace prvniKolize = kolize.get(0);
            throw new BusinessRuleException("Zvolený čas koliduje s jinou aktivní rezervací lékaře (" +
                    prvniKolize.getCasOd().toLocalTime() + " - " + prvniKolize.getCasDo().toLocalTime() + ").");
        }
    }

    public List<SlotDto> generujSlotyProDen(UUID lekarId, LocalDate datum) {
        int denVTydnu = datum.getDayOfWeek().getValue();
        List<OrdinacniDoba> rozvrh = ordinacniDobaRepository.findByLekarIdAndDenVTydnu(lekarId, denVTydnu);

        if (rozvrh.isEmpty()) {
            return Collections.emptyList();
        }

        // Najít celkové rozpětí ordinačních hodin pro daný den
        LocalTime minOd = rozvrh.stream().map(OrdinacniDoba::getCasOd).min(LocalTime::compareTo).orElse(LocalTime.of(8, 0));
        LocalTime maxDo = rozvrh.stream().map(OrdinacniDoba::getCasDo).max(LocalTime::compareTo).orElse(LocalTime.of(16, 0));

        LocalDateTime zacatekDne = datum.atTime(minOd);
        LocalDateTime konecDne = datum.atTime(maxDo);

        List<Rezervace> rezervaceDne = rezervaceRepository.findByLekarAndObdobi(lekarId, zacatekDne, konecDne);

        List<SlotDto> sloty = new ArrayList<>();
        LocalDateTime cur = zacatekDne;

        while (cur.isBefore(konecDne)) {
            LocalDateTime slotKonec = cur.plusMinutes(15);
            LocalTime curTime = cur.toLocalTime();
            LocalTime slotKonecTime = slotKonec.toLocalTime();

            // Kontrola, zda slot spadá do ordinační doby umožňující objednávání
            boolean vOrd = rozvrh.stream().anyMatch(od ->
                    od.isLzeObjednavat() &&
                    !curTime.isBefore(od.getCasOd()) &&
                    !slotKonecTime.isAfter(od.getCasDo())
            );

            SlotDto slot = new SlotDto(cur, slotKonec, true, vOrd);

            if (vOrd) {
                // Zjistit, zda je slot obsazen aktivní rezervací
                LocalDateTime curSlot = cur;
                Optional<Rezervace> obsazena = rezervaceDne.stream()
                        .filter(r -> r.getStav() == StavRezervace.PLANOVANA || r.getStav() == StavRezervace.PROBEHLA)
                        .filter(r -> r.getCasOd().isBefore(slotKonec) && r.getCasDo().isAfter(curSlot))
                        .findFirst();

                if (obsazena.isPresent()) {
                    Rezervace r = obsazena.get();
                    slot.setVolno(false);
                    slot.setRezervaceId(r.getId());
                    slot.setPacientJmeno(r.getPacient() != null ? r.getPacient().getCeleJmeno() : "");
                    slot.setPacientTelefon(r.getPacient() != null ? r.getPacient().getTelefon() : "");
                    slot.setDuvod(r.getDuvodNavstevy());
                    slot.setStav(r.getStav());
                }
            } else {
                slot.setVolno(false);
            }

            sloty.add(slot);
            cur = slotKonec;
        }

        return sloty;
    }
}
