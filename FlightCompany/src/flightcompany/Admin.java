package flightcompany;

public class Admin extends User {
	private static final long serialVersionUID = 1L;

	public Admin(String name, String surname, String nickname, String password) {
		super(name, surname, nickname, password);
	}

    @Override
    public String toString() {
        return "Administrator: " + super.toString();
    }
}
