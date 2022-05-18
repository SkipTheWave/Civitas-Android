package pt.unl.fct.civitas.ui.home;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import pt.unl.fct.civitas.MainApplication;
import pt.unl.fct.civitas.data.DataSource;
import pt.unl.fct.civitas.data.LoginRepository;
import pt.unl.fct.civitas.data.LoginRepositoryCallback;
import pt.unl.fct.civitas.data.Result;
import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.databinding.ActivityHomeBinding;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.ui.login.LoginActivity;
import pt.unl.fct.civitas.ui.login.LoginViewModel;
import pt.unl.fct.civitas.ui.login.LoginViewModelFactory;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView navBar;
    private Gson gson = new Gson();
    private ActivityHomeBinding binding;
    //private LoginRepository repository;
    private HomeViewModel viewModel;
    private ProfileFragment profileFragment;

    private MutableLiveData<Void> loginResult = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //repository = LoginRepository.getInstance(new DataSource(), MainApplication.getExecutorService());
        viewModel = new ViewModelProvider(this, new HomeViewModelFactory())
                .get(HomeViewModel.class);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navBar = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(navBar, navController);

        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.ProfileFragment);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "I'm a snackbar oo ga ga", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        viewModel.getProfileResult().observe(this, new Observer<ProfileResult>() {
            @Override
            public void onChanged(@Nullable ProfileResult profileResult) {
                if( profileResult == null ) {
                    // TODO how to check if logout really was successful? might be better
                    //if( result instanceof Result.Success ) {
                    TokenStore.setToken(getApplicationContext(), null);
                    Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                    Toast.makeText(getApplicationContext(), R.string.sign_out_success, Toast.LENGTH_LONG).show();
                    startActivity(logoutIntent);

                    //Complete and destroy home activity once successful
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }



//    viewModel.getProfileResult().observe(this, new Observer<ProfileResult>() {
//        @Override
//        public void onChanged(@Nullable ProfileResult profileResult) {
//            if (loginResult == null) {
//                return;
//            }
//            if (loginResult.getError() != null) {
//                showLoginFailed(loginResult.getError());
//            }
//            if (loginResult.getSuccess() != null) {
//                updateUiWithUser(loginResult.getSuccess());
//                TokenStore.setToken(getApplicationContext(), gson.toJson(loginResult.getSuccess().getUser()) );
////                    Uri gmmIntent = Uri.parse("geo:0,0?q="+"Nova School of Science and Technology, Quinta da Torre, Portugal");
////                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntent);
////                    mapIntent.setPackage("com.google.android.apps.maps");
////                    if( mapIntent.resolveActivity(getPackageManager()) != null ) {
////                        startActivity(mapIntent);
////                    }
//
//                //Complete and destroy login activity once successful
//                finish();
//            }
//        }
//    });

    public void signOut() {
        viewModel.logout();
    }
}