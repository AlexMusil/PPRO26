package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.Rezervace;
import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;

import java.time.LocalDateTime;
import java.util.UUID;

public class RezervaceResponseDto {

    private UUID id;
    private UUID pacientId;
    private String pacientJmeno;
    private String pacientTelefon;
    private String pacientCisloPojistence;
    private UUID lekarId;
    private String lekarJmeno;
    private LocalDateTime casOd;
    private LocalDateTime casDo;
    private long delkaMinut;
    private StavRezervace stav;
    private String stavPopis;
    private String duvodNavstevy;
    private String poznamkaZruseni;
    private boolean realizovana;
    private UUID navstevaId;

    public RezervaceResponseDto() {
    }

    public static RezervaceResponseDto fromEntity(Rezervace r) {
        if (r == null) return null;
        RezervaceResponseDto dto = new RezervaceResponseDto();
        dto.setId(r.getId());
        if (r.getPacient() != null) {
            dto.setPacientId(r.getPacient().getId());
            dto.setPacientJmeno(r.getPacient().getCeleJmeno());
            dto.setPacientTelefon(r.getPacient().getTelefon());
            dto.setPacientCisloPojistence(r.getPacient().getCisloPojistence());
        }
        if (r.getLekar() != null) {
            dto.setLekarId(r.getLekar().getId());
            dto.setLekarJmeno(r.getLekar().getCeleJmeno());
        }
        dto.setCasOd(r.getCasOd());
        dto.setCasDo(r.getCasDo());
        dto.setDelkaMinut(r.getDelkaMinut());
        dto.setStav(r.getStav());
        dto.setStavPopis(r.getStav() != null ? r.getStav().getPopis() : "");
        dto.setDuvodNavstevy(r.getDuvodNavstevy());
        dto.setPoznamkaZruseni(r.getPoznamkaZruseni());
        dto.setRealizovana(r.getNavsteva() != null || r.getStav() == StavRezervace.PROBEHLA);
        dto.setNavstevaId(r.getNavsteva() != null ? r.getNavsteva().getId() : null);
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPacientId() {
        return pacientId;
    }

    public void setPacientId(UUID pacientId) {
        this.pacientId = pacientId;
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

    public String getPacientCisloPojistence() {
        return pacientCisloPojistence;
    }

    public void setPacientCisloPojistence(String pacientCisloPojistence) {
        this.pacientCisloPojistence = pacientCisloPojistence;
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

    public long getDelkaMinut() {
        return delkaMinut;
    }

    public void setDelkaMinut(long delkaMinut) {
        this.delkaMinut = delkaMinut;
    }

    public StavRezervace getStav() {
        return stav;
    }

    public void setStav(StavRezervace stav) {
        this.stav = stav;
    }

    public String getStavPopis() {
        return stavPopis;
    }

    public void setStavPopis(String stavPopis) {
        this.stavPopis = stavPopis;
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

    public boolean isRealizovana() {
        return realizovana;
    }

    public void setRealizovana(boolean realizovana) {
        this.realizovana = realizovana;
    }

    public UUID getNavstevaId() {
        return navstevaId;
    }

    public void setNavstevaId(UUID navstevaId) {
        this.navstevaId = navstevaId;
    }
}
