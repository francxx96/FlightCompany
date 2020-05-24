package flightcompany;

import java.io.Serializable;

/**
 * Implements the airplanes that are involved in the airline's flights
 * @author Emilio, Francesco
 */
public class Airplane implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AirplaneModel model;
	private double speed; // measured in km/min
    private int maxSeats;

    public Airplane(AirplaneModel model) {
        this.model = model;
        
        switch(this.model) {
            case AIRBUS_A320:
                maxSeats = 10;
                speed = 14;
                break;
            case BOEING_737:
                maxSeats = 7;
                speed = 15.4;
                break;
            case EMBRAER:
                maxSeats = 5;
                speed = 14.5;
                break;
        }
    }

    public int getMaxSeats() {
        return maxSeats;
    }
    
    public double getSpeed() {
    	return speed;
    }

    @Override
    public String toString() {
        return "Airplane model: " + model;
    }
}

