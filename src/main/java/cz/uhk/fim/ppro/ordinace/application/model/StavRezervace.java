package cz.uhk.fim.ppro.ordinace.application.model;

public enum StavRezervace {
    PLANOVANA("Plánovaná"),
    PROBEHLA("Proběhlá"),
    ZRUSENA_PACIENTEM("Zrušena pacientem"),
    ZRUSENA_LEKAREM("Zrušena lékařem"),
    K_PREOBJEDNANI("K přeobjednání");

    private final String popis;

    StavRezervace(String popis) {
        this.popis = popis;
    }

    public String getPopis() {
        return popis;
    }

    public boolean isAktivniSlot() {
        return this == PLANOVANA;
    }
}

