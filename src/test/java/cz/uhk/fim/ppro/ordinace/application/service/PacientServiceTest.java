package cz.uhk.fim.ppro.ordinace.application.service;

import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.exception.DuplicateEntityException;
import cz.uhk.fim.ppro.ordinace.application.model.Pacient;
import cz.uhk.fim.ppro.ordinace.infrastructure.repository.PacientRepository;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacientServiceTest {

    @Mock
    private PacientRepository pacientRepository;

    @InjectMocks
    private PacientService pacientService;

    private PacientCreateDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new PacientCreateDto(
                "Marie",
                "Nováková",
                1980,
                "+420 777 123 456",
                "805212/1234",
                "111",
                "Pravidelný pacient"
        );
    }

    @Test
    @DisplayName("Úspěšné vytvoření pacienta")
    void testCreatePacient_Success() {
        when(pacientRepository.existsByCisloPojistence("805212/1234")).thenReturn(false);
        when(pacientRepository.existsByJmenoIgnoreCaseAndPrijmeniIgnoreCaseAndTelefon("Marie", "Nováková", "+420 777 123 456")).thenReturn(false);
        when(pacientRepository.save(any(Pacient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PacientResponseDto result = pacientService.createPacient(validDto);

        assertNotNull(result);
        assertEquals("Marie", result.getJmeno());
        assertEquals("Nováková", result.getPrijmeni());
        assertEquals("805212/1234", result.getCisloPojistence());
        verify(pacientRepository, times(1)).save(any(Pacient.class));
    }

    @Test
    @DisplayName("Zabránění duplicitě čísla pojištěnce (Pravidlo #4)")
    void testCreatePacient_DuplicateCisloPojistence() {
        when(pacientRepository.existsByCisloPojistence("805212/1234")).thenReturn(true);

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class, () -> {
            pacientService.createPacient(validDto);
        });

        assertTrue(ex.getMessage().contains("již v evidenci existuje"));
        verify(pacientRepository, never()).save(any(Pacient.class));
    }

    @Test
    @DisplayName("Zabránění duplicitnímu zápisu téže osoby se shodným telefonem (Pravidlo #4)")
    void testCreatePacient_DuplicateNameAndPhone() {
        when(pacientRepository.existsByCisloPojistence("805212/1234")).thenReturn(false);
        when(pacientRepository.existsByJmenoIgnoreCaseAndPrijmeniIgnoreCaseAndTelefon("Marie", "Nováková", "+420 777 123 456")).thenReturn(true);

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class, () -> {
            pacientService.createPacient(validDto);
        });

        assertTrue(ex.getMessage().contains("se shodným jménem"));
        verify(pacientRepository, never()).save(any(Pacient.class));
    }

    @Test
    @DisplayName("Pravidlo klienta: Nemazatelnost historie pacienta")
    void testDeletePacient_ThrowsBusinessRuleException() {
        UUID id = UUID.randomUUID();

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> {
            pacientService.deletePacient(id);
        });

        assertTrue(ex.getMessage().contains("Smazání pacienta je zakázáno"));
        verify(pacientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Vyhledávání pacientů podle dotazu")
    void testSearchPacienti() {
        Pacient pacient = new Pacient(UUID.randomUUID(), "Marie", "Nováková", 1980, "+420 777 123 456", "805212/1234", "111", null);
        when(pacientRepository.searchPacienti("Nováková")).thenReturn(List.of(pacient));

        List<PacientResponseDto> results = pacientService.searchPacienti("Nováková");

        assertEquals(1, results.size());
        assertEquals("Nováková", results.get(0).getPrijmeni());
    }
}
