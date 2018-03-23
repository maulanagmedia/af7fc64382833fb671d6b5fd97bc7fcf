package gmedia.net.id.semargres2018.MenuNews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.EndlessScroll;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2018.MenuHome.Adapter.PromoListAdapter;
import gmedia.net.id.semargres2018.MenuNews.Adapter.NewsEventAdapter;
import gmedia.net.id.semargres2018.MenuNews.Adapter.NewsPromoAdapter;
import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.Inisialisasi;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class NavNews extends Fragment {

    private Context context;
    private View layout;
    private Toolbar mToolbar;
    private RecyclerView rvListEvent, rvListPromo;
    private ProgressBar pbLoading;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private List<CustomItem> eventList, moreEventList, promoList, morePromoList;
    private int startEvent = 0, startPromo = 0;
    private int count = 10;
    private String offerImage = "";
    private ImageView ivAdv;
    private NewsEventAdapter eventAdapter;
    private NewsPromoAdapter promoAdapter;
    private boolean isEventLoading = false, isPromoLoading = false;

    public NavNews() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_nav_news, container, false);
        context = getContext();
        initUI();
        return layout;
    }

    private void initUI() {

        mToolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity)context).setSupportActionBar(mToolbar);

        ivAdv = (ImageView) layout.findViewById(R.id.iv_adv);
        rvListEvent = (RecyclerView) layout.findViewById(R.id.rv_list_event);
        rvListPromo = (RecyclerView) layout.findViewById(R.id.rv_list_promo);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);

        session = new SessionManager(context);
        isEventLoading = false;
        isPromoLoading = false;

        getEvent();

        setAdv();
    }

    private void getEvent() {

        isEventLoading = true;

        startEvent = 0;
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(startEvent));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getEvent, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                isEventLoading = false;
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    eventList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonNews = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonNews.length();i++){

                            JSONObject item = jsonNews.getJSONObject(i);

                            eventList.add(new CustomItem(item.getString("id_i"), item.getString("gambar")));

                        }
                    }

                    setEventList(eventList);

                    getPromo();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isEventLoading = false;
            }
        });
    }

    private void getPromo() {

        isPromoLoading = true;
        JSONObject jBody = new JSONObject();
        startPromo = 0;

        try {
            jBody.put("start", String.valueOf(startPromo));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getNewsPromo, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                isPromoLoading = false;
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    promoList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonPromo = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonPromo.length();i++){

                            JSONObject item = jsonPromo.getJSONObject(i);

                            promoList.add(new CustomItem(item.getString("id_i"), item.getString("gambar")));

                        }
                    }

                    setPromoList(promoList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isPromoLoading = false;
            }
        });
    }

    private void setEventList(List<CustomItem> listItem){

        rvListEvent.setAdapter(null);

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
            double menuFloat = 0.28 * (size.y); // seperlima dari tinggi
            menuWidth = (int) menuFloat;

            eventAdapter = new NewsEventAdapter(context, listItem, menuWidth, count);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvListEvent.setLayoutManager(mLayoutManager);
            rvListEvent.setItemAnimator(new DefaultItemAnimator());
            rvListEvent.setAdapter(eventAdapter);

            EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    startEvent += count;
                    getMoreEvent();
                }

            };

            rvListEvent.addOnScrollListener(scrollListener);
        }

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
            double menuFloat = 0.2 * (size.y); // seperlima dari tinggi
            menuWidth = (int) menuFloat;

            NewsPromoAdapter menuAdapter = new NewsPromoAdapter(context, listItem, menuWidth, count);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvListPromo.setLayoutManager(mLayoutManager);
            rvListPromo.setItemAnimator(new DefaultItemAnimator());
            rvListPromo.setAdapter(menuAdapter);

            EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    startPromo += count;
                    getMorePromo();
                }

            };

            rvListPromo.addOnScrollListener(scrollListener);
        }

    }

    private void getMoreEvent() {

        isEventLoading = true;
        startEvent = 0;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(startEvent));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getEvent, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                isEventLoading = false;
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    moreEventList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonNews = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonNews.length();i++){

                            JSONObject item = jsonNews.getJSONObject(i);

                            moreEventList.add(new CustomItem(item.getString("id_i"), item.getString("gambar")));

                        }
                    }

                    if(moreEventList.size()>0){
                        eventAdapter.AddMoreList(moreEventList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isEventLoading = false;
            }
        });
    }

    private void getMorePromo() {

        isPromoLoading = true;
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("start", String.valueOf(startPromo));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getNewsPromo, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                isPromoLoading = false;
                JSONObject responseAPI;
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    morePromoList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonPromo = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonPromo.length();i++){

                            JSONObject item = jsonPromo.getJSONObject(i);

                            morePromoList.add(new CustomItem(item.getString("id_i"), item.getString("gambar")));

                        }
                    }

                    if(morePromoList.size() > 0){

                        promoAdapter.AddMoreData(morePromoList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isPromoLoading = false;
            }
        });
    }

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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

}
