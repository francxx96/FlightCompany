package flightcompany;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Implement the services that are required by the airline
 * @author Emilio, Francesco
 */
public class UserServices {
	private Map<String, User> users;
	private Map<String, Flight> flights;
	private Map<AirportCity, Airport> airports;
	
	public UserServices() {
		this.users = Utilities.getUsers();
		this.flights = Utilities.getFlights();
		this.airports = Utilities.getAirports();
	}
	
	/**
	 * Handles the request to register a new user on the system
	 * @param name, surname, nickname, password, admin
	 * @return a string with the outcome of the operation
	 */
	public synchronized String registrationRequest(String name, String surname, String nickname, String password, boolean admin) {		
    	
		if (users.containsKey(nickname))
    		return "[Error] Nickname already in use, please choose another one";
    	
    	User newUsr;
    	if(admin)
    		newUsr = new Admin(name, surname, nickname, password);
    	else
    		newUsr = new Customer(name, surname, nickname, password);
		
		users.put(nickname, newUsr);
		Utilities.writeUsers(users);
		return "User registration completed";
	}
	
	/**
	 * Handles a user's login request
	 * @param nickname, password
	 * @return a string with the outcome of the operation
	 */
	public synchronized String loginRequest(String nickname, String password) {
		
		User usr = users.get(nickname);
    	if (usr == null)
    		return "[Error] User not registered";
    	
    	if (usr.isLogin())
    		return "[Error] User already logged in";
    	
    	if (!usr.getPassword().equals(password))
    		return "[Error] Wrong password";
    	
    	usr.setLogin(true);

    	System.out.println(usr.getClass());
    	if(usr.isAdmin())
    		return "Admin login completed";
    	else
    		return "Customer login completed";
    }
	
	/**
	 * Handles a user's logout request
	 * @param nickname
	 * @return a string with the outcome of the operation
	 */
	public synchronized String logoutRequest(String nickname) {
		
		User usr = users.get(nickname);
		if (usr == null)
    		return "[Error] User not registered";
		
		if (!usr.isLogin())
			return "[Error] User not logged in";
		
		usr.setLogin(false);
		return "User logout completed";
	}
	
	/**
	 * Calculate routes, even consisting of multiple flights, 
	 * that leave the departure airport after a date and time, 
	 * and arrive at the established destination airport
	 * @param departure city, arrival city, departure date_time
	 * @return all the available routes
	 */
	public synchronized String searchRoutes(AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		return printRoutes(searchRoutes(depAirport, arrAirport, depTime, new ArrayList<Airport>()));
	}
	
	/**
	 * Calculate routes that leave the departure airport after 
	 * a date and time and arrive at the destination airport without 
	 * going through the airports already visited
	 * @param departure city, arrival city, departure date_time, visited airports
	 * @return all the available routes
	 */
	private synchronized List<List<Flight>> searchRoutes(Airport depAirp, Airport arrAirp, LocalDateTime depTime, List<Airport> visitedAirports) {
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
	
	/**
	 * Returns a string containing the list of routes provided
	 * @param routes
	 * @return a string with all the routes
	 */
    private String printRoutes(List<List<Flight>> routes) {
    	String routesString = "The following flights are registered: \n\n";
    	
    	for(List<Flight> route : routes) {
    		routesString += "Route consisting of " + route.size() + " flights:\n";
    		for(Flight f : route) {
    			routesString += f.toString() + "\n";
    		}
    		routesString += "\n---------------------------------------------------------------------\n";
    	}
    	
    	return routesString;
    }
    
	/**
	 * Returns a string containing the list of flights
	 * @return a string with all the flights
	 */
    public String printFlights() {
    	String flightsString = "Flights registered: \n";
    	
    	for(Flight f : flights.values()) {
    		flightsString += f.toString() + "\n";
    		flightsString += "\n---------------------------------------------------------------------\n";
    	}
    	
    	return flightsString;
    }

    /**
     * Handles the user's flight booking request
     * @param flightId, nickname
     * @return a string with the outcome of the operation
     */
	public synchronized String bookFlight(String flightId, String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
				
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (cst.getMoney() < f.getCost())
			return "[Error] Customer has not enough money for buying ticket";
		
		if (!cst.bookFlight(f)) 
			return "[Error] Generic error during reservation completion";
		
		Utilities.writeUsers(users);
		Utilities.writeFlights(flights);
		
		return "Reservation completed";
	}

	/**
	 * Returns a string containing the user's booked flights
	 * @param nickname
	 * @return a string with all the flights booked
	 */
	public synchronized String bookedFlight(String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
				
		String flightsString = "You have booked the following flights: \n\n";
    	
		for(Flight f : cst.getBookedFlights()) {
			flightsString += "Flight ID: " + f.getId() + " - " + f.getPlane() + "\n"
	                + "From " + f.getDepAirport() + ", at time: " + f.getDepTime() + "\n"
	                + "To " + f.getArrAirport() + ", at time: " + f.getArrTime() + "\n\n";
		}
    	
    	return flightsString;
	}
	
    /**
     * Manages the request for cancellation of the flight reservation by the user
     * @param flightId, nickname
     * @return a string with the outcome of the operation
     */
	public synchronized String cancelFlight(String flightId, String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
		
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (!cst.cancelFlight(f)) 
			return "[Error] FlightID not booked";
		
		// The customer will receive a refund if he cancels the reservation at least one hour before departure
		if (f.getDepTime().isAfter(LocalDateTime.now().plusHours(1)))
			cst.setMoney(cst.getMoney()+f.getCost());
		
		//users.put(cst.getNickname(), cst);
		//loggedUsers.put(cst.getNickname(), cst);
		//flights.put(f.getId(), f);
		Utilities.writeUsers(users);
		Utilities.writeFlights(flights);
		
		return "Reservation is deleted successfully";
	}
	
	/**
	 * Update the user's balance by adding the amount provided
	 * @param amount, nickname
	 * @return a string with the outcome of the operation
	 */
	public synchronized String chargeMoney(double amount, String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
		
		if (!cst.chargeMoney(amount))
			return "[Error] Negative amount was inserted";
		
		//users.put(cst.getNickname(), cst);
		//loggedUsers.put(cst.getNickname(), cst);
		Utilities.writeUsers(users);
		
		return "Money charged successfully";
	}

	
	/**
	 * Adds a flight with the data provided by the admin
	 * @param nickname of the administrator, 
	 * @param flightId of the new flight
	 * @param planeModel the airplane model
	 * @param depCity the departure airport city
	 * @param arrCity the arrival airport city
	 * @param depTime the departure date_time
	 * @return a string with the outcome of the operation
	 */
	public synchronized String addFlight(String flightId, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime, String nickname) {

		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return "[Error] Adminisitrator not registered";
		
		if (!admin.isLogin())
			return "[Error] Administrator not logged in";
		
		if (flights.containsKey(flightId))
			return "[Error] Flight ID already in use";
		
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		
		if (depAirport == null || arrAirport == null)
			return "[Error] Inexistent departure and/or arrival airports";

		Airplane plane = new Airplane(planeModel);
		Flight newFlight = new Flight(flightId, plane, depAirport, arrAirport, depTime);
		
		flights.put(newFlight.getId(), newFlight);
		Utilities.writeFlights(flights);
		
		depAirport.addFlight(newFlight);
		Utilities.writeAirports(airports);
		
		return "Flight added successfully";
	}
	
	
	/**
	 * Remove an existing flight provided by the admin
	 * @param flightId of the existing Flight
	 * @param nickname of the administrator
	 * @return a string with the outcome of the operation
	 */
	public synchronized String removeFlight(String flightId, String nickname) {	

		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return "[Error] Adminisitrator not registered";
		
		if (!admin.isLogin())
			return "[Error] Administrator not logged in";
		
		if (!flights.containsKey(flightId))
			return "[Error] Inexistent flight";
		
		Flight removedFlight = flights.remove(flightId);
		Utilities.writeFlights(flights);
		
		Airport depAirport = airports.get(removedFlight.getDepAirport().getCity());
		depAirport.removeFlight(removedFlight);
		Utilities.writeAirports(airports);
		
		return "Flight deleted successfully";
	}
	
	
	/**
	 * Add minutes of delay on an existing flight provided by the admin
	 * @param flightId the ID of the existing Flight
	 * @param minutes amount of time in minutes to be added as delay
	 * @param nickname the nickname of the administrator
	 * @return a string with the outcome of the operation
	 */
	public synchronized String putDelay(String flightId, int minutes, String nickname) {
		
		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return "[Error] Adminisitrator not registered";
		
		if (!admin.isLogin())
			return "[Error] Administrator not logged in";
		
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		f.setDepTime(f.getDepTime().plusMinutes(minutes));
		f.setArrTime(f.getArrTime().plusMinutes(minutes));
		//flights.put(f.getId(), f);
		Utilities.writeFlights(flights);
		
		return "Delay added successfully";
	}
	
	
	/**
	 * Put a discount on the cost of an existing flight provided by the admin
	 * @param flightId of the existing Flight
	 * @param deal percentage to the cost
	 * @param nickname of the administrator
	 * @return a string with the outcome of the operation
	 */
	public synchronized String putDeal(String flightId, double dealPerc, String nickname) {

		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return "[Error] Adminisitrator not registered";
		
		if (!admin.isLogin())
			return "[Error] Administrator not logged in";
		
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (dealPerc <= 0 && dealPerc >= 100)
			return "[Error] Invalid percentage";
		System.out.println(f.getCost());
		f.setCost(f.getCost() - f.getCost()* dealPerc/100);
		//flights.put(f.getId(), f);
		Utilities.writeFlights(flights);
		System.out.println(f.getCost());
		System.out.println(flights.get(flightId).getCost());
		
		return "Discount set successfully";
	}
}
