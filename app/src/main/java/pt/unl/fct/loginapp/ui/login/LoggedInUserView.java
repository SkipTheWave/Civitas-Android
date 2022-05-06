package pt.unl.fct.loginapp.ui.login;

import pt.unl.fct.loginapp.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private LoggedInUser user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.user = user;
    }

    LoggedInUser getUser() {
        return user;
    }
}