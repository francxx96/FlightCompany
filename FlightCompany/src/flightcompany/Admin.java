package flightcompany;

import java.time.LocalDateTime;
import java.util.Map;

public class Admin extends User {
	private static final long serialVersionUID = 1L;

	public Admin(String name, String surname, String nickname, String password) {
		super(name, surname, nickname, password);
	}
	
	public boolean addFlight(String id, AirplaneModel planeModel, AirportCity depCity, AirportCity arrCity, LocalDateTime depTime) {
		Map<String, Flight> flights = Utilities.getFlights();
		
		if (flights.containsKey(id))
			return false;
		
		Map<AirportCity, Airport> airports = Utilities.getAirports();
		
		Airplane plane = new Airplane(planeModel);
		Airport depAirport = airports.get(depCity);
		Airport arrAirport = airports.get(arrCity);
		
		Flight newFlight = new Flight(id, plane, depAirport, arrAirport, depTime);
		flights.put(id, newFlight);
		Utilities.writeFlights(flights);
		
		return true;
	}
	
	public boolean removeFlight(String id) {
		Map<String, Flight> flights = Utilities.getFlights();
		
		if (!flights.containsKey(id))
			return false;
		
		flights.remove(id);
		Utilities.writeFlights(flights);
		
		return true;
	}
	
	public boolean putDelay(String id, int minutes) {
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(id);
		if (f == null)
			return false;
		
		f.setArrTime(f.getArrTime().plusMinutes(minutes));
		Utilities.writeFlights(flights);
		
		return true;
	}
	
	public boolean putDeal(String id, float dealPerc) {
		Map<String, Flight> flights = Utilities.getFlights();
		
		Flight f = flights.get(id);
		if (f == null)
			return false;

		f.setCost((int) (f.getCost() * dealPerc));
		Utilities.writeFlights(flights);
		
		return true;
	}
	
    @Override
    public String toString() {
        return "Administrator: " + super.toString();
    }
}
