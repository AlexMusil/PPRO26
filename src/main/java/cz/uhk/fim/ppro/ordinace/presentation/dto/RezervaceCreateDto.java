package cz.uhk.fim.ppro.ordinace.presentation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class RezervaceCreateDto {

    @NotNull(message = "Musíte vybrat pacienta")
    private UUID pacientId;

    @NotNull(message = "Musíte vybrat lékaře")
    private UUID lekarId;

    @NotNull(message = "Musíte zadat datum a čas rezervace")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime casOd;

    @NotNull(message = "Délka rezervace musí být zadána")
    @Min(value = 15, message = "Minimální délka rezervace je 15 minut")
    private Integer delkaMinut = 15;

    @NotBlank(message = "Důvod návštěvy musí být vyplněn")
    private String duvodNavstevy;

    public RezervaceCreateDto() {
    }

    public RezervaceCreateDto(UUID pacientId, UUID lekarId, LocalDateTime casOd, Integer delkaMinut, String duvodNavstevy) {
        this.pacientId = pacientId;
        this.lekarId = lekarId;
        this.casOd = casOd;
        this.delkaMinut = delkaMinut;
        this.duvodNavstevy = duvodNavstevy;
    }

    public UUID getPacientId() {
        return pacientId;
    }

    public void setPacientId(UUID pacientId) {
        this.pacientId = pacientId;
    }

    public UUID getLekarId() {
        return lekarId;
    }

    public void setLekarId(UUID lekarId) {
        this.lekarId = lekarId;
    }

    public LocalDateTime getCasOd() {
        return casOd;
    }

    public void setCasOd(LocalDateTime casOd) {
        this.casOd = casOd;
    }

    public Integer getDelkaMinut() {
        return delkaMinut;
    }

    public void setDelkaMinut(Integer delkaMinut) {
        this.delkaMinut = delkaMinut;
    }

    public String getDuvodNavstevy() {
        return duvodNavstevy;
    }

    public void setDuvodNavstevy(String duvodNavstevy) {
        this.duvodNavstevy = duvodNavstevy;
    }
}

