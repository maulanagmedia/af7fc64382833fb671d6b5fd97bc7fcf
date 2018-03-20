package gmedia.net.id.semargres2018.MenuHome.SystemUtils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Shin on 3/26/2017.
 */

public class NotifPref {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name

    // All Shared Preferences Keys
    public static final String TAG_PREF_NAME = "KATEGORI";
    public static final String TAG_LAST_PROMO = "LAST_PROMO";

    // Constructor
    public NotifPref(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(TAG_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveKategori(String idKat, String lastPromo){

        String promo = TAG_LAST_PROMO + idKat;

        editor.putString(promo, lastPromo);

        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getLastPromo(String idKat){

        String promo = TAG_LAST_PROMO + idKat;

        HashMap<String, String> lastPromo = new HashMap<String, String>();

        lastPromo.put(TAG_LAST_PROMO, pref.getString(promo, ""));

        return lastPromo;
    }

    public void clearPref(){
        editor.clear();
        editor.commit();
    }
}
