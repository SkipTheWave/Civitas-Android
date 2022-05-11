package pt.unl.fct.civitas.ui.login;

import pt.unl.fct.civitas.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private LoggedInUser user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(LoggedInUser user) {
        this.user = user;
    }

    LoggedInUser getUser() {
        return user;
    }
}