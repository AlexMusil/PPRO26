package cz.uhk.fim.ppro.ordinace.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PacientCreateDto {

    @NotBlank(message = "Křestní jméno je povinné")
    private String jmeno;

    @NotBlank(message = "Příjmení je povinné")
    private String prijmeni;

    @NotNull(message = "Rok narození je povinný")
    @Min(value = 1900, message = "Rok narození musí být od roku 1900")
    @Max(value = 2026, message = "Rok narození nemůže být v budoucnosti")
    private Integer rokNarozeni;

    @NotBlank(message = "Telefonní číslo je povinné")
    @Pattern(regexp = "^(\\+420)?\\s*[0-9]{3}\\s*[0-9]{3}\\s*[0-9]{3}$", message = "Telefon musí mít platný formát (např. +420 777 123 456 nebo 777123456)")
    private String telefon;

    @NotBlank(message = "Číslo pojištěnce (rodné číslo) je povinné")
    private String cisloPojistence;

    @NotBlank(message = "Zdravotní pojišťovna je povinná")
    private String pojistovna;

    private String poznamka;

    public PacientCreateDto() {
    }

    public PacientCreateDto(String jmeno, String prijmeni, Integer rokNarozeni, String telefon,
                            String cisloPojistence, String pojistovna, String poznamka) {
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.telefon = telefon;
        this.cisloPojistence = cisloPojistence;
        this.pojistovna = pojistovna;
        this.poznamka = poznamka;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public Integer getRokNarozeni() {
        return rokNarozeni;
    }

    public void setRokNarozeni(Integer rokNarozeni) {
        this.rokNarozeni = rokNarozeni;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getCisloPojistence() {
        return cisloPojistence;
    }

    public void setCisloPojistence(String cisloPojistence) {
        this.cisloPojistence = cisloPojistence;
    }

    public String getPojistovna() {
        return pojistovna;
    }

    public void setPojistovna(String pojistovna) {
        this.pojistovna = pojistovna;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
