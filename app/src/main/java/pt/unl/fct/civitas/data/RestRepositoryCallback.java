package pt.unl.fct.civitas.data;

/* Adicionado para devolver o resultado ao LoginViewModel */
// TODO pode ser usado genericamente para login e register? ou copiamos?
public interface RestRepositoryCallback<T> {
    void onComplete(Result<T> result);
}
