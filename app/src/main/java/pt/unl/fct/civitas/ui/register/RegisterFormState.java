package pt.unl.fct.civitas.ui.register;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class RegisterFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer diffPasswordError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer nifError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer diffPasswordError,
                      @Nullable Integer emailError, @Nullable Integer nameError, @Nullable Integer nifError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.diffPasswordError = diffPasswordError;
        this.emailError = emailError;
        this.nameError = nameError;
        this.nifError = nifError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.diffPasswordError = null;
        this.emailError = null;
        this.nameError = null;
        this.nifError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getDiffPasswordError() {
        return diffPasswordError;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getNameError() { return nameError; }

    @Nullable
    Integer getNifError() { return nifError; }

    boolean isDataValid() {
        return isDataValid;
    }
}