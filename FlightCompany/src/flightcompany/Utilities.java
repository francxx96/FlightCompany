package flightcompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;


public class Utilities {
	
	public static void writeObject(Set<Flight> set, String filename) {
		try {
			FileOutputStream f = new FileOutputStream(new File(filename));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			o.writeObject(set);

			o.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object readFromFile(String filename) {
		Object data = new Object();
		
		try {
			FileInputStream fi = new FileInputStream(new File(filename));
			ObjectInputStream oi = new ObjectInputStream(fi);
			
			data = oi.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return data;
	}
}
