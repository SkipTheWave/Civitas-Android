package pt.unl.fct.civitas.data;

import pt.unl.fct.civitas.data.model.LoginData;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.RegisterData;
import pt.unl.fct.civitas.data.model.UsernameData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPI {
    @POST("rest/login/v1")
    Call<LoggedInUser> doLogin(@Body LoginData credentials) ;

    @POST("rest/register/v1")
    Call<Void> registerUser(@Body RegisterData data);

    @POST("rest/login/logout")
    Call<Void> doLogout(@Body UsernameData data);
}
