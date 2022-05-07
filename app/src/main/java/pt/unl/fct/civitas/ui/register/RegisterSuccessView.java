package pt.unl.fct.civitas.ui.register;

import pt.unl.fct.civitas.data.model.LoggedInUser;

class RegisterSuccessView {
    private Integer success;
    //... other data fields that may be accessible to the UI

    RegisterSuccessView(Integer success) {
        this.success = success;
    }

    Integer getSuccessMessage() {
        return success;
    }
}