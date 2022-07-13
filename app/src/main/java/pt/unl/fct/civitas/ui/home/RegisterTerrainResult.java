package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

class RegisterTerrainResult {
    @Nullable
    private String success;
    @Nullable
    private String error;

    RegisterTerrainResult(@Nullable String success, @Nullable String error) {
        this.success = success;
        this.error = error;
    }

    @Nullable
    String getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}