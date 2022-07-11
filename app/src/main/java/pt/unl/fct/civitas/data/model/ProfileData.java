package pt.unl.fct.civitas.data.model;


public class ProfileData {

    public String username;
    public String changedUser;
    public String email;
    public String name;
    public String profile;
    public String telephone;
    public String mobilePhone;
    public String nif;
    public String role;
    public String state;
    public String profilePic;

    public ProfileData() {

    }

    public ProfileData(String username, String changedUser, String email, String name, String telephone,
                       String mobilePhone, String nif, String role, String state, String profilePic) {
        this.username = username;
        this.changedUser = changedUser;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.telephone = telephone;
        this.mobilePhone = mobilePhone;
        this.nif = nif;
        this.role = role;
        this.state = state;
        this.profilePic = profilePic;
    }

}
