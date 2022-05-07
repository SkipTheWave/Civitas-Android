package pt.unl.fct.civitas.data.model;

public class RegisterData {
    public String username;
    public String password;
    public String confirmation;
    public String email;
    public String name;
    public String profile;
    public int telephone;
    public int mobilePhone;
    public int nif;

    public RegisterData() {

    }

    public RegisterData(String username, String password, String confirmation, String email, String name, String profile/*, String telephone, String mobilePhone, String nif*/) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.telephone = 910000000;
        this.mobilePhone = 910000000;
        this.nif = 910000000;


    }
}
