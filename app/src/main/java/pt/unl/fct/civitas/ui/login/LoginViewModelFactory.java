package pt.unl.fct.civitas.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import pt.unl.fct.civitas.MainApplication;
import pt.unl.fct.civitas.data.DataSource;
import pt.unl.fct.civitas.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new DataSource(), MainApplication.getExecutorService()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}