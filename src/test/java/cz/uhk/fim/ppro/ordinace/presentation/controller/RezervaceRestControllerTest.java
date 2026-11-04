package cz.uhk.fim.ppro.ordinace.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.fim.ppro.ordinace.application.exception.BusinessRuleException;
import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;
import cz.uhk.fim.ppro.ordinace.application.service.RezervaceService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceCreateDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceResponseDto;
import cz.uhk.fim.ppro.ordinace.presentation.dto.RezervaceZrusitDto;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RezervaceRestController.class)
class RezervaceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RezervaceService rezervaceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/rezervace vytvoří novou rezervaci a vrátí 201 Created")
    void testCreateRezervace_Success() throws Exception {
        UUID pacientId = UUID.randomUUID();
        UUID lekarId = UUID.randomUUID();
        LocalDateTime casOd = LocalDateTime.of(2026, 11, 16, 9, 0);

        RezervaceCreateDto createDto = new RezervaceCreateDto(pacientId, lekarId, casOd, 30, "Kontrola tlaku");

        RezervaceResponseDto responseDto = new RezervaceResponseDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setPacientId(pacientId);
        responseDto.setPacientJmeno("Marie Nováková");
        responseDto.setLekarId(lekarId);
        responseDto.setLekarJmeno("MUDr. Jana Hrubá");
        responseDto.setCasOd(casOd);
        responseDto.setCasDo(casOd.plusMinutes(30));
        responseDto.setDelkaMinut(30);
        responseDto.setStav(StavRezervace.PLANOVANA);
        responseDto.setDuvodNavstevy("Kontrola tlaku");

        when(rezervaceService.createRezervace(any(RezervaceCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/rezervace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacientJmeno").value("Marie Nováková"))
                .andExpect(jsonPath("$.delkaMinut").value(30))
                .andExpect(jsonPath("$.stav").value("PLANOVANA"));
    }

    @Test
    @DisplayName("POST /api/rezervace vrátí 400 Bad Request při kolizi")
    void testCreateRezervace_Kolize_Returns400() throws Exception {
        RezervaceCreateDto createDto = new RezervaceCreateDto(
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.of(2026, 11, 16, 9, 0), 15, "Kontrola"
        );

        when(rezervaceService.createRezervace(any(RezervaceCreateDto.class)))
                .thenThrow(new BusinessRuleException("Zvolený čas koliduje s jinou aktivní rezervací lékaře."));

        mockMvc.perform(post("/api/rezervace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Zvolený čas koliduje s jinou aktivní rezervací lékaře."));
    }

    @Test
    @DisplayName("POST /api/rezervace/{id}/zrusit zruší rezervaci")
    void testZrusitRezervaci() throws Exception {
        UUID id = UUID.randomUUID();
        RezervaceResponseDto responseDto = new RezervaceResponseDto();
        responseDto.setId(id);
        responseDto.setStav(StavRezervace.ZRUSENA_PACIENTEM);
        responseDto.setPoznamkaZruseni("Nemoc pacienta");

        when(rezervaceService.zrusitRezervaci(eq(id), any(RezervaceZrusitDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/rezervace/" + id + "/zrusit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typZruseni\":\"ZRUSENA_PACIENTEM\",\"poznamka\":\"Nemoc pacienta\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stav").value("ZRUSENA_PACIENTEM"));
    }

    @Test
    @DisplayName("GET /api/rezervace/call-list vrací seznam pacientů k přeobjednání")
    void testGetCallList() throws Exception {
        RezervaceResponseDto dto = new RezervaceResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setPacientJmeno("Jana Krátká");
        dto.setStav(StavRezervace.K_PREOBJEDNANI);

        when(rezervaceService.getCallList()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rezervace/call-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacientJmeno").value("Jana Krátká"))
                .andExpect(jsonPath("$[0].stav").value("K_PREOBJEDNANI"));
    }
}
