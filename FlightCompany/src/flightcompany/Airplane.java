package flightcompany;

import java.io.Serializable;

public class Airplane implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AirplaneModel model;
    private int maxSeats;

    public Airplane(AirplaneModel model) {
        this.model = model;
        
        switch(this.model) {
            case AIRBUS_A320:
                maxSeats = 10;
                break;
            case BOEING_737:
                maxSeats = 7;
                break;
            case EMBRAER:
                maxSeats = 5;
                break;
        }
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    @Override
    public String toString() {
        return "Airplane model: " + model;
    }
}

