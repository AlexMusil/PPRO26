-- V1__create_pacient_table.sql
-- Vytvoření pilotní entity Pacient pro semestrální projekt PPRO Zadání A (Rezervace v ordinaci)

CREATE TABLE pacient (
    id UUID PRIMARY KEY,
    jmeno VARCHAR(50) NOT NULL,
    prijmeni VARCHAR(50) NOT NULL,
    rok_narozeni INT NOT NULL,
    telefon VARCHAR(20) NOT NULL,
    cislo_pojistence VARCHAR(20) NOT NULL,
    pojistovna VARCHAR(10) NOT NULL,
    poznamka TEXT,
    vytvoreno_dne TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pacient_cislo_pojistence UNIQUE (cislo_pojistence)
);

-- Indexy pro bleskové vyhledávání při telefonátu
CREATE INDEX idx_pacient_prijmeni ON pacient (prijmeni);
CREATE INDEX idx_pacient_telefon ON pacient (telefon);
