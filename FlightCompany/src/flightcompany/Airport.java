package flightcompany;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Airport implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AirportCity city;
    private double longitude;
    private double latitude;
    private Map<String, Flight> flights;

    public Airport(AirportCity city, double longitude, double latitude) {
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        
        this.flights = new HashMap<>();
    }

    public AirportCity getCity() {
        return city;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    
    public Map<String, Flight> getFlights() {
        return flights;
    }
    
    public void addFlight(Flight f) {
        flights.put(f.getId(), f);
    }
    
    public boolean removeFlight(Flight f) {
        return flights.remove(f.getId()) != null;
    }

    @Override
    public String toString() {
        return city.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.city);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Airport other = (Airport) obj;
        return this.city == other.city;
    }
}

