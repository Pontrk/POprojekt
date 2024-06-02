package restaurant;

public abstract class Employee {
    private int idPracownika;
    private String imie;
    private String stanowisko;
    private float efektywnosc;

    public Employee(int idPracownika, String imie, String stanowisko, float efektywnosc) {
        this.idPracownika = idPracownika;
        this.imie = imie;
        this.stanowisko = stanowisko;
        this.efektywnosc = efektywnosc;
    }

    public abstract void wykonajZadanie();

    public abstract void przygotujZamowienie(Order order);

    // Gettery i settery
    public int getIdPracownika() {
        return idPracownika;
    }

    public String getImie() {
        return imie;
    }

    public String getStanowisko() {
        return stanowisko;
    }

    public float getEfektywnosc() {
        return efektywnosc;
    }

    public void setEfektywnosc(float efektywnosc) {
        this.efektywnosc = efektywnosc;
    }
}
