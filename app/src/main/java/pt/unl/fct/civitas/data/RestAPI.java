package pt.unl.fct.civitas.data;

import java.util.List;

import pt.unl.fct.civitas.data.model.LoginData;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.RegisterData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.UsernameData;
import pt.unl.fct.civitas.data.model.VertexData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestAPI {
    @POST("rest/login/v1")
    Call<LoggedInUser> doLogin(@Body LoginData credentials) ;

    @POST("rest/register/v1")
    Call<Void> registerUser(@Body RegisterData data);

    @POST("rest/login/logout")
    Call<Void> doLogout(@Body UsernameData data);

    @POST("rest/login/profile")
    Call<ProfileData> getProfile(@Body UsernameData data);

    @POST("rest/login/update")
    Call<String> editProfile(@Body ProfileData data);

    @POST("rest/terrain/getTerrain")
    Call<List<TerrainInfo>> getTerrains(@Body UsernameData data);

    @POST("rest/terrain/terrainCounter")
    Call<TerrainInfo> getTerrainInfo(@Body UsernameData data);
}
