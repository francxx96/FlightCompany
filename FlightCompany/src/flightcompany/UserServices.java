package flightcompany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServices {
	private Map<String, User> users;
	private Map<String, User> loggedUsers;
	private Map<String, Flight> flights;
	private Map<AirportCity, Airport> airports;
	
	public UserServices() {
		this.users = Utilities.getUsers();
		this.loggedUsers = new HashMap<String, User>();
		this.flights = Utilities.getFlights();
		this.airports = Utilities.getAirports();
	}
	
	public boolean registrationRequest(String name, String surname, String nickname, String password, boolean admin) {		
    	
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
		
		User usr = users.get(nickname);
    	if (usr == null || !usr.getPassword().equals(password))
    		return false;
    	
    	usr.setLogin(true);
		loggedUsers.put(usr.getNickname(), usr);
		return true;
    }
	
	public boolean logoutRequest(String nickname) {
		User usr = loggedUsers.get(nickname);

		if (usr == null)
			return false;
		
		usr.setLogin(false);
		loggedUsers.remove(nickname);
		return true;
	}
	
	public List<List<Flight>> searchRoutes(AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		
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
					List<List<Flight>> subroutes = searchRoutes(f.getArrAirport(), arrAirp, f.getArrTime(), visitedAirports);
					
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
		Customer cst = (Customer) loggedUsers.get(nickname);
		if (cst == null)
			return false;
				
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		boolean isBooked = cst.bookFlight(f);
		if (isBooked) {
			//users.put(cst.getNickname(), cst);
			//loggedUsers.put(cst.getNickname(), cst);
			//flights.put(f.getId(), f);
			Utilities.writeUsers(users);
			Utilities.writeFlights(flights);
		}
		
		return isBooked;
	}
	

	public boolean cancelFlight(String flightId, String nickname) {
		Customer cst = (Customer) loggedUsers.get(nickname);
		if (cst == null)
			return false;
				
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		boolean isCancelled = cst.cancelFlight(f);
		if (isCancelled) {
			// The customer will receive a refund if he cancels the reservation at least one hour before departure
			if (f.getDepTime().isBefore(LocalDateTime.now().plusHours(1)))
				cst.setMoney(cst.getMoney()+f.getCost());
			
			//users.put(cst.getNickname(), cst);
			//loggedUsers.put(cst.getNickname(), cst);
			//flights.put(f.getId(), f);
			Utilities.writeUsers(users);
			Utilities.writeFlights(flights);
		}
		
		return isCancelled;
	}
	

	public boolean chargeMoney(float amount, String nickname) {
		Customer cst = (Customer) loggedUsers.get(nickname);
		if (cst == null)
			return false;
		
		boolean isCharged = cst.chargeMoney(amount);
		if (isCharged) {
			//users.put(cst.getNickname(), cst);
			//loggedUsers.put(cst.getNickname(), cst);
			Utilities.writeUsers(users);
		}
		return isCharged;
	}

	/**
	 * Admin has the ability to create a new Flight object.
	 * @param nickname the nickname of the administrator
	 * @param flightId the ID of the new Flight
	 * @param planeModel the airplane model of the new Flight
	 * @param depCity the departure airport city of the new Flight
	 * @param arrCity the arrival airport city of the new Flight
	 * @param depTime the departure time of the new Flight
	 * @return true if the creation of the new flight has success, false otherwise
	 */
	public boolean addFlight(String flightId, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime, String nickname) {

		Admin admin = (Admin) loggedUsers.get(nickname);

		if (admin == null || flights.containsKey(flightId))
			return false;
		
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		
		if (depAirport == null || arrAirport == null)
			return false;

		Airplane plane = new Airplane(planeModel);
		Flight newFlight = new Flight(flightId, plane, depAirport, arrAirport, depTime);
		flights.put(newFlight.getId(), newFlight);
		Utilities.writeFlights(flights);
		depAirport.addFlight(newFlight);
		//airports.put(depAirport.getCity(), depAirport);
		Utilities.writeAirports(airports);
		
		return true;
	}
	
	/**
	 * Admin has the ability to delete an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param nickname the nickname of the administrator
	 * @return true if the deletion of the existing flight has success, false otherwise
	 */
	public boolean removeFlight(String flightId, String nickname) {	
		Admin admin = (Admin) loggedUsers.get(nickname);
		
		if (admin == null || !flights.containsKey(flightId))
			return false;
		
		Flight removedFlight = flights.remove(flightId);
		Utilities.writeFlights(flights);
		
		Airport depAirport = airports.get(removedFlight.getDepAirport().getCity());
		depAirport.removeFlight(removedFlight);
		//airports.put(depAirport.getCity(), depAirport);
		Utilities.writeAirports(airports);
		
		return true;
	}
	
	
	/**
	 * Admin has the ability to add minutes of delay an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param minutes amount of time in minutes to be added as delay
	 * @param nickname the nickname of the administrator
	 * @return true if the addition of delay has success, false otherwise
	 */
	public boolean putDelay(String flightId, int minutes, String nickname) {
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		f.setDepTime(f.getDepTime().plusMinutes(minutes));
		f.setArrTime(f.getArrTime().plusMinutes(minutes));
		//flights.put(f.getId(), f);
		Utilities.writeFlights(flights);
		
		return true;
	}
	
	/**
	 * Admin has the ability to put a discount on the cost of an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param dealPerc percentage of discount to be applied to the cost
	 * @param nickname the nickname of the administrator
	 * @return true if the discount is set correctly, false otherwise
	 */
	public boolean putDeal(String flightId, double dealPerc, String nickname) {
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return false;
		
		Flight f = flights.get(flightId);
		if (f == null)
			return false;
		
		if (dealPerc < 0 && dealPerc >= 1)
			return false;
		
		f.setCost(f.getCost() * dealPerc);
		//flights.put(f.getId(), f);
		Utilities.writeFlights(flights);
		
		return true;
	}
}
