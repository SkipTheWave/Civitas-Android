package pt.unl.fct.loginapp.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.unl.fct.loginapp.MainApplication;
import pt.unl.fct.loginapp.data.LoginDataSource;
import pt.unl.fct.loginapp.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(LoginRepository.getInstance(new LoginDataSource(), MainApplication.getExecutorService()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}