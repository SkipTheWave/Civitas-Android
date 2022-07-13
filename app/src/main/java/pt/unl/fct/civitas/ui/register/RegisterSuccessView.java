package pt.unl.fct.civitas.ui.register;

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