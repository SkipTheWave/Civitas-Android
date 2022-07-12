package pt.unl.fct.civitas.data;

import okhttp3.OkHttpClient;
import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.ActivateUserData;
import pt.unl.fct.civitas.data.model.LoginData;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.RegisterData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.TerrainIdData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.UsernameData;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.data.model.shareTerrainInfo;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import pt.unl.fct.civitas.data.model.LoggedInUser;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class DataSource {

    private final RestAPI service;

    public DataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://civitasadc.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        this.service = retrofit.create(RestAPI.class);
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            Call<LoggedInUser> loginService = service.doLogin(new LoginData(username, password));
            Response<LoggedInUser> loginResponse = loginService.execute();
            if( loginResponse.isSuccessful() ) {
                return new Result.Success<>(loginResponse.body());
            } else {
                return new Result.Error(new Exception("Server result code: " + loginResponse.code()));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IO error", e));
        }
    }

    public Result<String> getProfilePic(String username) {
        try {
            Call<String[]> picService = service.getProfilePic(new UsernameData(username));
            Response<String[]> response = picService.execute();
            if( response.isSuccessful() && response.body() != null) {
                return new Result.Success<>(response.body()[1]);
            } else {
                return new Result.Error(new Exception("Server result code: " + response.code()));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IO error", e));
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
            return new Result.Error(new IOException("IO error", e));
        }

    }

    public Result<Void> register(String username, String password, String confirmPassword, String email,
                                 String name, String telephone, String mobilePhone, String nif) {
        try {
            Call<Void> registerService = service.registerUser(new RegisterData(username, password,
                    confirmPassword, email, name, telephone, mobilePhone, nif)) ;
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
            Call<ProfileData> profileService = service.getProfile(new ActivateUserData(user.getUsername(), user.getUsername())) ;
            Response<ProfileData> profileResponse = profileService.execute();
            if( profileResponse.isSuccessful() ) {
                ProfileData data = profileResponse.body();
                return new Result.Success<>(data);
            } else {
                return new Result.Error(new Exception("Server result code: " + profileResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IO error", e));
        }
    }

    public Result<String> editProfile(ProfileData data) {
        try {
            Call<String> profileService = service.editProfile(data) ;
            Response<String> profileResponse = profileService.execute();
            if( profileResponse.isSuccessful() ) {
                String responseText = profileResponse.body();
                return new Result.Success<>(responseText);
            } else {
                return new Result.Error(new Exception("Server result code: " + profileResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IO error", e));
        }
    }

    public Result<List<TerrainData>> getTerrains(LoggedInUser user) {
        try {
            Call<List<TerrainData>> terrainService = service.getTerrains(new UsernameData(user.getUsername())) ;
            Response<List<TerrainData>> terrainResponse = terrainService.execute();
            if( terrainResponse.isSuccessful() ) {
                List<TerrainData> terrains = terrainResponse.body();
                return new Result.Success<>(terrains);
            } else {
                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }

    public Result<Void> registerVertex(VertexData data) {
        try {
            Call<Void> vertexService = service.registerVertex(data) ;
            Response<Void> vertexResponse = vertexService.execute();
            if( vertexResponse.isSuccessful() ) {
                return new Result.Success<>(null);
            } else {
                return new Result.Error(new Exception("Server result code: " + vertexResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }

    public Result<String> registerTerrain(TerrainData data) {
        try {
            Call<String> terrainService = service.registerTerrain(data) ;
            Response<String> terrainResponse = terrainService.execute();
            if( terrainResponse.isSuccessful() ) {
                String terrainId = terrainResponse.body();
                return new Result.Success<>(terrainId);
            } else {
                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }

    public Result<List<TerrainData>> getAllTerrains() {
        try {
            Call<List<TerrainData>> terrainService = service.getAllTerrains();
            Response<List<TerrainData>> terrainResponse = terrainService.execute();
            if( terrainResponse.isSuccessful() ) {
                List<TerrainData> terrains = terrainResponse.body();
                return new Result.Success<>(terrains);
            } else {
                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }

    public Result<String> updateTerrain(TerrainData data) {
        try {
            Call<String> terrainService = service.updateTerrain(data) ;
            Response<String> terrainResponse = terrainService.execute();
            if( terrainResponse.isSuccessful() ) {
                String terrainId = terrainResponse.body();
                return new Result.Success<>(terrainId);
            } else {
                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }

    public Result<String> shareTerrain(shareTerrainInfo data) {
        try {
            Call<String> terrainService = service.shareTerrain(data) ;
            Response<String> terrainResponse = terrainService.execute();
            if( terrainResponse.isSuccessful() ) {
                String owners = terrainResponse.body();
                return new Result.Success<>(owners);
            } else {
                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("IOException moment", e));
        }
    }


//    public Result<List<TerrainData>> getTerrainInfo(LoggedInUser user) {
//        try {
//            Call<List<List<VertexData>>> terrainService = service.getTerrains(new UsernameData(user.getUsername())) ;
//            Response<List<List<VertexData>>> terrainResponse = terrainService.execute();
//            if( terrainResponse.isSuccessful() ) {
//                List<List<VertexData>> data = terrainResponse.body();
//                List<TerrainData> terrains = new LinkedList<>();
//                for(List<VertexData> vertexGroup : data) {
//                    String terrainId = vertexGroup.get(0).terrainId;
//                    terrains.add(new TerrainInfo(terrainId, vertexGroup));
//                }
//                return new Result.Success<>(terrains);
//            } else {
//                return new Result.Error(new Exception("Server result code: " + terrainResponse.code() ));
//            }
//        } catch (Exception e) {
//            return new Result.Error(new IOException("IO error", e));
//        }
//    }
}