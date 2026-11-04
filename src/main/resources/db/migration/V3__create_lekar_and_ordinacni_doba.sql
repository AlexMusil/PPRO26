-- V3__create_lekar_and_ordinacni_doba.sql
-- Vytvoření tabulek pro lékaře a jejich ordinační hodiny

CREATE TABLE lekar (
    id UUID PRIMARY KEY,
    titul_pred VARCHAR(20),
    jmeno VARCHAR(50) NOT NULL,
    prijmeni VARCHAR(50) NOT NULL,
    titul_za VARCHAR(20),
    specializace VARCHAR(100) NOT NULL,
    telefon VARCHAR(20),
    email VARCHAR(100),
    aktivni BOOLEAN NOT NULL DEFAULT TRUE,
    vytvoreno_dne TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lekar_prijmeni ON lekar (prijmeni);
CREATE INDEX idx_lekar_aktivni ON lekar (aktivni);

CREATE TABLE ordinacni_doba (
    id UUID PRIMARY KEY,
    lekar_id UUID NOT NULL REFERENCES lekar(id) ON DELETE CASCADE,
    den_v_tydnu INT NOT NULL CHECK (den_v_tydnu BETWEEN 1 AND 7), -- 1 = Pondělí, ..., 7 = Neděle
    cas_od TIME NOT NULL,
    cas_do TIME NOT NULL,
    pracoviste VARCHAR(150) NOT NULL DEFAULT 'Ordinace Na Vyhlídce',
    lze_objednavat BOOLEAN NOT NULL DEFAULT TRUE,
    poznamka VARCHAR(255),
    CONSTRAINT chk_ordinacni_doba_cas CHECK (cas_od < cas_do)
);

CREATE INDEX idx_ordinacni_doba_lekar_den ON ordinacni_doba (lekar_id, den_v_tydnu);
