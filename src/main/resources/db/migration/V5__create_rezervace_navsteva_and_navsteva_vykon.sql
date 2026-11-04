-- V5__create_rezervace_navsteva_and_navsteva_vykon.sql
-- Vytvoření tabulek pro rezervace, realizované návštěvy a asociační vazbu M:N na zdravotní výkony s atributem pocet

CREATE TABLE rezervace (
    id UUID PRIMARY KEY,
    pacient_id UUID NOT NULL REFERENCES pacient(id),
    lekar_id UUID NOT NULL REFERENCES lekar(id),
    cas_od TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    cas_do TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    stav VARCHAR(30) NOT NULL DEFAULT 'PLANOVANA', -- PLANOVANA, PROBEHLA, ZRUSENA_PACIENTEM, ZRUSENA_LEKAREM, K_PREOBJEDNANI
    duvod_navstevy VARCHAR(255) NOT NULL,
    poznamka_zruseni VARCHAR(255),
    vytvoreno_dne TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_rezervace_cas CHECK (cas_od < cas_do)
);

CREATE INDEX idx_rezervace_lekar_cas ON rezervace (lekar_id, cas_od, cas_do);
CREATE INDEX idx_rezervace_pacient ON rezervace (pacient_id);
CREATE INDEX idx_rezervace_stav ON rezervace (stav);

CREATE TABLE navsteva (
    id UUID PRIMARY KEY,
    rezervace_id UUID UNIQUE NOT NULL REFERENCES rezervace(id) ON DELETE RESTRICT,
    datum_cas TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lekarska_zprava TEXT NOT NULL,
    doporuceni TEXT
);

CREATE INDEX idx_navsteva_datum_cas ON navsteva (datum_cas);

-- Vazba M:N mezi Navsteva a Vykon s povinným doplňkovým atributem pocet
CREATE TABLE navsteva_vykon (
    id UUID PRIMARY KEY,
    navsteva_id UUID NOT NULL REFERENCES navsteva(id) ON DELETE CASCADE,
    vykon_kod VARCHAR(10) NOT NULL REFERENCES vykon(kod) ON DELETE RESTRICT,
    pocet INT NOT NULL CHECK (pocet > 0),
    poznamka VARCHAR(255),
    CONSTRAINT uq_navsteva_vykon UNIQUE (navsteva_id, vykon_kod)
);

CREATE INDEX idx_navsteva_vykon_navsteva ON navsteva_vykon (navsteva_id);
CREATE INDEX idx_navsteva_vykon_kod ON navsteva_vykon (vykon_kod);

