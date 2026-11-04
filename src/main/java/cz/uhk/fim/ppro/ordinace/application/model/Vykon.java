package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "vykon")
public class Vykon {

    @Id
    @NotBlank(message = "Kód výkonu nesmí být prázdný")
    @Column(name = "kod", nullable = false, length = 10)
    private String kod;

    @NotBlank(message = "Název výkonu nesmí být prázdný")
    @Column(name = "nazev", nullable = false, length = 150)
    private String nazev;

    @NotNull(message = "Počet bodů nesmí být prázdný")
    @Min(value = 0, message = "Počet bodů musí být nezáporný")
    @Column(name = "body", nullable = false)
    private Integer body;

    @Column(name = "popis", columnDefinition = "TEXT")
    private String popis;

    @Column(name = "aktivni", nullable = false)
    private boolean aktivni = true;

    public Vykon() {
        this.aktivni = true;
    }

    public Vykon(String kod, String nazev, Integer body, String popis, boolean aktivni) {
        this.kod = kod;
        this.nazev = nazev;
        this.body = body;
        this.popis = popis;
        this.aktivni = aktivni;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public Integer getBody() {
        return body;
    }

    public void setBody(Integer body) {
        this.body = body;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public boolean isAktivni() {
        return aktivni;
    }

    public void setAktivni(boolean aktivni) {
        this.aktivni = aktivni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vykon vykon = (Vykon) o;
        return Objects.equals(kod, vykon.kod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kod);
    }

    @Override
    public String toString() {
        return kod + " - " + nazev + " (" + body + " b.)";
    }
}

