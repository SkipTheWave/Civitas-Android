package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class ProfileFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer nameError;
    private boolean isDataValid;

    ProfileFormState(@Nullable Integer emailError, @Nullable Integer nameError) {
        this.emailError = emailError;
        this.nameError = nameError;
        this.isDataValid = false;
    }

    ProfileFormState(boolean isDataValid) {
        this.emailError = null;
        this.nameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getNameError() { return nameError; }

    boolean isDataValid() {
        return isDataValid;
    }
}