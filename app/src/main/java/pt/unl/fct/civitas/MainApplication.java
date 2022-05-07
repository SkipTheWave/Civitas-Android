package pt.unl.fct.civitas;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* Classe com o estado global da aplicação para termos uma pool de threads para fazer chamadas à Web */
/* O nome da classe tem de ser colocado no manifesto no atributo android:name do elemento application */
public class MainApplication extends Application {
    static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static ExecutorService getExecutorService() {
        return executorService;
    }

}
