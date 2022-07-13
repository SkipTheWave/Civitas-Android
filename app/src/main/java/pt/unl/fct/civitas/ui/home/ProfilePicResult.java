package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class ProfilePicResult {
    @Nullable
    private String success;     //URL
    @Nullable
    private Integer error;

    ProfilePicResult(@Nullable Integer error) {
        this.error = error;
    }

    ProfilePicResult(@Nullable String success) {
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