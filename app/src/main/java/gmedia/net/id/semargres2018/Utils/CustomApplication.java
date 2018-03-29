package gmedia.net.id.semargres2018.Utils;

import android.support.multidex.MultiDexApplication;

/**
 * Created by Shinmaul on 3/27/2018.
 */

public class CustomApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Quicksand-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Quicksand-Regular.ttf");
    }
}
