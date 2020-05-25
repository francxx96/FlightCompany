package flightcompany;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents users of the airline who are system customers
 * @author Emilio, Francesco
 *
 */
public class Customer extends User {
	private static final long serialVersionUID = 1L;
	
	private double money;
    private Set<Flight> bookedFlights;

	public Customer(String name, String surname, String nickname, String password) {
		super(name, surname, nickname, password);

        this.money = 0;
        this.bookedFlights = new HashSet<>();
	}

	/**
	 * Book an available seat on the flight provided
	 * @param f the flight to book
	 * @return true in case of success
	 */
    public boolean bookFlight(Flight f) {
    	if (bookedFlights.add(f) && f.addPassenger(this))
    		return true;

    	return false;
    }
    
	/**
	 * Cancel a reservation on the flight provided
	 * @param f the flight booked
	 * @return true in case of success
	 */
    public boolean cancelFlight(Flight f) {
    	if (bookedFlights.remove(f) && f.removePassenger(this))
    		return true;
    	
    	return false;
    }
    
	/**
	 * Adds the amount to the user's balance
	 * @param amount to add
	 * @return true in case of success
	 */
    public boolean chargeMoney(double amount) {
        if (amount <= 0)
        	return false;
        
        money += amount;
        return true;
    }
    
    /**
     * Indicates that this user is a customer
     * @return false
     */
	@Override
	public boolean isAdmin() {
		return false;
	}
    
    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
    
    public Set<Flight> getBookedFlights() {
		return bookedFlights;
	}

	public void setBookedFlights(Set<Flight> bookedFlights) {
		this.bookedFlights = bookedFlights;
	}

	@Override
    public String toString() {
        return "Customer: [money=" + String.format("%.2f",money) + ", fligthsBookedNum=" + bookedFlights.size() + ", "+ super.toString().substring(2);
    }
	
}
