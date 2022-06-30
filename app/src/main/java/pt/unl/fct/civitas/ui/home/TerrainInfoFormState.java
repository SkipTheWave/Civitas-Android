package pt.unl.fct.civitas.ui.home;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class TerrainInfoFormState {
    @Nullable
    private Integer articleError;
    @Nullable
    private Integer sectionError;
    private boolean isDataValid;

    TerrainInfoFormState(@Nullable Integer articleError, @Nullable Integer sectionError) {
        this.articleError = articleError;
        this.sectionError = sectionError;
        this.isDataValid = false;
    }

    TerrainInfoFormState(boolean isDataValid) {
        this.articleError = null;
        this.sectionError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getArticleError() { return articleError; }

    @Nullable
    Integer getSectionError() { return sectionError; }

    boolean isDataValid() {
        return isDataValid;
    }
}