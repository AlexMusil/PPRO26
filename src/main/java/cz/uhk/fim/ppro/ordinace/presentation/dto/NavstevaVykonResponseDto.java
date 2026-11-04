package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.NavstevaVykon;

import java.util.UUID;

public class NavstevaVykonResponseDto {

    private UUID id;
    private String vykonKod;
    private String vykonNazev;
    private Integer bodyZaKus;
    private Integer pocet;
    private Integer celkemBodu;
    private String poznamka;

    public NavstevaVykonResponseDto() {
    }

    public static NavstevaVykonResponseDto fromEntity(NavstevaVykon nv) {
        if (nv == null) return null;
        NavstevaVykonResponseDto dto = new NavstevaVykonResponseDto();
        dto.setId(nv.getId());
        if (nv.getVykon() != null) {
            dto.setVykonKod(nv.getVykon().getKod());
            dto.setVykonNazev(nv.getVykon().getNazev());
            dto.setBodyZaKus(nv.getVykon().getBody());
        }
        dto.setPocet(nv.getPocet());
        dto.setCelkemBodu(nv.getCelkemBodu());
        dto.setPoznamka(nv.getPoznamka());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVykonKod() {
        return vykonKod;
    }

    public void setVykonKod(String vykonKod) {
        this.vykonKod = vykonKod;
    }

    public String getVykonNazev() {
        return vykonNazev;
    }

    public void setVykonNazev(String vykonNazev) {
        this.vykonNazev = vykonNazev;
    }

    public Integer getBodyZaKus() {
        return bodyZaKus;
    }

    public void setBodyZaKus(Integer bodyZaKus) {
        this.bodyZaKus = bodyZaKus;
    }

    public Integer getPocet() {
        return pocet;
    }

    public void setPocet(Integer pocet) {
        this.pocet = pocet;
    }

    public Integer getCelkemBodu() {
        return celkemBodu;
    }

    public void setCelkemBodu(Integer celkemBodu) {
        this.celkemBodu = celkemBodu;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
