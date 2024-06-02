package restaurant;

import java.util.List;

public class Waiter extends Employee {
    private List<Table> stoliki;

    public Waiter(int idPracownika, String imie, String stanowisko, float efektywnosc, List<Table> stoliki) {
        super(idPracownika, imie, stanowisko, efektywnosc);
        this.stoliki = stoliki;
    }

    @Override
    public void wykonajZadanie() {
        System.out.println("Kelner obsluguje klientow.");
    }

    @Override
    public void przygotujZamowienie(Order order) {
        System.out.println("Kelner przyjmuje zamowienie: " + order.getIdZamowienia());
    }
}
