package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.OrdinacniDoba;

import java.time.LocalTime;
import java.util.UUID;

public class OrdinacniDobaDto {

    private UUID id;
    private UUID lekarId;
    private String lekarJmeno;
    private Integer denVTydnu;
    private String denNazev;
    private LocalTime casOd;
    private LocalTime casDo;
    private String pracoviste;
    private boolean lzeObjednavat;
    private String poznamka;

    public OrdinacniDobaDto() {
    }

    public static OrdinacniDobaDto fromEntity(OrdinacniDoba od) {
        if (od == null) return null;
        OrdinacniDobaDto dto = new OrdinacniDobaDto();
        dto.setId(od.getId());
        dto.setLekarId(od.getLekar() != null ? od.getLekar().getId() : null);
        dto.setLekarJmeno(od.getLekar() != null ? od.getLekar().getCeleJmeno() : "");
        dto.setDenVTydnu(od.getDenVTydnu());
        dto.setDenNazev(od.getDenNazev());
        dto.setCasOd(od.getCasOd());
        dto.setCasDo(od.getCasDo());
        dto.setPracoviste(od.getPracoviste());
        dto.setLzeObjednavat(od.isLzeObjednavat());
        dto.setPoznamka(od.getPoznamka());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLekarId() {
        return lekarId;
    }

    public void setLekarId(UUID lekarId) {
        this.lekarId = lekarId;
    }

    public String getLekarJmeno() {
        return lekarJmeno;
    }

    public void setLekarJmeno(String lekarJmeno) {
        this.lekarJmeno = lekarJmeno;
    }

    public Integer getDenVTydnu() {
        return denVTydnu;
    }

    public void setDenVTydnu(Integer denVTydnu) {
        this.denVTydnu = denVTydnu;
    }

    public String getDenNazev() {
        return denNazev;
    }

    public void setDenNazev(String denNazev) {
        this.denNazev = denNazev;
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
}

