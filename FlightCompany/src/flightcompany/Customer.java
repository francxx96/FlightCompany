package flightcompany;

import java.util.HashSet;
import java.util.Set;

public class Customer extends User {
    private double money;
    private Set<Flight> bookedFlights;

	public Customer(String name, String surname, String nickname, String password) {
		super(name, surname, nickname, password);

        this.money = 0;
        this.bookedFlights = new HashSet<>();
	}

    public boolean bookFlight(Flight f) {
        if (money >= f.getCost()) {
            money -= f.getCost();
            bookedFlights.add(f);
            return true;
        }
        
        return false;
    }
    
    public boolean chargeMoney(float amount) {
        if (amount > 0) {
            money += amount;
            return true;
        }
        
        return false;
    }
    
    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
    
    @Override
    public String toString() {
        return "Customer: " + super.toString();
    }
}
