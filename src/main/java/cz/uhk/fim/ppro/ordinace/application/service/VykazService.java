package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.model.Lekar;
import cz.uhk.fim.ppro.ordinace.application.model.Navsteva;
import cz.uhk.fim.ppro.ordinace.application.model.NavstevaVykon;
import cz.uhk.fim.ppro.ordinace.application.model.Vykon;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.LekarRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.NavstevaRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykazMesicniDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykazPolozkaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class VykazService {

    private final NavstevaRepository navstevaRepository;
    private final LekarRepository lekarRepository;

    public VykazService(NavstevaRepository navstevaRepository, LekarRepository lekarRepository) {
        this.navstevaRepository = navstevaRepository;
        this.lekarRepository = lekarRepository;
    }

    public VykazMesicniDto generujMesicniVykaz(int rok, int mesic, UUID lekarId) {
        YearMonth ym = YearMonth.of(rok, mesic);
        LocalDateTime zacatek = ym.atDay(1).atStartOfDay();
        LocalDateTime konec = ym.atEndOfMonth().atTime(23, 59, 59);

        String lekarJmeno = "Všichni lékaři";
        if (lekarId != null) {
            Lekar lekar = lekarRepository.findById(lekarId).orElse(null);
            if (lekar != null) {
                lekarJmeno = lekar.getCeleJmeno();
            }
        }

        List<Navsteva> navstevy = navstevaRepository.findNavstevyProVykaz(lekarId, zacatek, konec);

        Map<String, VykazPolozkaDto> agregace = new LinkedHashMap<>();
        int celkemBodu = 0;

        for (Navsteva n : navstevy) {
            if (n.getVykony() != null) {
                for (NavstevaVykon nv : n.getVykony()) {
                    Vykon v = nv.getVykon();
                    if (v == null) continue;

                    String kod = v.getKod();
                    int pocet = nv.getPocet() != null ? nv.getPocet() : 1;
                    int bodyZaKus = v.getBody() != null ? v.getBody() : 0;
                    int celkemZaPolozku = bodyZaKus * pocet;

                    celkemBodu += celkemZaPolozku;

                    if (!agregace.containsKey(kod)) {
                        agregace.put(kod, new VykazPolozkaDto(kod, v.getNazev(), bodyZaKus, pocet, celkemZaPolozku));
                    } else {
                        VykazPolozkaDto exist = agregace.get(kod);
                        exist.setCelkemPocet(exist.getCelkemPocet() + pocet);
                        exist.setCelkemBodu(exist.getCelkemBodu() + celkemZaPolozku);
                    }
                }
            }
        }

        VykazMesicniDto report = new VykazMesicniDto(rok, mesic, lekarId, lekarJmeno);
        report.setCelkemNavstev(navstevy.size());
        report.setCelkemBodu(celkemBodu);
        report.setPolozky(new ArrayList<>(agregace.values()));

        return report;
    }
}

