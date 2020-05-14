package flightcompany;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class User {
    private String name, surname;
    private String nickname, password;
    private double money;
    private Set<Flight> bookedFlights;

    public User(String name, String surname, String nickname, String password) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.password = password;
        
        this.money = 0;
        this.bookedFlights = new HashSet<>();
    }

    public boolean bookFlight(Flight f) {
        if (money >= f.getCost()) {
            money -= f.getCost();
            bookedFlights.add(f);
            return true;
        }
        
        return false;
    }
    
    public boolean chargeMoney(float amount) {
        if (amount > 0) {
            money += amount;
            return true;
        }
        
        return false;
    }
        
    @Override
    public String toString() {
        return "User: " + nickname + " -- Name and surname: " + name + " " + surname;
    }
    
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getNickname() {
        return nickname;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.nickname);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null || getClass() != obj.getClass())
            return false;

        final User other = (User) obj;
        return this.nickname.equals(other.nickname);
    }
}

