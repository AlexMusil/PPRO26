package cz.uhk.fim.ppro.ordinace.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "lekar")
public class Lekar {

    @Id
    private UUID id;

    @Column(name = "titul_pred", length = 20)
    private String titulPred;

    @NotBlank(message = "Jméno lékaře nesmí být prázdné")
    @Column(name = "jmeno", nullable = false, length = 50)
    private String jmeno;

    @NotBlank(message = "Příjmení lékaře nesmí být prázdné")
    @Column(name = "prijmeni", nullable = false, length = 50)
    private String prijmeni;

    @Column(name = "titul_za", length = 20)
    private String titulZa;

    @NotBlank(message = "Specializace nesmí být prázdná")
    @Column(name = "specializace", nullable = false, length = 100)
    private String specializace;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "aktivni", nullable = false)
    private boolean aktivni = true;

    @Column(name = "vytvoreno_dne", nullable = false, updatable = false)
    private LocalDateTime vytvorenoDne;

    @OneToMany(mappedBy = "lekar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrdinacniDoba> ordinacniDoby = new ArrayList<>();

    public Lekar() {
        this.id = UUID.randomUUID();
        this.vytvorenoDne = LocalDateTime.now();
        this.aktivni = true;
    }

    public Lekar(UUID id, String titulPred, String jmeno, String prijmeni, String titulZa,
                 String specializace, String telefon, String email) {
        this.id = id != null ? id : UUID.randomUUID();
        this.titulPred = titulPred;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.titulZa = titulZa;
        this.specializace = specializace;
        this.telefon = telefon;
        this.email = email;
        this.aktivni = true;
        this.vytvorenoDne = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulPred() {
        return titulPred;
    }

    public void setTitulPred(String titulPred) {
        this.titulPred = titulPred;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getTitulZa() {
        return titulZa;
    }

    public void setTitulZa(String titulZa) {
        this.titulZa = titulZa;
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

    public LocalDateTime getVytvorenoDne() {
        return vytvorenoDne;
    }

    public void setVytvorenoDne(LocalDateTime vytvorenoDne) {
        this.vytvorenoDne = vytvorenoDne;
    }

    public List<OrdinacniDoba> getOrdinacniDoby() {
        return ordinacniDoby;
    }

    public void setOrdinacniDoby(List<OrdinacniDoba> ordinacniDoby) {
        this.ordinacniDoby = ordinacniDoby;
    }

    public String getCeleJmeno() {
        StringBuilder sb = new StringBuilder();
        if (titulPred != null && !titulPred.isBlank()) {
            sb.append(titulPred.trim()).append(" ");
        }
        sb.append(jmeno.trim()).append(" ").append(prijmeni.trim());
        if (titulZa != null && !titulZa.isBlank()) {
            sb.append(", ").append(titulZa.trim());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lekar lekar = (Lekar) o;
        return Objects.equals(id, lekar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getCeleJmeno() + " (" + specializace + ")";
    }
}
