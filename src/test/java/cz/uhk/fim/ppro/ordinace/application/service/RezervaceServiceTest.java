package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.model.*;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.LekarRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.OrdinacniDobaRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.PacientRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NahlasitNepritomnostDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceZrusitDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RezervaceServiceTest {

    @Mock
    private RezervaceRepository rezervaceRepository;
    @Mock
    private PacientRepository pacientRepository;
    @Mock
    private LekarRepository lekarRepository;
    @Mock
    private OrdinacniDobaRepository ordinacniDobaRepository;

    @InjectMocks
    private RezervaceService rezervaceService;

    private Pacient pacient;
    private Lekar lekar;
    private OrdinacniDoba ordinacniDoba;
    private LocalDateTime pondeliRano;

    @BeforeEach
    void setUp() {
        pacient = new Pacient(UUID.randomUUID(), "Marie", "Nováková", 1980, "+420 777 123 456", "805212/1231", "111", null);
        lekar = new Lekar(UUID.randomUUID(), "MUDr.", "Jana", "Hrubá", null, "Všeobecné lékařství", "+420 495 123 451", "hruba@ordinace.cz");
        ordinacniDoba = new OrdinacniDoba(UUID.randomUUID(), lekar, 1, LocalTime.of(8, 0), LocalTime.of(13, 0), "Ordinace Na Vyhlídce", true, null);

        LocalDate pristiPondeli = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        pondeliRano = pristiPondeli.atTime(8, 30);
    }

    @Test
    @DisplayName("Úspěšné vytvoření rezervace v platných ordinačních hodinách")
    void testCreateRezervace_Success() {
        RezervaceCreateDto dto = new RezervaceCreateDto(pacient.getId(), lekar.getId(), pondeliRano, 30, "Kontrola tlaku");

        when(pacientRepository.findById(pacient.getId())).thenReturn(Optional.of(pacient));
        when(lekarRepository.findById(lekar.getId())).thenReturn(Optional.of(lekar));
        when(ordinacniDobaRepository.findByLekarIdAndDenVTydnu(lekar.getId(), 1)).thenReturn(List.of(ordinacniDoba));
        when(rezervaceRepository.findKolizeRezervace(lekar.getId(), pondeliRano, pondeliRano.plusMinutes(30))).thenReturn(Collections.emptyList());
        when(rezervaceRepository.save(any(Rezervace.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RezervaceResponseDto created = rezervaceService.createRezervace(dto);

        assertNotNull(created);
        assertEquals(pondeliRano, created.getCasOd());
        assertEquals(30, created.getDelkaMinut());
        assertEquals(StavRezervace.PLANOVANA, created.getStav());
        verify(rezervaceRepository, times(1)).save(any(Rezervace.class));
    }

    @Test
    @DisplayName("Odmítnutí rezervace při kolizi s existujícím termínem")
    void testCreateRezervace_KolizeSeStavajiciRezervaci_ThrowsException() {
        RezervaceCreateDto dto = new RezervaceCreateDto(pacient.getId(), lekar.getId(), pondeliRano, 30, "Kontrola");

        Rezervace existujici = new Rezervace(UUID.randomUUID(), pacient, lekar, pondeliRano, pondeliRano.plusMinutes(30), StavRezervace.PLANOVANA, "Jiný pacient");

        when(pacientRepository.findById(pacient.getId())).thenReturn(Optional.of(pacient));
        when(lekarRepository.findById(lekar.getId())).thenReturn(Optional.of(lekar));
        when(ordinacniDobaRepository.findByLekarIdAndDenVTydnu(lekar.getId(), 1)).thenReturn(List.of(ordinacniDoba));
        when(rezervaceRepository.findKolizeRezervace(lekar.getId(), pondeliRano, pondeliRano.plusMinutes(30))).thenReturn(List.of(existujici));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> rezervaceService.createRezervace(dto));
        assertTrue(ex.getMessage().contains("koliduje s jinou aktivní rezervací"));
        verify(rezervaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Odmítnutí rezervace mimo ordinační hodiny lékaře")
    void testCreateRezervace_MimoOrdinacniHodiny_ThrowsException() {
        // Čas 07:00 ráno (před začátkem v 08:00)
        LocalDateTime brzyRano = pondeliRano.withHour(7).withMinute(0);
        RezervaceCreateDto dto = new RezervaceCreateDto(pacient.getId(), lekar.getId(), brzyRano, 15, "Akutní");

        when(pacientRepository.findById(pacient.getId())).thenReturn(Optional.of(pacient));
        when(lekarRepository.findById(lekar.getId())).thenReturn(Optional.of(lekar));
        when(ordinacniDobaRepository.findByLekarIdAndDenVTydnu(lekar.getId(), 1)).thenReturn(List.of(ordinacniDoba));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> rezervaceService.createRezervace(dto));
        assertTrue(ex.getMessage().contains("pouze v rámci platných ordinačních hodin"));
    }

    @Test
    @DisplayName("Odmítnutí rezervace mimo 15minutovou mřížku")
    void testCreateRezervace_SpatnaMrizka_ThrowsException() {
        // Čas s minutou 10 (neodpovídá 0, 15, 30, 45)
        LocalDateTime spatnyCas = pondeliRano.withMinute(10);
        RezervaceCreateDto dto = new RezervaceCreateDto(pacient.getId(), lekar.getId(), spatnyCas, 15, "Kontrola");

        when(pacientRepository.findById(pacient.getId())).thenReturn(Optional.of(pacient));
        when(lekarRepository.findById(lekar.getId())).thenReturn(Optional.of(lekar));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> rezervaceService.createRezervace(dto));
        assertTrue(ex.getMessage().contains("15minutové mřížce"));
    }

    @Test
    @DisplayName("Zrušení plánované rezervace uvolní termín")
    void testZrusitRezervaci_Success() {
        Rezervace r = new Rezervace(UUID.randomUUID(), pacient, lekar, pondeliRano, pondeliRano.plusMinutes(15), StavRezervace.PLANOVANA, "Kontrola");

        when(rezervaceRepository.findById(r.getId())).thenReturn(Optional.of(r));
        when(rezervaceRepository.save(any(Rezervace.class))).thenAnswer(inv -> inv.getArgument(0));

        RezervaceResponseDto resp = rezervaceService.zrusitRezervaci(r.getId(), new RezervaceZrusitDto(StavRezervace.ZRUSENA_PACIENTEM, "Omluva"));

        assertEquals(StavRezervace.ZRUSENA_PACIENTEM, resp.getStav());
        assertEquals("Omluva", resp.getPoznamkaZruseni());
    }

    @Test
    @DisplayName("Zákaz zrušení již proběhlé rezervace")
    void testZrusitRezervaci_Probehlou_ThrowsException() {
        Rezervace r = new Rezervace(UUID.randomUUID(), pacient, lekar, pondeliRano, pondeliRano.plusMinutes(15), StavRezervace.PROBEHLA, "Kontrola");

        when(rezervaceRepository.findById(r.getId())).thenReturn(Optional.of(r));

        assertThrows(BusinessRuleException.class, () ->
                rezervaceService.zrusitRezervaci(r.getId(), new RezervaceZrusitDto(StavRezervace.ZRUSENA_PACIENTEM, "Nelze"))
        );
    }

    @Test
    @DisplayName("Nahlášení nepřítomnosti lékaře převede kolidující rezervace do Call-listu")
    void testNahlasitNepritomnost_PrevedeDoCallListu() {
        Rezervace kolidujici = new Rezervace(UUID.randomUUID(), pacient, lekar, pondeliRano, pondeliRano.plusMinutes(30), StavRezervace.PLANOVANA, "Kontrola");

        when(rezervaceRepository.findPlatneRezervaceVIntervalu(lekar.getId(), pondeliRano.minusHours(1), pondeliRano.plusHours(2)))
                .thenReturn(List.of(kolidujici));
        when(rezervaceRepository.save(any(Rezervace.class))).thenAnswer(inv -> inv.getArgument(0));

        NahlasitNepritomnostDto dto = new NahlasitNepritomnostDto(lekar.getId(), pondeliRano.minusHours(1), pondeliRano.plusHours(2), "Nemoc lékaře");
        List<RezervaceResponseDto> dotcene = rezervaceService.nahlasitNepritomnost(dto);

        assertEquals(1, dotcene.size());
        assertEquals(StavRezervace.K_PREOBJEDNANI, dotcene.get(0).getStav());
    }
}

