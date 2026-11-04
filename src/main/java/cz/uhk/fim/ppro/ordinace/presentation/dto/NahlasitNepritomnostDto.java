package cz.uhk.fim.ppro.ordinace.presentation.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class NahlasitNepritomnostDto {

    @NotNull(message = "Lékař musí být vybrán")
    private UUID lekarId;

    @NotNull(message = "Začátek nepřítomnosti musí být zadán")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime odCas;

    @NotNull(message = "Konec nepřítomnosti musí být zadán")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime doCas;

    private String duvod = "Nemoc / indispozice lékaře";

    public NahlasitNepritomnostDto() {
    }

    public NahlasitNepritomnostDto(UUID lekarId, LocalDateTime odCas, LocalDateTime doCas, String duvod) {
        this.lekarId = lekarId;
        this.odCas = odCas;
        this.doCas = doCas;
        this.duvod = duvod;
    }

    public UUID getLekarId() {
        return lekarId;
    }

    public void setLekarId(UUID lekarId) {
        this.lekarId = lekarId;
    }

    public LocalDateTime getOdCas() {
        return odCas;
    }

    public void setOdCas(LocalDateTime odCas) {
        this.odCas = odCas;
    }

    public LocalDateTime getDoCas() {
        return doCas;
    }

    public void setDoCas(LocalDateTime doCas) {
        this.doCas = doCas;
    }

    public String getDuvod() {
        return duvod;
    }

    public void setDuvod(String duvod) {
        this.duvod = duvod;
    }
}
