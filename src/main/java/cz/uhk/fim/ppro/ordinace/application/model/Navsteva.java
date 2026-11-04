package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "navsteva")
public class Navsteva {

    @Id
    private UUID id;

    @NotNull(message = "Návštěva musí být vázána na rezervaci")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rezervace_id", nullable = false, unique = true)
    private Rezervace rezervace;

    @NotNull(message = "Datum a čas vyšetření musí být zadány")
    @Column(name = "datum_cas", nullable = false)
    private LocalDateTime datumCas;

    @NotBlank(message = "Lékařská zpráva nesmí být prázdná")
    @Column(name = "lekarska_zprava", nullable = false, columnDefinition = "TEXT")
    private String lekarskaZprava;

    @Column(name = "doporuceni", columnDefinition = "TEXT")
    private String doporuceni;

    @OneToMany(mappedBy = "navsteva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NavstevaVykon> vykony = new ArrayList<>();

    public Navsteva() {
        this.id = UUID.randomUUID();
        this.datumCas = LocalDateTime.now();
    }

    public Navsteva(UUID id, Rezervace rezervace, LocalDateTime datumCas, String lekarskaZprava, String doporuceni) {
        this.id = id != null ? id : UUID.randomUUID();
        this.rezervace = rezervace;
        this.datumCas = datumCas != null ? datumCas : LocalDateTime.now();
        this.lekarskaZprava = lekarskaZprava;
        this.doporuceni = doporuceni;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Rezervace getRezervace() {
        return rezervace;
    }

    public void setRezervace(Rezervace rezervace) {
        this.rezervace = rezervace;
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

    public List<NavstevaVykon> getVykony() {
        return vykony;
    }

    public void setVykony(List<NavstevaVykon> vykony) {
        this.vykony = vykony;
    }

    public void addVykon(Vykon vykon, int pocet, String poznamka) {
        NavstevaVykon nv = new NavstevaVykon(UUID.randomUUID(), this, vykon, pocet, poznamka);
        this.vykony.add(nv);
    }

    public int getCelkemBodu() {
        if (vykony == null || vykony.isEmpty()) {
            return 0;
        }
        return vykony.stream().mapToInt(NavstevaVykon::getCelkemBodu).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Navsteva navsteva = (Navsteva) o;
        return Objects.equals(id, navsteva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
