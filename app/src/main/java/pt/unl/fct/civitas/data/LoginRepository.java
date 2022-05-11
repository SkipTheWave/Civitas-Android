package pt.unl.fct.civitas.data;

import java.util.concurrent.Executor;

import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private DataSource dataSource;
    private Executor executor;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(DataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public static LoginRepository getInstance(DataSource dataSource, Executor executor) {
        if (instance == null) {
            instance = new LoginRepository(dataSource,executor);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(LoginRepositoryCallback<Void> callback) {
        // handle login in a separate thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = dataSource.logout(user.getUsername());
                user = null;
                callback.onComplete(result);
            }
        });
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(String username, String password, LoginRepositoryCallback<LoggedInUser> callback) {
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

    public void getProfile(LoginRepositoryCallback<ProfileData> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<ProfileData> result = dataSource.getProfile(user);
                callback.onComplete(result);
            }
        });
    }

}