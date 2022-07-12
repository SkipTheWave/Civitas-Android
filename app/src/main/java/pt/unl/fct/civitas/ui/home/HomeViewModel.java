package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.register.RegisterViewModel.isEmailValid;
import static pt.unl.fct.civitas.ui.register.RegisterViewModel.isNameValid;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.RestRepository;
import pt.unl.fct.civitas.data.RestRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.TerrainIdData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.data.model.shareTerrainInfo;

public class HomeViewModel extends ViewModel {

    public static boolean addTerrainMode = false;
    private TerrainData selectedTerrain;

    private MutableLiveData<Void> loginResult = new MutableLiveData<>();
    private MutableLiveData<ShowTerrainResult> showTerrainResult = new MutableLiveData<>();
    private MutableLiveData<ShowTerrainResult> showAllTerrainResult = new MutableLiveData<>();
    private MutableLiveData<RegisterTerrainResult> registerTerrainResult = new MutableLiveData<>();
    private MutableLiveData<RegisterTerrainResult> updateTerrainResult = new MutableLiveData<>();
    private MutableLiveData<RegisterTerrainResult> shareTerrainResult = new MutableLiveData<>();
    private MutableLiveData<RegisterTerrainResult> registerTerrainEndResult = new MutableLiveData<>();
    private MutableLiveData<ProfileFormState> profileFormState = new MutableLiveData<>();
    private MutableLiveData<TerrainInfoFormState> terrainFormState = new MutableLiveData<>();
    private MutableLiveData<ProfileResult> profileResult = new MutableLiveData<>();
    private MutableLiveData<TerrainData> currentTerrainData = new MutableLiveData<>();
    private RestRepository restRepository;

    HomeViewModel(RestRepository restRepository) {
        selectedTerrain = new TerrainData("", "", "");
        this.restRepository = restRepository;
    }

    LiveData<ProfileResult> getProfileResult() {
        return profileResult;
    }

    LiveData<ShowTerrainResult> getShowTerrainResult() {
        return showTerrainResult;
    }
    LiveData<ShowTerrainResult> getShowAllTerrainResult() { return showAllTerrainResult; }
    LiveData<RegisterTerrainResult> getRegisterTerrainResult() { return registerTerrainResult; }
    LiveData<RegisterTerrainResult> getRegisterTerrainEndResult() { return registerTerrainEndResult; }
    LiveData<RegisterTerrainResult> getShareTerrainResult() { return shareTerrainResult; }
    LiveData<RegisterTerrainResult> getUpdateTerrainResult() { return updateTerrainResult; }

    void setCurrentTerrainData(TerrainData data) {
        currentTerrainData.setValue(data);
    }
    LiveData<TerrainData> getCurrentTerrainData() {
        return currentTerrainData;
    }

    LiveData<TerrainInfoFormState> getTerrainInfoFormState() { return terrainFormState; }

    void addTerrainAux(TerrainData data) {
        setCurrentTerrainData(data);
        addTerrainMode = true;
    }

    public String getUsername() {
        return restRepository.getUsername();
    }
    public TerrainData getSelectedTerrain() { return selectedTerrain; }
    public void setSelectedTerrain(TerrainData terrain) { selectedTerrain = terrain; }

    public void getProfile() {
        restRepository.getProfile(new RestRepositoryCallback<ProfileData>() {
            @Override
            public void onComplete(Result<ProfileData> result) {
                if (result instanceof Result.Success) {
                    ProfileData data = ((Result.Success<ProfileData>) result).getData();
                    ProfileResult auxResult = new ProfileResult(data);
                    profileResult.postValue(auxResult);
                } else {
                    profileResult.postValue(new ProfileResult(R.string.error_show_profile));
                }
            }
        });
    }

    public void editProfile(ProfileData data) {
        restRepository.editProfile(data, new RestRepositoryCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                if (result instanceof Result.Success) {
                    //String response = ((Result.Success<String>) result).getData();
                    profileResult.postValue(new ProfileResult(data));
                } else {
                    profileResult.postValue(new ProfileResult(R.string.error_edit_profile));
                }
            }
        });
    }

    public void profileDataChanged(String name, String email) {
        if (!isNameValid(name)) {
            profileFormState.setValue(new ProfileFormState(R.string.invalid_name, null));
        } else if (!isEmailValid(email)) {
            profileFormState.setValue(new ProfileFormState(null, R.string.invalid_email));
        } else {
            profileFormState.setValue(new ProfileFormState(true));
        }
    }

    public void terrainDataChanged(String article, String section) {
        if (!isNameValid(article)) {
            terrainFormState.setValue(new TerrainInfoFormState(R.string.invalid_article, null));
        } else if (!isNameValid(section)) {
            terrainFormState.setValue(new TerrainInfoFormState(null, R.string.invalid_section));
        } else {
            terrainFormState.setValue(new TerrainInfoFormState(true));
        }
    }

    public void logout() {
        restRepository.logout(new RestRepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                profileResult.postValue(null);
            }
        });
    }

    public void showTerrains() {
        restRepository.getTerrains(new RestRepositoryCallback<List<TerrainData>>() {
            @Override
            public void onComplete(Result<List<TerrainData>> result) {
                if (result instanceof Result.Success) {
                    List<TerrainData> data = ((Result.Success<List<TerrainData>>) result).getData();
                    ShowTerrainResult auxResult = new ShowTerrainResult(data);
                    showTerrainResult.postValue(auxResult);
                } else {
                    showTerrainResult.postValue(new ShowTerrainResult(((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public void registerVertices(List<VertexData> vertices) {
        restRepository.registerVertex(vertices, new RestRepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                if (result instanceof Result.Success) {
                    registerTerrainResult.postValue(new RegisterTerrainResult("Vertex registered", null));
                } else {
                    registerTerrainResult.postValue(new RegisterTerrainResult(null,
                            ((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public void registerTerrain(TerrainData data, List<VertexData> vertices) {
        registerTerrainResult.observeForever(new Observer<RegisterTerrainResult>() {
            @Override
            public void onChanged(RegisterTerrainResult observedResult) {
                if (observedResult.getSuccess() != null) {
                    for (VertexData vertex : vertices) {
                        vertex.terrainId = observedResult.getSuccess();
                    }
                    registerVertices(vertices);
                    // TODO maybe find some way of counting all the successes to know if not a single vertex failed
                    registerTerrainEndResult.setValue(new RegisterTerrainResult(observedResult.getSuccess(), null));
                }
                else
                    registerTerrainEndResult.setValue(new RegisterTerrainResult(null, observedResult.getError()));
            }
        });
        restRepository.registerTerrain(data, new RestRepositoryCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                if (result instanceof Result.Success) {
                    registerTerrainResult.postValue(new RegisterTerrainResult(
                            ((Result.Success<String>) result).getData(), null));
                } else {
                    registerTerrainResult.postValue(new RegisterTerrainResult(null,
                            ((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public void showAllTerrains() {
        restRepository.getAllTerrains(new RestRepositoryCallback<List<TerrainData>>() {
            @Override
            public void onComplete(Result<List<TerrainData>> result) {
                if (result instanceof Result.Success) {
                    List<TerrainData> data = ((Result.Success<List<TerrainData>>) result).getData();
                    ShowTerrainResult auxResult = new ShowTerrainResult(data);
                    showAllTerrainResult.postValue(auxResult);
                } else {
                    showAllTerrainResult.postValue(new ShowTerrainResult(((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public void updateTerrain(TerrainData data) {
        restRepository.updateTerrain(data, new RestRepositoryCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                // TODO might need to be changed to a new result type
                if (result instanceof Result.Success) {
                    updateTerrainResult.postValue(new RegisterTerrainResult(
                            ((Result.Success<String>) result).getData(), null));
                } else {
                    updateTerrainResult.postValue(new RegisterTerrainResult(null,
                            ((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public void shareTerrain(shareTerrainInfo data) {
        restRepository.shareTerrain(data, new RestRepositoryCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                if (result instanceof Result.Success) {
                    shareTerrainResult.postValue(new RegisterTerrainResult(
                            ((Result.Success<String>) result).getData(), null));
                } else {
                    shareTerrainResult.postValue(new RegisterTerrainResult(null,
                            ((Result.Error) result).getError().getMessage()));
                }
            }
        });
    }

    public String[] checkShares(String newShares, String owner) {
        String[] newSharesAux = newShares.split(",");
        for (int i = 0; i < newSharesAux.length; i++) {
            if(newSharesAux[i].equals(owner)) {
                return null;
            }
        }
        return newSharesAux;
    }
}