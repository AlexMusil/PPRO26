package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "rezervace")
public class Rezervace {

    @Id
    private UUID id;

    @NotNull(message = "Pacient musí být vybrán")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pacient_id", nullable = false)
    private Pacient pacient;

    @NotNull(message = "Lékař musí být vybrán")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lekar_id", nullable = false)
    private Lekar lekar;

    @NotNull(message = "Čas začátku rezervace musí být zadán")
    @Column(name = "cas_od", nullable = false)
    private LocalDateTime casOd;

    @NotNull(message = "Čas konce rezervace musí být zadán")
    @Column(name = "cas_do", nullable = false)
    private LocalDateTime casDo;

    @NotNull(message = "Stav rezervace musí být nastaven")
    @Enumerated(EnumType.STRING)
    @Column(name = "stav", nullable = false, length = 30)
    private StavRezervace stav = StavRezervace.PLANOVANA;

    @NotBlank(message = "Důvod návštěvy musí být uveden")
    @Column(name = "duvod_navstevy", nullable = false, length = 255)
    private String duvodNavstevy;

    @Column(name = "poznamka_zruseni", length = 255)
    private String poznamkaZruseni;

    @Column(name = "vytvoreno_dne", nullable = false, updatable = false)
    private LocalDateTime vytvorenoDne;

    @OneToOne(mappedBy = "rezervace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Navsteva navsteva;

    public Rezervace() {
        this.id = UUID.randomUUID();
        this.vytvorenoDne = LocalDateTime.now();
        this.stav = StavRezervace.PLANOVANA;
    }

    public Rezervace(UUID id, Pacient pacient, Lekar lekar, LocalDateTime casOd, LocalDateTime casDo,
                     StavRezervace stav, String duvodNavstevy) {
        this.id = id != null ? id : UUID.randomUUID();
        this.pacient = pacient;
        this.lekar = lekar;
        this.casOd = casOd;
        this.casDo = casDo;
        this.stav = stav != null ? stav : StavRezervace.PLANOVANA;
        this.duvodNavstevy = duvodNavstevy;
        this.vytvorenoDne = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pacient getPacient() {
        return pacient;
    }

    public void setPacient(Pacient pacient) {
        this.pacient = pacient;
    }

    public Lekar getLekar() {
        return lekar;
    }

    public void setLekar(Lekar lekar) {
        this.lekar = lekar;
    }

    public LocalDateTime getCasOd() {
        return casOd;
    }

    public void setCasOd(LocalDateTime casOd) {
        this.casOd = casOd;
    }

    public LocalDateTime getCasDo() {
        return casDo;
    }

    public void setCasDo(LocalDateTime casDo) {
        this.casDo = casDo;
    }

    public StavRezervace getStav() {
        return stav;
    }

    public void setStav(StavRezervace stav) {
        this.stav = stav;
    }

    public String getDuvodNavstevy() {
        return duvodNavstevy;
    }

    public void setDuvodNavstevy(String duvodNavstevy) {
        this.duvodNavstevy = duvodNavstevy;
    }

    public String getPoznamkaZruseni() {
        return poznamkaZruseni;
    }

    public void setPoznamkaZruseni(String poznamkaZruseni) {
        this.poznamkaZruseni = poznamkaZruseni;
    }

    public LocalDateTime getVytvorenoDne() {
        return vytvorenoDne;
    }

    public void setVytvorenoDne(LocalDateTime vytvorenoDne) {
        this.vytvorenoDne = vytvorenoDne;
    }

    public Navsteva getNavsteva() {
        return navsteva;
    }

    public void setNavsteva(Navsteva navsteva) {
        this.navsteva = navsteva;
    }

    public long getDelkaMinut() {
        if (casOd != null && casDo != null) {
            return Duration.between(casOd, casDo).toMinutes();
        }
        return 0;
    }

    public boolean isKolidujici(LocalDateTime jinyCasOd, LocalDateTime jinyCasDo) {
        if (this.stav != StavRezervace.PLANOVANA) {
            return false;
        }
        return this.casOd.isBefore(jinyCasDo) && this.casDo.isAfter(jinyCasOd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rezervace rezervace = (Rezervace) o;
        return Objects.equals(id, rezervace.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

