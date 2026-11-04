package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.Navsteva;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NavstevaResponseDto {

    private UUID id;
    private UUID rezervaceId;
    private UUID pacientId;
    private String pacientJmeno;
    private String pacientCisloPojistence;
    private String pacientPojistovna;
    private UUID lekarId;
    private String lekarJmeno;
    private LocalDateTime datumCas;
    private String lekarskaZprava;
    private String doporuceni;
    private int celkemBodu;
    private List<NavstevaVykonResponseDto> vykony = new ArrayList<>();

    public NavstevaResponseDto() {
    }

    public static NavstevaResponseDto fromEntity(Navsteva n) {
        if (n == null) return null;
        NavstevaResponseDto dto = new NavstevaResponseDto();
        dto.setId(n.getId());
        if (n.getRezervace() != null) {
            dto.setRezervaceId(n.getRezervace().getId());
            if (n.getRezervace().getPacient() != null) {
                dto.setPacientId(n.getRezervace().getPacient().getId());
                dto.setPacientJmeno(n.getRezervace().getPacient().getCeleJmeno());
                dto.setPacientCisloPojistence(n.getRezervace().getPacient().getCisloPojistence());
                dto.setPacientPojistovna(n.getRezervace().getPacient().getPojistovna());
            }
            if (n.getRezervace().getLekar() != null) {
                dto.setLekarId(n.getRezervace().getLekar().getId());
                dto.setLekarJmeno(n.getRezervace().getLekar().getCeleJmeno());
            }
        }
        dto.setDatumCas(n.getDatumCas());
        dto.setLekarskaZprava(n.getLekarskaZprava());
        dto.setDoporuceni(n.getDoporuceni());
        dto.setCelkemBodu(n.getCelkemBodu());
        if (n.getVykony() != null) {
            dto.setVykony(n.getVykony().stream()
                    .map(NavstevaVykonResponseDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRezervaceId() {
        return rezervaceId;
    }

    public void setRezervaceId(UUID rezervaceId) {
        this.rezervaceId = rezervaceId;
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

    public String getPacientCisloPojistence() {
        return pacientCisloPojistence;
    }

    public void setPacientCisloPojistence(String pacientCisloPojistence) {
        this.pacientCisloPojistence = pacientCisloPojistence;
    }

    public String getPacientPojistovna() {
        return pacientPojistovna;
    }

    public void setPacientPojistovna(String pacientPojistovna) {
        this.pacientPojistovna = pacientPojistovna;
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

    public int getCelkemBodu() {
        return celkemBodu;
    }

    public void setCelkemBodu(int celkemBodu) {
        this.celkemBodu = celkemBodu;
    }

    public List<NavstevaVykonResponseDto> getVykony() {
        return vykony;
    }

    public void setVykony(List<NavstevaVykonResponseDto> vykony) {
        this.vykony = vykony;
    }
}
