package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ordinacni_doba")
public class OrdinacniDoba {

    @Id
    private UUID id;

    @NotNull(message = "Lékař musí být přiřazen")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lekar_id", nullable = false)
    private Lekar lekar;

    @NotNull(message = "Den v týdnu musí být zadán")
    @Min(value = 1, message = "Den v týdnu musí být v rozsahu 1-7 (Pondělí-Neděle)")
    @Max(value = 7, message = "Den v týdnu musí být v rozsahu 1-7 (Pondělí-Neděle)")
    @Column(name = "den_v_tydnu", nullable = false)
    private Integer denVTydnu;

    @NotNull(message = "Čas začátku ordinační doby musí být zadán")
    @Column(name = "cas_od", nullable = false)
    private LocalTime casOd;

    @NotNull(message = "Čas konce ordinační doby musí být zadán")
    @Column(name = "cas_do", nullable = false)
    private LocalTime casDo;

    @Column(name = "pracoviste", nullable = false, length = 150)
    private String pracoviste = "Ordinace Na Vyhlídce";

    @Column(name = "lze_objednavat", nullable = false)
    private boolean lzeObjednavat = true;

    @Column(name = "poznamka", length = 255)
    private String poznamka;

    public OrdinacniDoba() {
        this.id = UUID.randomUUID();
        this.lzeObjednavat = true;
        this.pracoviste = "Ordinace Na Vyhlídce";
    }

    public OrdinacniDoba(UUID id, Lekar lekar, Integer denVTydnu, LocalTime casOd, LocalTime casDo,
                         String pracoviste, boolean lzeObjednavat, String poznamka) {
        this.id = id != null ? id : UUID.randomUUID();
        this.lekar = lekar;
        this.denVTydnu = denVTydnu;
        this.casOd = casOd;
        this.casDo = casDo;
        this.pracoviste = (pracoviste != null && !pracoviste.isBlank()) ? pracoviste : "Ordinace Na Vyhlídce";
        this.lzeObjednavat = lzeObjednavat;
        this.poznamka = poznamka;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Lekar getLekar() {
        return lekar;
    }

    public void setLekar(Lekar lekar) {
        this.lekar = lekar;
    }

    public Integer getDenVTydnu() {
        return denVTydnu;
    }

    public void setDenVTydnu(Integer denVTydnu) {
        this.denVTydnu = denVTydnu;
    }

    public LocalTime getCasOd() {
        return casOd;
    }

    public void setCasOd(LocalTime casOd) {
        this.casOd = casOd;
    }

    public LocalTime getCasDo() {
        return casDo;
    }

    public void setCasDo(LocalTime casDo) {
        this.casDo = casDo;
    }

    public String getPracoviste() {
        return pracoviste;
    }

    public void setPracoviste(String pracoviste) {
        this.pracoviste = pracoviste;
    }

    public boolean isLzeObjednavat() {
        return lzeObjednavat;
    }

    public void setLzeObjednavat(boolean lzeObjednavat) {
        this.lzeObjednavat = lzeObjednavat;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public String getDenNazev() {
        if (denVTydnu == null) return "";
        return switch (denVTydnu) {
            case 1 -> "Pondělí";
            case 2 -> "Úterý";
            case 3 -> "Středa";
            case 4 -> "Čtvrtek";
            case 5 -> "Pátek";
            case 6 -> "Sobota";
            case 7 -> "Neděle";
            default -> "Neznámý den";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdinacniDoba that = (OrdinacniDoba) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

