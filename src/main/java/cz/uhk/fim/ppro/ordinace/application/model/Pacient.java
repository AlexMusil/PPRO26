package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Doménová entita reprezentující pacienta v evidenci Ordinace Na Vyhlídce.
 * Zodpovídá požadavku klienta #1 a invariantu nemazatelnosti historie.
 */
@Entity
@Table(name = "pacient")
public class Pacient {

    @Id
    private UUID id;

    @NotBlank(message = "Jméno pacienta nesmí být prázdné")
    @Column(name = "jmeno", nullable = false, length = 50)
    private String jmeno;

    @NotBlank(message = "Příjmení pacienta nesmí být prázdné")
    @Column(name = "prijmeni", nullable = false, length = 50)
    private String prijmeni;

    @NotNull(message = "Rok narození musí být zadán")
    @Column(name = "rok_narozeni", nullable = false)
    private Integer rokNarozeni;

    @NotBlank(message = "Telefonní číslo nesmí být prázdné")
    @Column(name = "telefon", nullable = false, length = 20)
    private String telefon;

    @NotBlank(message = "Číslo pojištěnce (rodné číslo) nesmí být prázdné")
    @Column(name = "cislo_pojistence", nullable = false, unique = true, length = 20)
    private String cisloPojistence;

    @NotBlank(message = "Kód zdravotní pojišťovny nesmí být prázdný")
    @Column(name = "pojistovna", nullable = false, length = 10)
    private String pojistovna;

    @Column(name = "poznamka", columnDefinition = "TEXT")
    private String poznamka;

    @Column(name = "vytvoreno_dne", nullable = false, updatable = false)
    private LocalDateTime vytvorenoDne;

    public Pacient() {
        this.id = UUID.randomUUID();
        this.vytvorenoDne = LocalDateTime.now();
    }

    public Pacient(UUID id, String jmeno, String prijmeni, Integer rokNarozeni,
                   String telefon, String cisloPojistence, String pojistovna, String poznamka) {
        this.id = id != null ? id : UUID.randomUUID();
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.telefon = telefon;
        this.cisloPojistence = cisloPojistence;
        this.pojistovna = pojistovna;
        this.poznamka = poznamka;
        this.vytvorenoDne = LocalDateTime.now();
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

    public String getCeleJmeno() {
        return prijmeni + " " + jmeno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pacient pacient = (Pacient) o;
        return Objects.equals(id, pacient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
