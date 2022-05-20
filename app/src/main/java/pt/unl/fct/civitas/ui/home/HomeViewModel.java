package pt.unl.fct.civitas.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.RestRepository;
import pt.unl.fct.civitas.data.RestRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainData;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Void> loginResult = new MutableLiveData<>();
    private MutableLiveData<ShowTerrainResult> showTerrainResult = new MutableLiveData<>();
    private MutableLiveData<ProfileResult> profileResult = new MutableLiveData<>();
    private RestRepository restRepository;

    HomeViewModel(RestRepository restRepository) {
        this.restRepository = restRepository;
    }

    LiveData<ProfileResult> getProfileResult() {
        return profileResult;
    }
    LiveData<ShowTerrainResult> getShowTerrainResult() {return showTerrainResult; }

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
                    showTerrainResult.postValue(new ShowTerrainResult(R.string.error_show_terrains));
                }
            }
        });
    }
}