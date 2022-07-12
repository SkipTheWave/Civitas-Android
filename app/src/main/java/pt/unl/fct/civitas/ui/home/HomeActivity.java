package pt.unl.fct.civitas.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.InputStream;
import java.net.URL;

import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.databinding.ActivityHomeBinding;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.databinding.ContentHomeBinding;
import pt.unl.fct.civitas.ui.login.LoginActivity;

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
        binding.toolbar.setTitleTextAppearance(this, R.style.MeriendaOne);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navBar = findViewById(R.id.BottomNavigationView);
        NavigationUI.setupWithNavController(navBar, navController);

        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.ProfileFragment);

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

        viewModel.getProfilePicResult().observe(this, new Observer<ProfilePicResult>() {
            @Override
            public void onChanged(@Nullable ProfilePicResult pfpResult) {
                if(pfpResult == null)
                    return;
                if (pfpResult.getSuccess() != null) {
                    Context c = getApplicationContext();
                    String url = pfpResult.getSuccess();
                    SharedPreferences prefs = c.getSharedPreferences(
                            c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(c.getString(R.string.shared_preferences_pfp), url);
                    editor.apply();

                    //if(!url.isEmpty())
                    Glide.with(HomeActivity.this)
                            .load(url)
                            .placeholder(R.drawable.ic_person)
                            .circleCrop()
                            .into((ImageView) findViewById(R.id.toolbar_image));
                }
            }
        });

        viewModel.getProfilePic();
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void signOut() {
        Context c = getApplicationContext();
        SharedPreferences prefs = c.getSharedPreferences(
                c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        viewModel.logout();
    }

    @Override
    protected void onDestroy() {
        Context c = getApplicationContext();
        SharedPreferences prefs = c.getSharedPreferences(
                c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        super.onDestroy();
    }
}