package cz.uhk.fim.ppro.ordinace.presentation.controller;

import cz.uhk.fim.ppro.ordinace.application.service.VykonService;
import cz.uhk.fim.ppro.ordinace.presentation.dto.VykonDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VykonRestController.class)
class VykonRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VykonService vykonService;

    @Test
    @DisplayName("GET /api/vykony vrací číselník výkonů")
    void testGetVykony() throws Exception {
        VykonDto dto = new VykonDto("09513", "Cílené vyšetření", 280, null, true);
        when(vykonService.getAllAktivniVykony()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vykony"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kod").value("09513"))
                .andExpect(jsonPath("$[0].body").value(280));
    }
}
