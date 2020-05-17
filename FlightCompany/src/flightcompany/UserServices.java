package flightcompany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserServices {
	private Map<String, User> users;
	private Map<String, Flight> flights;
	private Map<AirportCity, Airport> airports;
	
	public UserServices() {
		this.users = Utilities.getUsers();
		this.flights = Utilities.getFlights();
		this.airports = Utilities.getAirports();
	}
	
	public String registrationRequest(String name, String surname, String nickname, String password, boolean admin) {		
    	
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
	
	public String loginRequest(String nickname, String password) {
		
		User usr = users.get(nickname);
    	if (usr == null)
    		return "[Error] User not registered";
    	
    	if (usr.isLogin())
    		return "[Error] User already logged in";
    	
    	if (!usr.getPassword().equals(password))
    		return "[Error] Wrong password";
    	
    	usr.setLogin(true);
		return "User login completed";
    }
	
	public String logoutRequest(String nickname) {
		
		User usr = users.get(nickname);
		if (usr == null)
    		return "[Error] User not registered";
		
		if (!usr.isLogin())
			return "[Error] User not logged in";
		
		usr.setLogin(false);
		return "User logout completed";
	}
	
	public String searchRoutes(AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		return printRoutes(searchRoutes(depAirport, arrAirport, depTime, new ArrayList<Airport>()));
	}
	
	private List<List<Flight>> searchRoutes(Airport depAirp, Airport arrAirp, LocalDateTime depTime, List<Airport> visitedAirports) {
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
	
    private static String printRoutes(List<List<Flight>> routes) {
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
	
	
	public String bookFlight(String flightId, String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
				
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (!cst.bookFlight(f)) 
			return "[Error] Generic error during reservation completion";
		
		if (cst.getMoney() < f.getCost())
			return "[Error] Customer has not enough money for buying ticket";
		
		//users.put(cst.getNickname(), cst);
		//loggedUsers.put(cst.getNickname(), cst);
		//flights.put(f.getId(), f);
		Utilities.writeUsers(users);
		Utilities.writeFlights(flights);
		
		return "Reservation completed";
	}
	

	public String cancelFlight(String flightId, String nickname) {
		
		Customer cst = (Customer) users.get(nickname);
		if (cst == null)
			return "[Error] Customer not registered";
		
		if(!cst.isLogin())
			return "[Error] Customer not logged in";
		
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (!cst.cancelFlight(f)) {
			return "[Error] Generic error during reservation deletion";
		}

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
	

	public String chargeMoney(double amount, String nickname) {
		
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
	 * Admin has the ability to create a new Flight object.
	 * @param nickname the nickname of the administrator
	 * @param flightId the ID of the new Flight
	 * @param planeModel the airplane model of the new Flight
	 * @param depCity the departure airport city of the new Flight
	 * @param arrCity the arrival airport city of the new Flight
	 * @param depTime the departure time of the new Flight
	 * @return true if the creation of the new flight has success, false otherwise
	 */
	public String addFlight(String flightId, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime, String nickname) {

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
		//airports.put(depAirport.getCity(), depAirport);
		Utilities.writeAirports(airports);
		
		return "Flight added successfully";
	}
	
	/**
	 * Admin has the ability to delete an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param nickname the nickname of the administrator
	 * @return true if the deletion of the existing flight has success, false otherwise
	 */
	public String removeFlight(String flightId, String nickname) {	

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
		//airports.put(depAirport.getCity(), depAirport);
		Utilities.writeAirports(airports);
		
		return "Flight deleted successfully";
	}
	
	
	/**
	 * Admin has the ability to add minutes of delay an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param minutes amount of time in minutes to be added as delay
	 * @param nickname the nickname of the administrator
	 * @return true if the addition of delay has success, false otherwise
	 */
	public String putDelay(String flightId, int minutes, String nickname) {
		
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
	 * Admin has the ability to put a discount on the cost of an existing Flight object.
	 * @param flightId the ID of the existing Flight
	 * @param dealPerc percentage of discount to be applied to the cost
	 * @param nickname the nickname of the administrator
	 * @return true if the discount is set correctly, false otherwise
	 */
	public String putDeal(String flightId, double dealPerc, String nickname) {

		Admin admin = (Admin) users.get(nickname);
		if (admin == null)
			return "[Error] Adminisitrator not registered";
		
		if (!admin.isLogin())
			return "[Error] Administrator not logged in";
		
		Flight f = flights.get(flightId);
		if (f == null)
			return "[Error] Inexistent flight";
		
		if (dealPerc <= 0 && dealPerc >= 1)
			return "[Error] Invalid percentage";
		
		f.setCost(f.getCost() * dealPerc);
		//flights.put(f.getId(), f);
		Utilities.writeFlights(flights);
		
		return "Discount set successfully";
	}
}
