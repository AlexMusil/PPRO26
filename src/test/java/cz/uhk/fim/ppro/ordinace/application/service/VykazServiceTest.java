package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.model.*;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.LekarRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.NavstevaRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykazMesicniDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VykazServiceTest {

    @Mock
    private NavstevaRepository navstevaRepository;
    @Mock
    private LekarRepository lekarRepository;

    @InjectMocks
    private VykazService vykazService;

    private Lekar lekar;
    private Pacient pacient;
    private Vykon vysetreni;
    private Vykon injekce;

    @BeforeEach
    void setUp() {
        lekar = new Lekar(UUID.randomUUID(), "MUDr.", "Jana", "Hrubá", null, "Všeobecné lékařství", null, null);
        pacient = new Pacient(UUID.randomUUID(), "Marie", "Nováková", 1980, "+420 777 123 456", "805212/1231", "111", null);
        vysetreni = new Vykon("09513", "Cílené vyšetření", 280, null, true);
        injekce = new Vykon("09115", "Injekce", 65, null, true);
    }

    @Test
    @DisplayName("Výpočet měsíčního výkazu a agregace bodů pro pojišťovnu")
    void testGenerujMesicniVykaz_KalkulaceBodu() {
        Rezervace r1 = new Rezervace(UUID.randomUUID(), pacient, lekar,
                LocalDateTime.of(2026, 10, 12, 8, 30),
                LocalDateTime.of(2026, 10, 12, 9, 0),
                StavRezervace.PROBEHLA, "Kontrola");

        Navsteva n1 = new Navsteva(UUID.randomUUID(), r1, LocalDateTime.of(2026, 10, 12, 8, 35), "Zpráva 1", null);
        n1.addVykon(vysetreni, 1, null); // 280 b.
        n1.addVykon(injekce, 2, null);   // 130 b.

        when(lekarRepository.findById(lekar.getId())).thenReturn(Optional.of(lekar));
        when(navstevaRepository.findNavstevyProVykaz(eq(lekar.getId()), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(n1));

        VykazMesicniDto vykaz = vykazService.generujMesicniVykaz(2026, 10, lekar.getId());

        assertNotNull(vykaz);
        assertEquals(2026, vykaz.getRok());
        assertEquals(10, vykaz.getMesic());
        assertEquals(1, vykaz.getCelkemNavstev());
        assertEquals(410, vykaz.getCelkemBodu());
        assertEquals(2, vykaz.getPolozky().size());
    }
}
