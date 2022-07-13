package pt.unl.fct.civitas.ui.register;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.RegisterRepository;
import pt.unl.fct.civitas.data.RepositoryCallback;
import pt.unl.fct.civitas.data.Result;

public class RegisterViewModel extends ViewModel {

    public static final String UNDEFINED = "UNDEFINED";

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password, String confirmPassword, String email, String name,
                         String telephone, String mobilePhone, String nif) {
        registerRepository.register(username, password, confirmPassword, email, name,
                checkUndefined(telephone), checkUndefined(mobilePhone), nif, new RepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                if (result instanceof Result.Success) {
                    //String data = ((Result.Success<String>) result).getData();
                    registerResult.postValue(new RegisterResult(new RegisterSuccessView(R.string.register_success)));
                } else {
                    registerResult.postValue(new RegisterResult(R.string.register_failed));
                }
            }
        });

    }

    public void registerDataChanged(String username, String password, String confirmPassword, String email, String name, String nif) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null, null, null, null));
        } else if (!passwordsMatch(password, confirmPassword)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.error_different_passwords, null, null, null));
        } else if (!isNameValid(name)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_name, null));
        } else if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_email, null, null));
        } else if (!isNifValid(nif)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, null, R.string.invalid_nif));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    public static String checkUndefined(String data) {
        if (data == null)
            data = UNDEFINED;
        if (data.trim().isEmpty())
            data = UNDEFINED;
        return data;
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

//    public static boolean isEmailValid(String email) {
//        if (email == null) {
//            return false;
//        }
//        if (!email.contains("@")) {
//            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
//        } else {
//            return !email.trim().isEmpty();
//        }
//    }

    public static boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean isNameValid(String name) {
        if (name == null) {
            return false;
        } else {
            return !name.trim().isEmpty();
        }
    }

    private boolean isNifValid(String nif) {
        if(nif == null) {
            return false;
        } else if( !TextUtils.isDigitsOnly(nif) ) {
            return false;
        } else
            return !nif.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }

    private boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}