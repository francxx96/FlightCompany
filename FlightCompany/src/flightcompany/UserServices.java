package flightcompany;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserServices {
	private Map<String, User> loggedUsers = new HashMap<String, User>();
	
	// consuma richieste registrazione utente -OK
	public boolean registrationRequest(String name, String surname, String nickname, String password) {
		Map<String, User> users = Utilities.getUsers();
		
    	if (users.containsKey(nickname))
    		return false;
    	
		User newUsr = new User(name, surname, nickname, password);
		users.put(nickname, newUsr);
		Utilities.writeUsers(users, Utilities.USERS_FILE);
		return true;
    	
	}
	
	// consuma richieste login
	public boolean loginRequest(String nickname, String password) {
		Map<String, User> users = Utilities.getUsers();
    	
    	User usr = users.get(nickname);
    	if (usr == null || !usr.getPassword().equals(password))
    		return false;
    	
		loggedUsers.put(usr.getNickname(), usr);
		return true;
    }
	
	// consuma richieste logout
	public boolean logoutRequest(String nickname) {
		
		if (loggedUsers.remove(nickname) == null)
			return false;
		
		return true;
	}
	
	// ======================================================== consuma richieste voli disponibili !!!!
	public void searchRoute(AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		
	}
	
	// consuma richieste prenotazione volo
	public boolean bookFlightRequest(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		return cst.bookFlight(f);
	}
	
	// consuma richieste cancellazione volo
	public boolean cancelFlightRequest(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		return cst.cancelFlight(f);
	}
	
	// consuma richieste aggiornamento saldo
	public boolean chargeMoneyRequest(float amount, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		return cst.chargeMoney(amount);
	}
	
	
	// consuma richieste registrazione voli
	public boolean addFlight(String nickname, String flightId, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.addFlight(flightId, planeModel, depCity, arrCity, depTime);
	}
	
	// consuma richieste cancellazione voli
	public boolean removeFlightRequest(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.removeFlight(flightId);
	}
	
	// consuma richieste aggiornamento ritardi voli
	public boolean putDelayRequest(String flightId, int minutes, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.putDelay(flightId, minutes);
	}
	
	// consuma richieste promozioni -OK
	public boolean putDealRequest(String flightId, float dealPerc, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.putDeal(flightId, dealPerc);
	}
}
