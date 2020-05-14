package flightcompany;

import java.util.HashSet;
import java.util.Set;


public class Airport {
    private AirportCity city;
    private double longitude;
    private double latitude;
    private Set<Flight> flights;

    public Airport(AirportCity city, double longitude, double latitude) {
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        
        this.flights = new HashSet<>();
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
    
    public void addFlight(Flight f) {
        flights.add(f);
    }
    
    public boolean removeFlight(Flight f) {
        return flights.remove(f);
    }

    @Override
    public String toString() {
        return "Airport city: " + city;
    }
}

