package pt.unl.fct.civitas.data.model;

public class ActivateUserData {
    private String username;
    private String userToBeActivate;

    public ActivateUserData() { }

    public ActivateUserData(String username, String userToBeActivate) {
        this.userToBeActivate = userToBeActivate;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getUserToBeActivate() { return userToBeActivate; }
}
