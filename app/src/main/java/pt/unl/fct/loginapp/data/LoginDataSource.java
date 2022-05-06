package pt.unl.fct.loginapp.data;

import android.content.SharedPreferences;
import android.util.Log;

import pt.unl.fct.loginapp.data.model.LoginData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import pt.unl.fct.loginapp.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final RestAPI service;

    public LoginDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://civitas-348815.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RestAPI.class);
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            Call<LoggedInUser> loginService = service.doLogin(new LoginData(username,password)) ;
            Response<LoggedInUser> loginResponse = loginService.execute();
            if( loginResponse.isSuccessful() ) {
                //Log.d("User", loginResponse.body().getUserId());
                LoggedInUser user = loginResponse.body();
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new Exception("Server result code: " + loginResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}