package pt.unl.fct.loginapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import pt.unl.fct.loginapp.R;

public class TokenStore {

    public static String getToken(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        return prefs.getString(c.getString(R.string.shared_preferences_token), "");
    }

    public static void setToken(Context c, String token) {
        SharedPreferences prefs = c.getSharedPreferences(c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(c.getString(R.string.shared_preferences_token), token);
        editor.apply();
    }

}
