package flightcompany;

import java.util.HashSet;
import java.util.Set;

public class Customer extends User {
	private static final long serialVersionUID = 1L;
	
	private double money;
    private Set<Flight> bookedFlights;

	public Customer(String name, String surname, String nickname, String password) {
		super(name, surname, nickname, password);

        this.money = 0;
        this.bookedFlights = new HashSet<>();
	}

    public boolean bookFlight(Flight f) {
    	if (bookedFlights.add(f) && f.addPassenger(this))
    		return true;

    	return false;
    }
    
    public boolean cancelFlight(Flight f) {
    	if (bookedFlights.remove(f) && f.removePassenger(this))
    		return true;
    	
    	return false;
    }
    
    public boolean chargeMoney(double amount) {
        if (amount <= 0)
        	return false;
        
        money += amount;
        return true;
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
