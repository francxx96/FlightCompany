package flightcompany;

import java.util.Objects;


public class User {
    private String name, surname;
    private String nickname, password;


    public User(String name, String surname, String nickname, String password) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    public String toString() {
        return nickname + " -- Name and surname: " + name + " " + surname;
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

