package pt.unl.fct.loginapp.ui.register;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class RegisterResult {
    @Nullable
    private String success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable String success) {
        this.success = success;
    }

    @Nullable
    String getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}