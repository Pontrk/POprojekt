package restaurant;

public class Cook extends Employee {
    public Cook(int idPracownika, String imie, String stanowisko, float efektywnosc) {
        super(idPracownika, imie, stanowisko, efektywnosc);
    }

    @Override
    public void wykonajZadanie() {
        System.out.println("Kucharz przygotowuje dania.");
    }

    @Override
    public void przygotujZamowienie(Order order) {
        System.out.println("Kucharz przygotowuje zamowienie: " + order.getIdZamowienia());
    }
}
