package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.register.RegisterViewModel.isEmailValid;
import static pt.unl.fct.civitas.ui.register.RegisterViewModel.isNameValid;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.RestRepository;
import pt.unl.fct.civitas.data.RestRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainInfo;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Void> loginResult = new MutableLiveData<>();
    private MutableLiveData<ShowTerrainResult> showTerrainResult = new MutableLiveData<>();
    private MutableLiveData<ProfileFormState> profileFormState = new MutableLiveData<>();
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

    public void logout() {
        restRepository.logout(new RestRepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                profileResult.postValue(null);
            }
        });
    }

    public void showTerrains() {
        restRepository.getTerrains(new RestRepositoryCallback<List<TerrainInfo>>() {
            @Override
            public void onComplete(Result<List<TerrainInfo>> result) {
                if (result instanceof Result.Success) {
                    List<TerrainInfo> data = ((Result.Success<List<TerrainInfo>>) result).getData();
                    ShowTerrainResult auxResult = new ShowTerrainResult(data);
                    showTerrainResult.postValue(auxResult);
                } else {
                    showTerrainResult.postValue(new ShowTerrainResult( ((Result.Error)result).getError().getMessage() ));
                }
            }
        });
    }
}