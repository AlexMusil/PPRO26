package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;
import jakarta.validation.constraints.NotNull;

public class RezervaceZrusitDto {

    @NotNull(message = "Musíte zvolit, kdo termín ruší")
    private StavRezervace typZruseni = StavRezervace.ZRUSENA_PACIENTEM;

    private String poznamka;

    public RezervaceZrusitDto() {
    }

    public RezervaceZrusitDto(StavRezervace typZruseni, String poznamka) {
        this.typZruseni = typZruseni;
        this.poznamka = poznamka;
    }

    public StavRezervace getTypZruseni() {
        return typZruseni;
    }

    public void setTypZruseni(StavRezervace typZruseni) {
        this.typZruseni = typZruseni;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}

