package flightcompany;

import java.io.Serializable;
import java.util.Objects;

/**
 * Implements a generic flight company user
 * @author Emilio, Francesco
 */
public abstract class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name, surname, nickname, password;
	private transient boolean login;

    public User(String name, String surname, String nickname, String password) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.password = password;
        this.login = false;
    }
    

	@Override
	public String toString() {
		return " [name=" + name + ", surname=" + surname + ", nickname=" + nickname + ", password=" + password
				+ ", login=" + login + "]";
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
    
    public String getPassword() {
    	return password;
    }
    
    public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public abstract boolean isAdmin();

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

