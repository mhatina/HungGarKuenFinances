package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mhatina on 25/08/16.
 */
public class AccountManager {
    static final String PREFERENCES_USERNAME = "username";

    static SharedPreferences getAccountManager(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getAccountManager(ctx).edit();
        editor.putString(PREFERENCES_USERNAME, userName);
        editor.apply();
    }

    public static String getUserName(Context ctx) {
        return getAccountManager(ctx).getString(PREFERENCES_USERNAME, "");
    }

    public static void clearSavedAccountInformation(Context ctx) {
        SharedPreferences.Editor editor = getAccountManager(ctx).edit();
        editor.clear();
        editor.apply();
    }
}
