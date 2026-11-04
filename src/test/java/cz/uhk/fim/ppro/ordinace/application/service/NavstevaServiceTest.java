package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.model.*;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.NavstevaRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.RezervaceRepository;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.VykonRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.NavstevaVykonPolozkaDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NavstevaServiceTest {

    @Mock
    private NavstevaRepository navstevaRepository;
    @Mock
    private RezervaceRepository rezervaceRepository;
    @Mock
    private VykonRepository vykonRepository;

    @InjectMocks
    private NavstevaService navstevaService;

    private Rezervace minulaRezervace;
    private Rezervace budouciRezervace;
    private Vykon vysetreni;
    private Vykon injekce;

    @BeforeEach
    void setUp() {
        Pacient pacient = new Pacient(UUID.randomUUID(), "Marie", "Nováková", 1980, "+420 777 123 456", "805212/1231", "111", null);
        Lekar lekar = new Lekar(UUID.randomUUID(), "MUDr.", "Jana", "Hrubá", null, "Všeobecné lékařství", "+420 495 123 451", "hruba@ordinace.cz");

        minulaRezervace = new Rezervace(UUID.randomUUID(), pacient, lekar,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusMinutes(30),
                StavRezervace.PLANOVANA, "Kontrola tlaku");

        budouciRezervace = new Rezervace(UUID.randomUUID(), pacient, lekar,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(30),
                StavRezervace.PLANOVANA, "Budoucí termín");

        vysetreni = new Vykon("09513", "Cílené vyšetření", 280, null, true);
        injekce = new Vykon("09115", "Injekce i.m.", 65, null, true);
    }

    @Test
    @DisplayName("Úspěšné zaevidování návštěvy a vykázání výkonů (vazba M:N s atributem počet)")
    void testCreateNavsteva_Success_WithVykonyMN() {
        NavstevaCreateDto dto = new NavstevaCreateDto(minulaRezervace.getId(), LocalDateTime.now().minusDays(1).plusMinutes(5), "Vše v normě", "Kontrola za rok");
        dto.setVykony(List.of(
                new NavstevaVykonPolozkaDto("09513", 1, "Cílené vyšetření"),
                new NavstevaVykonPolozkaDto("09115", 2, "Aplikace 2 injekcí")
        ));

        when(rezervaceRepository.findById(minulaRezervace.getId())).thenReturn(Optional.of(minulaRezervace));
        when(navstevaRepository.findByRezervaceId(minulaRezervace.getId())).thenReturn(Optional.empty());
        when(vykonRepository.findByKod("09513")).thenReturn(Optional.of(vysetreni));
        when(vykonRepository.findByKod("09115")).thenReturn(Optional.of(injekce));
        when(navstevaRepository.save(any(Navsteva.class))).thenAnswer(inv -> inv.getArgument(0));

        NavstevaResponseDto resp = navstevaService.createNavsteva(dto);

        assertNotNull(resp);
        assertEquals(2, resp.getVykony().size());
        // 280 * 1 + 65 * 2 = 280 + 130 = 410 bodů
        assertEquals(410, resp.getCelkemBodu());
        assertEquals(StavRezervace.PROBEHLA, minulaRezervace.getStav());
        verify(navstevaRepository, times(1)).save(any(Navsteva.class));
        verify(rezervaceRepository, times(1)).save(minulaRezervace);
    }

    @Test
    @DisplayName("Odmítnutí zaevidování výkonu k budoucí rezervaci")
    void testCreateNavsteva_DoBudoucna_ThrowsException() {
        NavstevaCreateDto dto = new NavstevaCreateDto(budouciRezervace.getId(), LocalDateTime.now(), "Zpráva", null);

        when(rezervaceRepository.findById(budouciRezervace.getId())).thenReturn(Optional.of(budouciRezervace));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> navstevaService.createNavsteva(dto));
        assertTrue(ex.getMessage().contains("budoucnosti"));
        verify(navstevaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Odmítnutí opakovaného zaevidování návštěvy k téže rezervaci")
    void testCreateNavsteva_KOpakovaneRealizaci_ThrowsException() {
        minulaRezervace.setStav(StavRezervace.PROBEHLA);
        NavstevaCreateDto dto = new NavstevaCreateDto(minulaRezervace.getId(), LocalDateTime.now().minusDays(1), "Zpráva", null);

        when(rezervaceRepository.findById(minulaRezervace.getId())).thenReturn(Optional.of(minulaRezervace));

        assertThrows(BusinessRuleException.class, () -> navstevaService.createNavsteva(dto));
        verify(navstevaRepository, never()).save(any());
    }
}
