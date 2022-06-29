package pt.unl.fct.civitas.data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.TerrainIdData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.VertexData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class RestRepository {

    private static volatile RestRepository instance;

    private DataSource dataSource;
    private Executor executor;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private RestRepository(DataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public static RestRepository getInstance(DataSource dataSource, Executor executor) {
        if (instance == null) {
            instance = new RestRepository(dataSource,executor);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(RestRepositoryCallback<Void> callback) {
        // handle logout in a separate thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = dataSource.logout(user.getUsername());
                user = null;
                callback.onComplete(result);
            }
        });
    }

    public String getUsername() { return this.user.getUsername(); }
    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public void login(String username, String password, RestRepositoryCallback<LoggedInUser> callback) {
        // handle login in a separate thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<LoggedInUser> result = dataSource.login(username, password);
                if (result instanceof Result.Success) {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                }
                callback.onComplete(result);
            }
        });
    }

    public void getProfile(RestRepositoryCallback<ProfileData> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<ProfileData> result = dataSource.getProfile(user);
                callback.onComplete(result);
            }
        });
    }

    public void editProfile(ProfileData data, RestRepositoryCallback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> result = dataSource.editProfile(data);
                callback.onComplete(result);
            }
        });
    }

    public void getTerrains(RestRepositoryCallback<List<TerrainInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<List<TerrainInfo>> result = dataSource.getTerrains(user);
                callback.onComplete(result);
            }
        });
    }

    public void registerVertex(List<VertexData> data, RestRepositoryCallback<Void> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = new Result.Error(new IOException("Empty vertex list?"));
                for(VertexData vertex : data) {
                    result = dataSource.registerVertex(vertex);
                }
                callback.onComplete(result);
            }
        });
    }

    public void registerTerrain(TerrainData data, RestRepositoryCallback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> result = dataSource.registerTerrain(data);
                callback.onComplete(result);
            }
        });
    }

}