package pt.unl.fct.civitas.data.model;

public class LoginData {
    private String username;
    private String password;

    public LoginData() { }

    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
