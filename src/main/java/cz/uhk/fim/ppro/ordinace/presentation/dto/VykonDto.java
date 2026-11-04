package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.Vykon;

public class VykonDto {

    private String kod;
    private String nazev;
    private Integer body;
    private String popis;
    private boolean aktivni;

    public VykonDto() {
    }

    public VykonDto(String kod, String nazev, Integer body, String popis, boolean aktivni) {
        this.kod = kod;
        this.nazev = nazev;
        this.body = body;
        this.popis = popis;
        this.aktivni = aktivni;
    }

    public static VykonDto fromEntity(Vykon v) {
        if (v == null) return null;
        return new VykonDto(
                v.getKod(),
                v.getNazev(),
                v.getBody(),
                v.getPopis(),
                v.isAktivni()
        );
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
}

