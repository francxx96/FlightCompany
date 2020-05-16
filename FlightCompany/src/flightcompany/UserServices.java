package flightcompany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServices {
	private Map<String, User> loggedUsers = new HashMap<String, User>();
	
	public boolean registrationRequest(String name, String surname, String nickname, String password, boolean admin) {
		Map<String, User> users = Utilities.getUsers();
		
    	if (users.containsKey(nickname))
    		return false;
    	
    	User newUsr;
    	if(admin)
    		newUsr = new Admin(name, surname, nickname, password);
    	else
    		newUsr = new Customer(name, surname, nickname, password);
		
    	users.put(nickname, newUsr);
		Utilities.writeUsers(users);
		return true;
	}
	
	public boolean loginRequest(String nickname, String password) {
		Map<String, User> users = Utilities.getUsers();
    	
    	User usr = users.get(nickname);
    	if (usr == null || !usr.getPassword().equals(password))
    		return false;
    	
		loggedUsers.put(usr.getNickname(), usr);
		return true;
    }
	
	public boolean logoutRequest(String nickname) {
		
		if (loggedUsers.remove(nickname) == null)
			return false;
		
		return true;
	}
	
	public List<List<Flight>> searchRoutes(AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		Map<AirportCity, Airport> airports = Utilities.getAirports();
		
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		return searchRoutes(depAirport, arrAirport, depTime, new ArrayList<Airport>());
	}
	
	public List<List<Flight>> searchRoutes(Airport depAirp, Airport arrAirp, LocalDateTime depTime, List<Airport> visitedAirports) {
		List<List<Flight>> routes = new ArrayList<List<Flight>>();
		
		Collection<Flight> departures = depAirp.getFlights().values();
		
		for (Flight f : departures) {
			if (!visitedAirports.contains(f.getArrAirport()) && f.getDepTime().isAfter(depTime)) {
				if (f.getArrAirport() == arrAirp)
					routes.add(Arrays.asList(f));
				
				else {
					visitedAirports.add(depAirp);
					List<List<Flight>> subroutes = searchRoutes(f.getArrAirport(), arrAirp, f.getDepTime(), visitedAirports);
					
					for (List<Flight> r : subroutes) {
						r.add(0, f);
						routes.add(r);
					}
				}
			}
		}
		
		return routes;
	}
	
	public boolean bookFlight(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		boolean isBooked = cst.bookFlight(f);
		if (isBooked)
			Utilities.writeUsers(users);
		
		return isBooked;
	}
	
	public boolean cancelFlight(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		boolean isCancelled = cst.cancelFlight(f);
		if (isCancelled) {
			// The customer will receive a refund if he cancels the reservation at least one hour before departure
			if (f.getDepTime().isBefore(LocalDateTime.now().plusHours(1)))
				cst.setMoney(cst.getMoney()+f.getCost());	
			
			Utilities.writeUsers(users);
		}
		
		return isCancelled;
	}
	
	public boolean chargeMoney(float amount, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return false;
		
		boolean isCharged = cst.chargeMoney(amount);
		if (isCharged)
			Utilities.writeUsers(users);
		
		return isCharged;
	}
	
	
	public boolean addFlight(String flightId, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.addFlight(flightId, planeModel, depCity, arrCity, depTime);
	}
	
	public boolean removeFlight(String flightId, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.removeFlight(flightId);
	}
	
	// consuma richieste aggiornamento ritardi voli
	public boolean putDelay(String flightId, int minutes, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.putDelay(flightId, minutes);
	}
	
	// consuma richieste promozioni -OK
	public boolean putDeal(String flightId, float dealPerc, String nickname) {
		Map<String, User> users = Utilities.getUsers();
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		return admin.putDeal(flightId, dealPerc);
	}
}
