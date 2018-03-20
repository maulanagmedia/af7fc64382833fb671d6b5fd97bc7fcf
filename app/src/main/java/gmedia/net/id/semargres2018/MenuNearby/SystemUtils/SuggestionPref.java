package gmedia.net.id.semargres2018.MenuNearby.SystemUtils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Shin on 3/26/2017.
 */

public class SuggestionPref {

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
    public static final String TAG_PREF_NAME = "SUGGESTIONLIST";
    public static final String TAG_SUGGESTION = "SUGGESTION";

    // Constructor
    public SuggestionPref(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(TAG_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveSuggestion(String suggestion){

        editor.putString(TAG_SUGGESTION, suggestion);

        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public String getSuggestion(){

        return pref.getString(TAG_SUGGESTION, "");
    }

    public void clearPref(){
        editor.clear();
        editor.commit();
    }
}
