package cz.uhk.fim.ppro.ordinace.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NavstevaCreateDto {

    @NotNull(message = "Rezervace musí být vybrána")
    private UUID rezervaceId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime datumCas = LocalDateTime.now();

    @NotBlank(message = "Lékařská zpráva nesmí být prázdná")
    private String lekarskaZprava;

    private String doporuceni;

    private List<NavstevaVykonPolozkaDto> vykony = new ArrayList<>();

    public NavstevaCreateDto() {
        this.datumCas = LocalDateTime.now();
    }

    public NavstevaCreateDto(UUID rezervaceId, LocalDateTime datumCas, String lekarskaZprava, String doporuceni) {
        this.rezervaceId = rezervaceId;
        this.datumCas = datumCas != null ? datumCas : LocalDateTime.now();
        this.lekarskaZprava = lekarskaZprava;
        this.doporuceni = doporuceni;
    }

    public UUID getRezervaceId() {
        return rezervaceId;
    }

    public void setRezervaceId(UUID rezervaceId) {
        this.rezervaceId = rezervaceId;
    }

    public LocalDateTime getDatumCas() {
        return datumCas;
    }

    public void setDatumCas(LocalDateTime datumCas) {
        this.datumCas = datumCas;
    }

    public String getLekarskaZprava() {
        return lekarskaZprava;
    }

    public void setLekarskaZprava(String lekarskaZprava) {
        this.lekarskaZprava = lekarskaZprava;
    }

    public String getDoporuceni() {
        return doporuceni;
    }

    public void setDoporuceni(String doporuceni) {
        this.doporuceni = doporuceni;
    }

    public List<NavstevaVykonPolozkaDto> getVykony() {
        return vykony;
    }

    public void setVykony(List<NavstevaVykonPolozkaDto> vykony) {
        this.vykony = vykony;
    }
}

