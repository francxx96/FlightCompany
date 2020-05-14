package flightcompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class Utilities {
	public final static String USERS_FILE = "users.txt";
	public final static String FLIGHTS_FILE = "flights.txt";
	public final static String AIRPORTS_FILE = "airports.txt";
	
	
	public static void writeFlights(Map<String, Flight> set, String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(set);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeUsers(Map<String, User> set, String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(set);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public static void writeAirports(Map<AirportCity, Airport> set, String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(set);

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	
	
	public static Map<AirportCity, Airport> getAirports() {
		return (HashMap<AirportCity, Airport>) readFromFile(AIRPORTS_FILE);
	}
	public static Map<String, Flight> getFlights() {
		return (HashMap<String, Flight>) readFromFile(FLIGHTS_FILE);
	}
	
	public static Map<String, User> getUsers() {
		return (HashMap<String, User>) readFromFile(USERS_FILE);
	}
	
	public static Object readFromFile(String filename) {
		Object data = new Object();
		
		try {
			FileInputStream fis = new FileInputStream(new File(filename));
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			data = ois.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return data;
	}
}
