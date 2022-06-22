package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

import java.util.List;

import pt.unl.fct.civitas.data.model.TerrainInfo;

/**
 * Authentication result : success (user details) or error message.
 */
class ShowTerrainResult {
    @Nullable
    private List<TerrainInfo> success;
    @Nullable
    private String error;

    ShowTerrainResult(@Nullable String error) {
        this.error = error;
    }

    ShowTerrainResult(@Nullable List<TerrainInfo> success) {
        this.success = success;
    }

    @Nullable
    List<TerrainInfo> getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}