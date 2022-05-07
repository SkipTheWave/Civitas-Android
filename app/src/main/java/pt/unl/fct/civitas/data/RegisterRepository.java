package pt.unl.fct.civitas.data;

import java.util.concurrent.Executor;

import pt.unl.fct.civitas.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class RegisterRepository {

    private static volatile RegisterRepository instance;

    private DataSource dataSource;
    private Executor executor;

    // private constructor : singleton access
    private RegisterRepository(DataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public static RegisterRepository getInstance(DataSource dataSource, Executor executor) {
        if (instance == null) {
            instance = new RegisterRepository(dataSource,executor);
        }
        return instance;
    }

    public void register(String username, String password, String confirmPassword, String email, String name,
                         String profile, RepositoryCallback<String> callback) {
        // handle login in a separate thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> result = dataSource.register(username, password, confirmPassword, email, name, profile);
                callback.onComplete(result);
            }
        });
    }

}