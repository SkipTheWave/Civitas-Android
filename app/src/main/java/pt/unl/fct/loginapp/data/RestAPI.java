package pt.unl.fct.loginapp.data;

import pt.unl.fct.loginapp.data.model.LoginData;
import pt.unl.fct.loginapp.data.model.LoggedInUser;
import pt.unl.fct.loginapp.data.model.RegisterData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPI {
    @POST("rest/login/v1")
    Call<LoggedInUser> doLogin(@Body LoginData credentials) ;

    @POST("rest/register")
    Call<String> registerUser(@Body RegisterData data);
}
