package cz.uhk.fim.ppro.ordinace.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.fim.ppro.ordinace.application.exception.DuplicateEntityException;
import cz.uhk.fim.ppro.ordinace.application.service.PacientService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacientRestController.class)
class PacientRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacientService pacientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/pacienti vrací seznam pacientů")
    void testGetPacienti() throws Exception {
        PacientResponseDto dto = new PacientResponseDto(
                UUID.randomUUID(), "Marie", "Nováková", 1980,
                "+420 777 123 456", "805212/1234", "111", null, LocalDateTime.now()
        );
        when(pacientService.searchPacienti(null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pacienti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jmeno").value("Marie"))
                .andExpect(jsonPath("$[0].prijmeni").value("Nováková"))
                .andExpect(jsonPath("$[0].cisloPojistence").value("805212/1234"));
    }

    @Test
    @DisplayName("POST /api/pacienti úspěšně vytvoří pacienta (HTTP 201)")
    void testCreatePacient_Success() throws Exception {
        PacientCreateDto createDto = new PacientCreateDto(
                "Jan", "Novák", 1990, "+420 777 000 111", "900101/1234", "111", null
        );
        PacientResponseDto responseDto = new PacientResponseDto(
                UUID.randomUUID(), "Jan", "Novák", 1990,
                "+420 777 000 111", "900101/1234", "111", null, LocalDateTime.now()
        );
        when(pacientService.createPacient(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/pacienti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jmeno").value("Jan"))
                .andExpect(jsonPath("$.prijmeni").value("Novák"));
    }

    @Test
    @DisplayName("POST /api/pacienti při duplicitě vrací HTTP 409 Conflict")
    void testCreatePacient_Duplicate() throws Exception {
        PacientCreateDto createDto = new PacientCreateDto(
                "Jan", "Novák", 1990, "+420 777 000 111", "900101/1234", "111", null
        );
        when(pacientService.createPacient(any()))
                .thenThrow(new DuplicateEntityException("Pacient s číslem pojištěnce již existuje."));

        mockMvc.perform(post("/api/pacienti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Pacient s číslem pojištěnce již existuje."));
    }
}
