package pt.unl.fct.loginapp.data;

/* Adicionado para devolver o resultado ao LoginViewModel */
// TODO pode ser usado genericamente para login e register? ou copiamos?
public interface LoginRepositoryCallback<T> {
    void onComplete(Result<T> result);
}
