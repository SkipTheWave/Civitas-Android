package pt.unl.fct.civitas.data.model;

public class RegisterData {

    public String username;
    public String password;
    public String confirmation;
    public String email;
    public String name;
    public String profile;
    public String nif;
    public String mobilePhone, telephone;

    public RegisterData() { }

    public RegisterData(String username, String password, String confirmation, String email, String name,
                        /*String nif, String phoneMobile, String phoneHome,*/ String profile) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email = email;
        this.name = name;
//        this.address = address;
//        this.nif = nif;
//        this.phoneMobile = phoneMobile;
//        this.phoneHome = phoneHome;
        this.nif = "UNDEFINED";
        this.mobilePhone = "UNDEFINED";
        this.telephone = "UNDEFINED";
        this.profile = profile;
    }
}
