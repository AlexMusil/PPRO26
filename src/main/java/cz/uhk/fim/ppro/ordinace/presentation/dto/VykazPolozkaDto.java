package cz.uhk.fim.ppro.ordinace.presentation.dto;

public class VykazPolozkaDto {

    private String vykonKod;
    private String vykonNazev;
    private int bodyZaKus;
    private int celkemPocet;
    private int celkemBodu;

    public VykazPolozkaDto() {
    }

    public VykazPolozkaDto(String vykonKod, String vykonNazev, int bodyZaKus, int celkemPocet, int celkemBodu) {
        this.vykonKod = vykonKod;
        this.vykonNazev = vykonNazev;
        this.bodyZaKus = bodyZaKus;
        this.celkemPocet = celkemPocet;
        this.celkemBodu = celkemBodu;
    }

    public String getVykonKod() {
        return vykonKod;
    }

    public void setVykonKod(String vykonKod) {
        this.vykonKod = vykonKod;
    }

    public String getVykonNazev() {
        return vykonNazev;
    }

    public void setVykonNazev(String vykonNazev) {
        this.vykonNazev = vykonNazev;
    }

    public int getBodyZaKus() {
        return bodyZaKus;
    }

    public void setBodyZaKus(int bodyZaKus) {
        this.bodyZaKus = bodyZaKus;
    }

    public int getCelkemPocet() {
        return celkemPocet;
    }

    public void setCelkemPocet(int celkemPocet) {
        this.celkemPocet = celkemPocet;
    }

    public int getCelkemBodu() {
        return celkemBodu;
    }

    public void setCelkemBodu(int celkemBodu) {
        this.celkemBodu = celkemBodu;
    }
}
