package pt.unl.fct.civitas.ui.register;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class RegisterResult {
    @Nullable
    private RegisterSuccessView success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable RegisterSuccessView success) {
        this.success = success;
    }

    @Nullable
    RegisterSuccessView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}