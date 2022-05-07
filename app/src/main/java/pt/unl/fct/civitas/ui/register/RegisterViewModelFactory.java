package pt.unl.fct.civitas.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.unl.fct.civitas.MainApplication;
import pt.unl.fct.civitas.data.DataSource;
import pt.unl.fct.civitas.data.LoginRepository;
import pt.unl.fct.civitas.data.RegisterRepository;

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
            return (T) new RegisterViewModel(RegisterRepository.getInstance(new DataSource(), MainApplication.getExecutorService()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}