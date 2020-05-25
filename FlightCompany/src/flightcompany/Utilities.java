package flightcompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements I/O access methods to the resources managed by the airline
 * @author Emilio, Francesco
 */
public class Utilities {
	public final static String USERS_FILE = "users.txt";
	public final static String FLIGHTS_FILE = "flights.txt";
	public final static String AIRPORTS_FILE = "airports.txt";
	
	/**
	 * Update the file containing the flight map
	 * @param the flight map
	 */
	public static void writeFlights(Map<String, Flight> map) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(FLIGHTS_FILE));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(map);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the file containing the user map
	 * @param the user map
	 */
	public static void writeUsers(Map<String, User> map) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(USERS_FILE));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(map);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the file containing the airport map
	 * @param the airport map
	 */
	public static void writeAirports(Map<AirportCity, Airport> map) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(AIRPORTS_FILE));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(map);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Read the file containing the flight map
	 * @return the flight map
	 */
	public static Map<String, Flight> getFlights() {
		Object obj = readFromFile(FLIGHTS_FILE);
		if(obj == null)
			return new HashMap<String, Flight>();
		else
			return (HashMap<String, Flight>) obj;
	}
	
	/**
	 * Read the file containing the user map
	 * @return the user map
	 */
	public static Map<String, User> getUsers() {
		Object obj = readFromFile(USERS_FILE);
		if(obj == null)
			return new HashMap<String, User>();
		else
			return (HashMap<String, User>) obj;
	}
	
	/**
	 * Read the file containing the airport map
	 * @return the airport map
	 */
	public static Map<AirportCity, Airport> getAirports() {
		Object obj = readFromFile(AIRPORTS_FILE);
		if(obj == null)
			return new HashMap<AirportCity, Airport>();
		else
			return (HashMap<AirportCity, Airport>) obj;
	}
	
	/**
	 * Reads a generic object from the file associated with the given name
	 * @param the file name
	 * @return the object read
	 */
	public static Object readFromFile(String filename) {
		File file = new File(filename); 
		Object data = null;
		
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				data = ois.readObject();
	            ois.close();
	            
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
        }
		
		return data;
	}
	
}
