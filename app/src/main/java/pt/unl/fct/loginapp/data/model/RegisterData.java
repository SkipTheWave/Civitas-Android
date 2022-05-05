package pt.unl.fct.loginapp.data.model;

public class RegisterData {

    public String username;
    public String password;
    public String passwordConfirmation;
    public String email;
    public String name;
    public String address;
    public String nif;
    public String phoneMobile, phoneHome;
    public boolean profilePublic;

    public RegisterData() { }

    public RegisterData(String username, String password, String passwordConfirmation, String email, String name,
                        String address, String nif, String phoneMobile, String phoneHome, boolean profilePublic) {
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.email = email;
        this.name = name;
        this.address = address;
        this.nif = nif;
        this.phoneMobile = phoneMobile;
        this.phoneHome = phoneHome;
        this.profilePublic = profilePublic;
    }
}
