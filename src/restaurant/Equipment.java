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

    public void checkStatus() {
        System.out.println("Sprzet " + typ + " jest w statusie: " + status);
    }

    public void reportIssue() {
        System.out.println("Zgłoszenie problemu ze sprzetem " + typ);
    }

    public void repair() {
        System.out.println("Naprawa sprzetu " + typ);
        this.status = "sprawny";
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
}
