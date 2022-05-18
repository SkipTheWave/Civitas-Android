package pt.unl.fct.civitas.data;

import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.LoginData;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.RegisterData;
import pt.unl.fct.civitas.data.model.UsernameData;
import pt.unl.fct.civitas.ui.home.HomeActivity;
import pt.unl.fct.civitas.ui.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import pt.unl.fct.civitas.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class DataSource {

    private final RestAPI service;

    public DataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://civitas-348815.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        this.service = retrofit.create(RestAPI.class);
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            Call<LoggedInUser> loginService = service.doLogin(new LoginData(username,password)) ;
            Response<LoggedInUser> loginResponse = loginService.execute();
            if( loginResponse.isSuccessful() ) {
                LoggedInUser user = loginResponse.body();
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new Exception("Server result code: " + loginResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<Void> logout(String username) {
        try {
            Call<Void> logoutService = service.doLogout(new UsernameData(username)) ;
            Response<Void> logoutResponse = logoutService.execute();
            if( logoutResponse.isSuccessful() ) {
                return new Result.Success<>(R.string.sign_out_success);
            } else {
                return new Result.Error(new Exception("Server result code: " + logoutResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }

    public Result<Void> register(String username, String password, String confirmPassword, String email,
                                 String name, String profile, String telephone, String mobilePhone, String nif) {
        try {
            Call<Void> registerService = service.registerUser(new RegisterData(username, password,
                    confirmPassword, email, name, profile, telephone, mobilePhone, nif)) ;
            Response<Void> registerResponse = registerService.execute();
            if( registerResponse.isSuccessful() ) {
                //String responseText = registerResponse.body();
                return new Result.Success<>(R.string.register_success);
            } else {
                return new Result.Error(new Exception("Server result code: " + registerResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

    public Result<ProfileData> getProfile(LoggedInUser user) {
        try {
            Call<ProfileData> profileService = service.getProfile(new UsernameData(user.getUsername())) ;
            Response<ProfileData> profileResponse = profileService.execute();
            if( profileResponse.isSuccessful() ) {
                ProfileData data = profileResponse.body();
                return new Result.Success<>(data);
            } else {
                return new Result.Error(new Exception("Server result code: " + profileResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
}