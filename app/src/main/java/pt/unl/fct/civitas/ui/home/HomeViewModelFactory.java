package pt.unl.fct.civitas.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.unl.fct.civitas.MainApplication;
import pt.unl.fct.civitas.data.DataSource;
import pt.unl.fct.civitas.data.LoginRepository;
import pt.unl.fct.civitas.ui.login.LoginViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class HomeViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(LoginRepository.getInstance(new DataSource(), MainApplication.getExecutorService()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}