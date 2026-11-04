package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "navsteva_vykon", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"navsteva_id", "vykon_kod"})
})
public class NavstevaVykon {

    @Id
    private UUID id;

    @NotNull(message = "Návštěva musí být přiřazena")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "navsteva_id", nullable = false)
    private Navsteva navsteva;

    @NotNull(message = "Výkon musí být vybrán")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vykon_kod", referencedColumnName = "kod", nullable = false)
    private Vykon vykon;

    @NotNull(message = "Počet provedení výkonu musí být zadán")
    @Min(value = 1, message = "Počet opakování výkonu musí být alespoň 1")
    @Column(name = "pocet", nullable = false)
    private Integer pocet;

    @Column(name = "poznamka", length = 255)
    private String poznamka;

    public NavstevaVykon() {
        this.id = UUID.randomUUID();
        this.pocet = 1;
    }

    public NavstevaVykon(UUID id, Navsteva navsteva, Vykon vykon, Integer pocet, String poznamka) {
        this.id = id != null ? id : UUID.randomUUID();
        this.navsteva = navsteva;
        this.vykon = vykon;
        this.pocet = pocet != null ? pocet : 1;
        this.poznamka = poznamka;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Navsteva getNavsteva() {
        return navsteva;
    }

    public void setNavsteva(Navsteva navsteva) {
        this.navsteva = navsteva;
    }

    public Vykon getVykon() {
        return vykon;
    }

    public void setVykon(Vykon vykon) {
        this.vykon = vykon;
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

    public int getCelkemBodu() {
        if (vykon != null && vykon.getBody() != null && pocet != null) {
            return vykon.getBody() * pocet;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavstevaVykon that = (NavstevaVykon) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

