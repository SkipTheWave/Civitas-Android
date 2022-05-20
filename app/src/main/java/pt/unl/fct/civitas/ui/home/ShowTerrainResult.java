package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

import java.util.List;

import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.VertexData;

/**
 * Authentication result : success (user details) or error message.
 */
class ShowTerrainResult {
    @Nullable
    private List<TerrainData> success;
    @Nullable
    private Integer error;

    ShowTerrainResult(@Nullable Integer error) {
        this.error = error;
    }

    ShowTerrainResult(@Nullable List<TerrainData> success) {
        this.success = success;
    }

    @Nullable
    List<TerrainData> getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}