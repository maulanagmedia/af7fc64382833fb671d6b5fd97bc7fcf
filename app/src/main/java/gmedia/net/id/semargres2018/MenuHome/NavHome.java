package gmedia.net.id.semargres2018.MenuHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.semargres2018.CustomView.WrapContentViewPager;
import gmedia.net.id.semargres2018.MenuHome.Adapter.HeaderSliderAdapter;
import gmedia.net.id.semargres2018.MenuHome.Adapter.KategoriListAdapter;
import gmedia.net.id.semargres2018.MenuHome.Adapter.PromoListAdapter;
import gmedia.net.id.semargres2018.ProfileActivity;
import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.Inisialisasi;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class NavHome extends Fragment implements ViewPager.OnPageChangeListener {

    private Context context;
    private View layout;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private WrapContentViewPager vpHeaderSlider;
    private LinearLayout llPagerIndicator;
    private List<CustomItem> sliderList;
    private int dotsCount;
    private ImageView[] dots;
    private HeaderSliderAdapter mAdapter;
    private boolean firstLoad = true;
    private int changeHeaderTimes = 6;
    private Timer timer;
    private RecyclerView rvListKategori;
    private List<CustomItem> kategoryList;
    private String offerImage;
    private ImageView ivAdv;
    private RecyclerView rvListPromo;
    private List<CustomItem> promoList;
    private int count = 15;
    private ImageView ivProfile;

    public NavHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_nav_home, container, false);
        context = getContext();
        initUI();

        firstLoad = true;
        return layout;
    }

    private void initUI() {

        // Header
        vpHeaderSlider = (WrapContentViewPager) layout.findViewById(R.id.pager_introduction);
        vpHeaderSlider.setScrollDurationFactor(5);
        llPagerIndicator = (LinearLayout) layout.findViewById(R.id.ll_view_pager_dot_count);
        rvListKategori = (RecyclerView) layout.findViewById(R.id.rv_list_kategori);
        ivAdv = (ImageView) layout.findViewById(R.id.iv_adv);
        rvListPromo = (RecyclerView) layout.findViewById(R.id.rv_list_promo);
        ivProfile = (ImageView) layout.findViewById(R.id.iv_profile);

        session = new SessionManager(context);

        ImageUtils iu = new ImageUtils();
        //iu.LoadProfileImage(context, session.getUserInfo(SessionManager.TAG_PICTURE), ivProfile);

        getKategoriData();

        int[] dimension = iv.getScreenResolution(context);

        int heightLine = (dimension[0] / 3);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vpHeaderSlider.getLayoutParams();
        lp.width = dimension[0];
        lp.height = dimension[0];

        getListHeaderSlider();

        initEvent();
    }

    private void getKategoriData() {

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getKategori, "","", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    kategoryList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            kategoryList.add(new CustomItem(item.getString("id_k"),item.getString("nama"),item.getString("icon"),item.getString("id_m")));
                        }
                    }

                    setKategoriAdapter(kategoryList);

                    setAdv();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setKategoriAdapter(List listItem){

        rvListKategori.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            try {
                // this is why the minimal sdk must be JB
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealSize(size);
                }else {
                    display.getSize(size);
                }
            } catch (NoSuchMethodError err) {
                display.getSize(size);
            }

            int menuWidth = 0;
            double menuFloat = (size.x - iv.dpToPx(context, 32)) / 4;
            menuWidth = (int) menuFloat;

            int jmlBaris = (int)(Math.ceil((double)listItem.size() / 4));

            rvListKategori.setLayoutParams(new LinearLayout.LayoutParams(rvListKategori.getLayoutParams().width, (((size.x - iv.dpToPx(context, 32)) / 4 * jmlBaris))));

            KategoriListAdapter menuAdapter = new KategoriListAdapter(context, listItem, menuWidth);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 4);
            rvListKategori.setLayoutManager(mLayoutManager);
//        rvListMenu.addItemDecoration(new NavMenu.GridSpacingItemDecoration(2, dpToPx(10), true));
            rvListKategori.setItemAnimator(new DefaultItemAnimator());
            rvListKategori.setAdapter(menuAdapter);
        }
    }

    private void initEvent() {

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("is_edit", true);
                ((Activity) context).startActivity(intent);
                ((Activity) context).finish();
            }
        });
    }

    //region Slider Header
    private void getListHeaderSlider() {

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getPromo, "","", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    sliderList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            sliderList.add(new CustomItem(item.getString("id_i"), item.getString("gambar"), item.getString("keterangan"), item.getString("link")));
                        }
                    }

                    if(firstLoad){
                        setViewPagerTimer(changeHeaderTimes);
                        firstLoad = false;
                    }

                    setHeaderSlider();
                    setUiPageViewController();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setHeaderSlider(){

        vpHeaderSlider.setAdapter(null);
        mAdapter = null;
        mAdapter = new HeaderSliderAdapter(context, sliderList);
        vpHeaderSlider.setAdapter(mAdapter);
        vpHeaderSlider.setCurrentItem(0);
        vpHeaderSlider.setOnPageChangeListener(this);
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];
        llPagerIndicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            llPagerIndicator.addView(dots[i], params);
        }

        if(dots.length > 0) dots[0].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    private void setViewPagerTimer(int seconds){
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    private void getListPromo() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kat", "");
            jBody.put("start", "0");
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getMerchantHome, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    promoList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            promoList.add(new CustomItem(item.getString("id_m"), item.getString("foto")));

                        }
                    }

                    setPromoList(promoList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setPromoList(List<CustomItem> listItem){

        rvListPromo.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            try {
                // this is why the minimal sdk must be JB
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealSize(size);
                }else {
                    display.getSize(size);
                }
            } catch (NoSuchMethodError err) {
                display.getSize(size);
            }

            int menuWidth = 0;
            //double menuFloat = 0.145 * (size.y - 60); // 60 from activity_home.xml
            double menuFloat = 0.17 * (size.y); // seperlima dari tinggi
            menuWidth = (int) menuFloat;

            PromoListAdapter menuAdapter = new PromoListAdapter(context, listItem, menuWidth, count);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvListPromo.setLayoutManager(mLayoutManager);
            rvListPromo.setItemAnimator(new DefaultItemAnimator());
            rvListPromo.setAdapter(menuAdapter);
        }

    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {

                    if(vpHeaderSlider.getCurrentItem() == mAdapter.getCount() - 1){
                        vpHeaderSlider.setCurrentItem(0);

                    }else{
                        vpHeaderSlider.setCurrentItem(vpHeaderSlider.getCurrentItem() + 1);
                    }
                }
            });

        }
    }

    //region Adv
    private void setAdv(){

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getIklanHome, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            offerImage = item.getString("icon");

                            final String link = item.getString("link");


                            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            try {
                                // this is why the minimal sdk must be JB
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    display.getRealSize(size);
                                }else {
                                    display.getSize(size);
                                }
                            } catch (NoSuchMethodError err) {
                                display.getSize(size);
                            }

                            int menuWidth = 0;
                            menuWidth = size.x;

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , (int) (menuWidth / Inisialisasi.offerHeight));
                            ivAdv.setLayoutParams(lp);
                            ImageUtils iu = new ImageUtils();
                            iu.LoadAdvImage(context, offerImage, ivAdv);

                            ivAdv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                    context.startActivity(browserIntent);
                                }
                            });

                            break;
                        }
                    }

                    getListPromo();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));
        }

        dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
