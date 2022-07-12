package pt.unl.fct.civitas.data;

import java.util.List;

import pt.unl.fct.civitas.data.model.ActivateUserData;
import pt.unl.fct.civitas.data.model.LoginData;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.RegisterData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.TerrainIdData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.UsernameData;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.data.model.shareTerrainInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestAPI {
    @POST("rest/login/v2")
    Call<LoggedInUser> doLogin(@Body LoginData credentials) ;

    @POST("rest/login/getNamePic")
    Call<String[]> getProfilePic(@Body UsernameData data) ;

    @POST("rest/register/registerAndroid")
    Call<Void> registerUser(@Body RegisterData data);

    @POST("rest/login/logout")
    Call<Void> doLogout(@Body UsernameData data);

    @POST("rest/login/profile")
    Call<ProfileData> getProfile(@Body ActivateUserData data);

    @POST("rest/login/update")
    Call<String> editProfile(@Body ProfileData data);

    @POST("rest/terrain/getTerrain")
    Call<List<TerrainData>> getTerrains(@Body UsernameData data);

    @POST("rest/terrain/vertex")
    Call<Void> registerVertex(@Body VertexData data);

    @POST("rest/terrain/register")
    Call<String> registerTerrain(@Body TerrainData data);

    @POST("rest/terrain/getAllTerrain")
    Call<List<TerrainData>> getAllTerrains();

    // returns terrainID
    @POST("rest/terrain/updateTerrain")
    Call<String> updateTerrain(@Body TerrainData data);

    // returns owners ("user1,user2")
    @POST("rest/terrain/share")
    Call<String> shareTerrain(@Body shareTerrainInfo data);
}
