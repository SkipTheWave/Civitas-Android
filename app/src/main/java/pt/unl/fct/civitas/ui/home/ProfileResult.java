package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

import pt.unl.fct.civitas.data.model.ProfileData;

/**
 * Authentication result : success (user details) or error message.
 */
class ProfileResult {
    @Nullable
    private ProfileData success;
    @Nullable
    private Integer error;

    ProfileResult(@Nullable Integer error) {
        this.error = error;
    }

    ProfileResult(@Nullable ProfileData success) {
        this.success = success;
    }

    @Nullable
    ProfileData getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}