package flightcompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;


public class Utilities {
	
	public static void writeFlights(Set<Flight> set, String filename) {
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
	
	public static void writeUsers(Set<User> set, String filename) {
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
