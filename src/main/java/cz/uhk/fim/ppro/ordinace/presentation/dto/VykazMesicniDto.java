package cz.uhk.fim.ppro.ordinace.presentation.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VykazMesicniDto {

    private int rok;
    private int mesic;
    private UUID lekarId;
    private String lekarJmeno;
    private int celkemNavstev;
    private int celkemBodu;
    private List<VykazPolozkaDto> polozky = new ArrayList<>();

    public VykazMesicniDto() {
    }

    public VykazMesicniDto(int rok, int mesic, UUID lekarId, String lekarJmeno) {
        this.rok = rok;
        this.mesic = mesic;
        this.lekarId = lekarId;
        this.lekarJmeno = lekarJmeno;
    }

    public int getRok() {
        return rok;
    }

    public void setRok(int rok) {
        this.rok = rok;
    }

    public int getMesic() {
        return mesic;
    }

    public void setMesic(int mesic) {
        this.mesic = mesic;
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

    public int getCelkemNavstev() {
        return celkemNavstev;
    }

    public void setCelkemNavstev(int celkemNavstev) {
        this.celkemNavstev = celkemNavstev;
    }

    public int getCelkemBodu() {
        return celkemBodu;
    }

    public void setCelkemBodu(int celkemBodu) {
        this.celkemBodu = celkemBodu;
    }

    public List<VykazPolozkaDto> getPolozky() {
        return polozky;
    }

    public void setPolozky(List<VykazPolozkaDto> polozky) {
        this.polozky = polozky;
    }
}

