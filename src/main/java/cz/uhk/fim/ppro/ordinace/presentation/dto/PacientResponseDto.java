package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.Pacient;
import java.time.LocalDateTime;
import java.util.UUID;

public class PacientResponseDto {

    private UUID id;
    private String jmeno;
    private String prijmeni;
    private Integer rokNarozeni;
    private String telefon;
    private String cisloPojistence;
    private String pojistovna;
    private String poznamka;
    private LocalDateTime vytvorenoDne;

    public PacientResponseDto() {
    }

    public PacientResponseDto(UUID id, String jmeno, String prijmeni, Integer rokNarozeni,
                              String telefon, String cisloPojistence, String pojistovna,
                              String poznamka, LocalDateTime vytvorenoDne) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.telefon = telefon;
        this.cisloPojistence = cisloPojistence;
        this.pojistovna = pojistovna;
        this.poznamka = poznamka;
        this.vytvorenoDne = vytvorenoDne;
    }

    public static PacientResponseDto fromEntity(Pacient p) {
        return new PacientResponseDto(
                p.getId(),
                p.getJmeno(),
                p.getPrijmeni(),
                p.getRokNarozeni(),
                p.getTelefon(),
                p.getCisloPojistence(),
                p.getPojistovna(),
                p.getPoznamka(),
                p.getVytvorenoDne()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getVytvorenoDne() {
        return vytvorenoDne;
    }

    public void setVytvorenoDne(LocalDateTime vytvorenoDne) {
        this.vytvorenoDne = vytvorenoDne;
    }
}
