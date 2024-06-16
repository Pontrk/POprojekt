package restaurant;

public class Equipment {
    private int idSprzetu;
    private String typ;
    private String status;

    public Equipment(int idSprzetu, String typ, String status) {
        this.idSprzetu = idSprzetu;
        this.typ = typ;
        this.status = status;
    }

    public int getIdSprzetu() {
        return idSprzetu;
    }

    public String getTyp() {
        return typ;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBroken() {
        return "uszkodzony".equals(status);
    }

    public void repair() {
        setStatus("sprawny");
        System.out.println("Sprzęt " + typ + " został naprawiony.");
    }
}
