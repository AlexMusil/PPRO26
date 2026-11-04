-- V4__create_vykon_table.sql
-- Vytvoření číselníku zdravotních výkonů

CREATE TABLE vykon (
    kod VARCHAR(10) PRIMARY KEY,
    nazev VARCHAR(150) NOT NULL,
    body INT NOT NULL CHECK (body >= 0),
    popis TEXT,
    aktivni BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_vykon_nazev ON vykon (nazev);
