package pt.unl.fct.civitas.data.model;

public class ProfileData {
    public String username;
    public String password;
    public String confirmation;
    public String email;
    public String name;
    public String profile;
    public String telephone;
    public String mobilePhone;
    public String nif;

    public ProfileData() {

    }

    public ProfileData(String username, String email, String name,
                        String profile, String telephone, String mobilePhone, String nif) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.telephone = telephone;
        this.mobilePhone = mobilePhone;
        this.nif = nif;


    }
}