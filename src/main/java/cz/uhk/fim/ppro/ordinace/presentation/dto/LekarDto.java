package cz.uhk.fim.ppro.ordinace.presentation.dto;

import cz.uhk.fim.ppro.ordinace.application.model.Lekar;

import java.util.UUID;

public class LekarDto {

    private UUID id;
    private String celeJmeno;
    private String specializace;
    private String telefon;
    private String email;
    private boolean aktivni;

    public LekarDto() {
    }

    public LekarDto(UUID id, String celeJmeno, String specializace, String telefon, String email, boolean aktivni) {
        this.id = id;
        this.celeJmeno = celeJmeno;
        this.specializace = specializace;
        this.telefon = telefon;
        this.email = email;
        this.aktivni = aktivni;
    }

    public static LekarDto fromEntity(Lekar lekar) {
        if (lekar == null) return null;
        return new LekarDto(
                lekar.getId(),
                lekar.getCeleJmeno(),
                lekar.getSpecializace(),
                lekar.getTelefon(),
                lekar.getEmail(),
                lekar.isAktivni()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCeleJmeno() {
        return celeJmeno;
    }

    public void setCeleJmeno(String celeJmeno) {
        this.celeJmeno = celeJmeno;
    }

    public String getSpecializace() {
        return specializace;
    }

    public void setSpecializace(String specializace) {
        this.specializace = specializace;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAktivni() {
        return aktivni;
    }

    public void setAktivni(boolean aktivni) {
        this.aktivni = aktivni;
    }
}

