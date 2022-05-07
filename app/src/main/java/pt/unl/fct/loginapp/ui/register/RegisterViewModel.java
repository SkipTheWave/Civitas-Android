package pt.unl.fct.loginapp.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.unl.fct.loginapp.R;
import pt.unl.fct.loginapp.data.LoginRepository;
import pt.unl.fct.loginapp.data.LoginRepositoryCallback;
import pt.unl.fct.loginapp.data.Result;
import pt.unl.fct.loginapp.data.model.LoggedInUser;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void login(String username, String password) {
        loginRepository.login(username, password, new LoginRepositoryCallback<LoggedInUser>() {
            @Override
            public void onComplete(Result<LoggedInUser> result) {
                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    registerResult.postValue(new RegisterResult(data.getUserId()));
                    //TODO debug display data
                } else {
                    registerResult.postValue(new RegisterResult(R.string.register_failed));
                }
            }
        });

    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new RegisterFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new RegisterFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }
}