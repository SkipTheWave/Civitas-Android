package pt.unl.fct.civitas.ui.home;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.LoginRepository;
import pt.unl.fct.civitas.data.LoginRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.model.ProfileData;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Void> loginResult = new MutableLiveData<>();
    private MutableLiveData<ProfileResult> profileResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    HomeViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<ProfileResult> getProfileResult() {
        return profileResult;
    }

    public void getProfile() {
        loginRepository.getProfile(new LoginRepositoryCallback<ProfileData>() {
            @Override
            public void onComplete(Result<ProfileData> result) {
                if (result instanceof Result.Success) {
                    ProfileData data = ((Result.Success<ProfileData>) result).getData();
                    ProfileResult auxResult = new ProfileResult(data);
                    profileResult.postValue(auxResult);
                } else {
                    profileResult.postValue(new ProfileResult(R.string.login_failed));
                }
            }
        });
    }

    public void logout() {
        loginRepository.logout(new LoginRepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                profileResult.postValue(null);
            }
        });
    }
}