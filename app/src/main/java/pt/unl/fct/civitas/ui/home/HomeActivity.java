package pt.unl.fct.civitas.ui.home;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import pt.unl.fct.civitas.MainApplication;
import pt.unl.fct.civitas.data.DataSource;
import pt.unl.fct.civitas.data.LoginRepository;
import pt.unl.fct.civitas.data.LoginRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.databinding.ActivityHomeBinding;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.ui.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityHomeBinding binding;
    private LoginRepository repository;
    private MutableLiveData<Void> loginResult = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = LoginRepository.getInstance(new DataSource(), MainApplication.getExecutorService());

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "I'm a snackbar oo ga ga", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void signOut() {
        repository.logout(new LoginRepositoryCallback<Void>() {
            @Override
            public void onComplete(Result<Void> result) {
                loginResult.postValue(null);
            }
        });
        // TODO need to verify if logout was successful?
            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
            Toast.makeText(getApplicationContext(), R.string.sign_out_success, Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);

            //Complete and destroy logout activity once successful
            finish();
    }

//    loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
//        @Override
//        public void onChanged(@Nullable LoginResult loginResult) {
//            if (loginResult == null) {
//                return;
//            }
//            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
//            Toast.makeText(getApplicationContext(), R.string.sign_out_success, Toast.LENGTH_LONG).show();
//            startActivity(logoutIntent);
//
//            //Complete and destroy login activity once successful
//            finish();
//            }
//        }
//    });
}