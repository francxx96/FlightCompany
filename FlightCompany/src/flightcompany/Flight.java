package flightcompany;

import static java.lang.Math.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class Flight {
    private String id;
    private int cost;
    private Airplane plane;
    private Set<User> passengers;
    
    private Airport depAirport, arrAirport;
    private LocalDateTime depTime, arrTime;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    
    public Flight(String id, Airplane plane, Airport depAirport, Airport arrAirport, LocalDateTime depTime) {
        this.id = id;
        this.plane = plane;
        this.depAirport = depAirport;
        this.arrAirport = arrAirport;
        this.depTime = depTime;
        
        this.passengers = new HashSet<>();
        
        double dist = airportDistance(this.depAirport, this.arrAirport);
        this.cost = (int) (1 * dist);  // 1€ for each kilometer
        this.arrTime = this.depTime.plusMinutes(round(dist)/15); // supposing a speed of 900 km/h = 15 km/min
    }

    public int getAvlSeats() {
        return plane.getMaxSeats() - passengers.size();
    }
    
    public boolean addPassenger(User p) {
        if (getAvlSeats() > 0 && p.getMoney() >= cost)
            if (passengers.add(p)) {
                p.setMoney(p.getMoney()-cost);
                return true;
            }
        
        return false;
    }
    
    public static double airportDistance(Airport departure, Airport arrival) {
        double lat1 = departure.getLatitude();
        double lat2 = arrival.getLatitude();
        double lon1 = departure.getLongitude();
        double lon2 = arrival.getLongitude();
        
        if ((lat1 == lat2) && (lon1 == lon2))
            return 0;
        
        else {
            double theta = lon1 - lon2;
            double dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
            dist = acos(dist);
            dist = toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;   // distance in kilometers
            
            return dist;
        }
    }
    
    @Override
    public String toString() {
        return "Flight ID: " + id + " -- " + plane + "\n"
                + "Available seats: " + getAvlSeats() + " -- Ticket price: €" + cost + "\n"
                + "Departure: " + depAirport + ", at time: " + depTime.format(formatter) + "\n"
                + "Arrival: " + arrAirport + ", at time: " + arrTime.format(formatter);
    }
    
    public String getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Airplane getPlane() {
        return plane;
    }

    public Airport getDepAirport() {
        return depAirport;
    }

    public Airport getArrAirport() {
        return arrAirport;
    }

    public LocalDateTime getDepTime() {
        return depTime;
    }

    public LocalDateTime getArrTime() {
        return arrTime;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null || getClass() != obj.getClass())
            return false;
        
        final Flight other = (Flight) obj;
        return this.id.equals(other.id);
    }
}
