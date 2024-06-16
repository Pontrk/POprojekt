package restaurant;

public class Cook extends Employee {

    public Cook(int idPracownika, String imie, String stanowisko, float efektywnosc) {
        super(idPracownika, imie, stanowisko, efektywnosc);
    }

    @Override
    public void wykonajZadanie() {
        System.out.println(imie + " przygotowuje jedzenie.");
        for (Order order : RestaurantSimulation.getOrders()) {
            if (order.getStatus().equals("nowe")) {
                try {
                    long czasPrzygotowania = (long) (2000 / efektywnosc);
                    Thread.sleep(Math.max(czasPrzygotowania, 100)); // Czas przygotowania zamówienia
                    order.setStatus("gotowe");
                    System.out.println(imie + " przygotował zamówienie nr " + order.getIdZamowienia());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid sleep time calculated: " + e.getMessage());
                }
            }
        }
    }
}
