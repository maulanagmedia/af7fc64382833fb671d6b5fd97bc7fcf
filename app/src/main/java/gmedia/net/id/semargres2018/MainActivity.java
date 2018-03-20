package gmedia.net.id.semargres2018;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.RuntimePermissionsActivity;
import com.maulana.custommodul.SessionManager;

import gmedia.net.id.semargres2018.MenuEkupon.NavEkupon;
import gmedia.net.id.semargres2018.MenuHome.NavHome;
import gmedia.net.id.semargres2018.MenuMyQR.NavMyQR;
import gmedia.net.id.semargres2018.MenuNearby.NavNearby;
import gmedia.net.id.semargres2018.TabBehavior.CustomViewPager;

public class MainActivity extends RuntimePermissionsActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private TabLayout mTabLayout;
    private int[] mTabsIcons = {
            R.drawable.ic_menu_home,
            R.drawable.ic_menu_scan_qr,
            R.drawable.ic_menu_ekupon,
            R.drawable.ic_menu_news,
            R.drawable.ic_menu_diskon};
    public final int PAGE_COUNT = mTabsIcons.length;
    private int lastPosition = 0;
    private static final int REQUEST_PERMISSIONS = 20;
    private final String[] mTabsTitle = {"Home", "My QR", "E-Kupon", "News", "Terdekat"};
    private final String TAG = "Home";

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        session = new SessionManager(this);

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {
                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        // for android > M
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            MainActivity.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.WRITE_SETTINGS,
                            android.Manifest.permission.WAKE_LOCK,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }
        initUI();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void initUI() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is why the minimal sdk must be JB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else{
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int menuWidth = 0;
        menuWidth = size.x / PAGE_COUNT;

        // Setup the viewPager
        final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.vp_container);
        final MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), menuWidth);

        if (viewPager != null) {
            viewPager.setPagingEnabled(false);
            viewPager.setAdapter(pagerAdapter);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        if (mTabLayout != null) {

            mTabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }

            mTabLayout.getTabAt(lastPosition).getCustomView().setSelected(true);
        }

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                getTabView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragment = new NavHome();
        setTitle("Home");
        callFragment(MainActivity.this, fragment);
    }

    private void getTabView(int position){

        switch (position){
            case 0 :
                fragment = new NavHome();
                setTitle("Home");
                break;
            case 1:
                fragment = new NavMyQR();
                setTitle("My QR");
                break;
            case 2:
                fragment = new NavEkupon();
                setTitle("E-Kupon");
                break;
            case 4:
                fragment = new NavNearby();
                setTitle("Diskon Terdekat");
                break;
        }

        if(position > lastPosition){

            callFragment(MainActivity.this, fragment);
        }else if (position < lastPosition ){
            callFragmentBack(MainActivity.this, fragment);
        }

        lastPosition = position;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public int menuWidth;
        private View view;
        private LinearLayout llContainer;
        private ImageView ivIcon;

        public MyPagerAdapter(FragmentManager fm, int menuWidth) {
            super(fm);
            this.menuWidth = menuWidth;
        }

        public View getTabView(final int position) {

            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_bottom_tab, null);

            llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llContainer.getLayoutParams();
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(dpToPx(menuWidth), lp.height);
            llContainer.setLayoutParams(lp2);
            TextView title = (TextView) view.findViewById(R.id.title);
            LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) title.getLayoutParams();
            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(dpToPx(menuWidth/2), lp3.height);
            title.setLayoutParams(lp4);
            title.setText(mTabsTitle[position]);
            title.setGravity(Gravity.CENTER);
            ivIcon = (ImageView) view.findViewById(R.id.icon);
            ivIcon.setTag(mTabsIcons[position]);
            ivIcon.setImageResource(mTabsIcons[position]);

            return view;
        }

        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }

        @Override
        public Fragment getItem(int pos) {

            switch (pos) {
                case 0:
//                    return PageFragment.newInstance(1,R.layout.fragment_home);
                case 1:
//                    return PageFragment.newInstance(2,R.layout.fragment_kategori);
                case 2:
//                    return PageFragment.newInstance(4, R.layout.fragment_gallery);
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

    @Override
    public void onBackPressed() {
        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, timerClose);
    }

    private static Fragment fragment;
    private static void callFragment(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    private static void callFragmentBack(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}
