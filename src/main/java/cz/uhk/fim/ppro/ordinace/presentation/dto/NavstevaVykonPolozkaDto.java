package cz.uhk.fim.ppro.ordinace.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NavstevaVykonPolozkaDto {

    @NotBlank(message = "Kód výkonu nesmí být prázdný")
    private String vykonKod;

    @NotNull(message = "Počet provedení musí být zadán")
    @Min(value = 1, message = "Počet musí být minimálně 1")
    private Integer pocet = 1;

    private String poznamka;

    public NavstevaVykonPolozkaDto() {
    }

    public NavstevaVykonPolozkaDto(String vykonKod, Integer pocet, String poznamka) {
        this.vykonKod = vykonKod;
        this.pocet = pocet;
        this.poznamka = poznamka;
    }

    public String getVykonKod() {
        return vykonKod;
    }

    public void setVykonKod(String vykonKod) {
        this.vykonKod = vykonKod;
    }

    public Integer getPocet() {
        return pocet;
    }

    public void setPocet(Integer pocet) {
        this.pocet = pocet;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
