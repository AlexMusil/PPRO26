package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;

import java.time.LocalDateTime;
import java.util.UUID;

public class SlotDto {

    private LocalDateTime casOd;
    private LocalDateTime casDo;
    private boolean volno;
    private boolean vOrdinaDobe;
    private UUID rezervaceId;
    private String pacientJmeno;
    private String pacientTelefon;
    private String duvod;
    private StavRezervace stav;

    public SlotDto() {
    }

    public SlotDto(LocalDateTime casOd, LocalDateTime casDo, boolean volno, boolean vOrdinaDobe) {
        this.casOd = casOd;
        this.casDo = casDo;
        this.volno = volno;
        this.vOrdinaDobe = vOrdinaDobe;
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

    public boolean isVolno() {
        return volno;
    }

    public void setVolno(boolean volno) {
        this.volno = volno;
    }

    public boolean isvOrdinaDobe() {
        return vOrdinaDobe;
    }

    public void setvOrdinaDobe(boolean vOrdinaDobe) {
        this.vOrdinaDobe = vOrdinaDobe;
    }

    public UUID getRezervaceId() {
        return rezervaceId;
    }

    public void setRezervaceId(UUID rezervaceId) {
        this.rezervaceId = rezervaceId;
    }

    public String getPacientJmeno() {
        return pacientJmeno;
    }

    public void setPacientJmeno(String pacientJmeno) {
        this.pacientJmeno = pacientJmeno;
    }

    public String getPacientTelefon() {
        return pacientTelefon;
    }

    public void setPacientTelefon(String pacientTelefon) {
        this.pacientTelefon = pacientTelefon;
    }

    public String getDuvod() {
        return duvod;
    }

    public void setDuvod(String duvod) {
        this.duvod = duvod;
    }

    public StavRezervace getStav() {
        return stav;
    }

    public void setStav(StavRezervace stav) {
        this.stav = stav;
    }
}
