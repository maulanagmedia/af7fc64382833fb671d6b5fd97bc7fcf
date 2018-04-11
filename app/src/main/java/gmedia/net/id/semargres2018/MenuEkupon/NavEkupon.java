package gmedia.net.id.semargres2018.MenuEkupon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.EndlessScroll;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import gmedia.net.id.semargres2018.MenuEkupon.Adapter.KuponListAdapter;
import gmedia.net.id.semargres2018.MenuEkupon.Adapter.MasterKuponAdapter;
import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class NavEkupon extends Fragment {

    private Context context;
    private View layout;
    private TextView tvKupon;
    private RecyclerView rvKupon;
    private ProgressBar pbLoading;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> kuponList, moreList;
    private int startIndex = 0;
    private int count = 10;
    private ListView lvKupon;
    private SortedMap<String, List<CustomItem>> masterKuponList;
    private List<String> merchantList;

    public NavEkupon() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_nav_ekupon, container, false);
        context = getContext();
        initUI();
        return layout;
    }

    private void initUI() {

        tvKupon = (TextView) layout.findViewById(R.id.tv_kupon);
        rvKupon = (RecyclerView) layout.findViewById(R.id.rv_list_kupon);
        lvKupon = (ListView) layout.findViewById(R.id.lv_list_kupon);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);

        startIndex = 0;
        //getKupon();
        getMasterKupon();
    }

    private void getKupon(){

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getKuponList, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    kuponList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            kuponList.add(new CustomItem(item.getString("id"),item.getString("nomor"),item.getString("merchant"),item.getString("kategori")));
                        }

                        tvKupon.setText("Anda memiliki "+ String.valueOf(jsonArray.length())+" e-kupon");
                    }

                    setKuponAdapter(kuponList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void getMasterKupon(){

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getKuponList, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterKuponList = new TreeMap<>();
                    merchantList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        String merchant = "";
                        List<CustomItem> kuponPerMerchant = new ArrayList<>();

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            if(merchant.equals(item.getString("merchant"))){

                                kuponPerMerchant.add(new CustomItem(item.getString("id"),item.getString("nomor"),item.getString("merchant"),item.getString("kategori")));

                            }else{

                                merchant = item.getString("merchant");
                                kuponPerMerchant = new ArrayList<>();

                                merchantList.add(item.getString("merchant"));
                                kuponPerMerchant.add(new CustomItem(item.getString("id"),item.getString("nomor"),item.getString("merchant"),item.getString("kategori")));
                            }

                            if((i+1) < jsonArray.length()){

                                JSONObject item2 = jsonArray.getJSONObject(i+1);

                                if(!item2.getString("merchant").equals(merchant)){ // sudah masuk merchant baru

                                    masterKuponList.put(merchant, kuponPerMerchant);
                                }
                            }else if((i+1) == jsonArray.length()){ // akhir

                                masterKuponList.put(merchant, kuponPerMerchant);
                            }
                        }

                        tvKupon.setText("Anda memiliki "+ String.valueOf(jsonArray.length())+" e-kupon");
                    }

                    /*String merchant = "";
                    List<CustomItem> kuponPerMerchant = new ArrayList<>();

                    for(int i = 0; i < 100;i++){

                        if(merchant.equals("Sangubariku")){

                            kuponPerMerchant.add(new CustomItem(String.valueOf(i),"1000"+ i ,"Sangubariku", "fashion"));

                        }else{

                            merchant = "Sangubariku";
                            kuponPerMerchant = new ArrayList<>();

                            merchantList.add("Sangubariku");
                            kuponPerMerchant.add(new CustomItem(String.valueOf(i),"1000"+ i ,merchant,"fashion"));
                        }

                        if((i+1) < 100){

                            if(!("Sangubariku").equals(merchant)){ // sudah masuk merchant baru

                                masterKuponList.put(merchant, kuponPerMerchant);
                            }
                        }else if((i+1) == 100){ // akhir

                            masterKuponList.put(merchant, kuponPerMerchant);
                        }
                    }

                    merchant = "";
                    kuponPerMerchant = new ArrayList<>();

                    for(int i = 0; i < 100;i++){

                        if(merchant.equals("Sangubariku1")){

                            kuponPerMerchant.add(new CustomItem(String.valueOf(i),"1000"+ i ,"Sangubariku1", "fashion"));

                        }else{

                            merchant = "Sangubariku1";
                            kuponPerMerchant = new ArrayList<>();

                            merchantList.add("Sangubariku1");
                            kuponPerMerchant.add(new CustomItem(String.valueOf(i),"1000"+ i ,merchant,"fashion"));
                        }

                        if((i+1) < 100){

                            if(!("Sangubariku1").equals(merchant)){ // sudah masuk merchant baru

                                masterKuponList.put(merchant, kuponPerMerchant);
                            }
                        }else if((i+1) == 100){ // akhir

                            masterKuponList.put(merchant, kuponPerMerchant);
                        }
                    }

                    tvKupon.setText("Anda memiliki "+ String.valueOf(200)+" e-kupon");*/

                    setMasterKuponAdapter(merchantList, masterKuponList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setMasterKuponAdapter(List listItem, SortedMap<String, List<CustomItem>> masterKupon){

        lvKupon.setAdapter(null);

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
            double menuFloat = (size.x - (iv.dpToPx(context, 32))) / 2;
            menuWidth = (int) menuFloat;
            MasterKuponAdapter adapter = new MasterKuponAdapter((Activity) context, masterKupon, listItem, menuWidth);
            lvKupon.setAdapter(adapter);


        }
    }

    private void setKuponAdapter(List listItem){

        rvKupon.setAdapter(null);

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
            double menuFloat = (size.x - (iv.dpToPx(context, 32))) / 2;
            menuWidth = (int) menuFloat;

            KuponListAdapter menuAdapter = new KuponListAdapter(context, listItem, menuWidth);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
            rvKupon.setLayoutManager(mLayoutManager);
//        rvListMenu.addItemDecoration(new NavMenu.GridSpacingItemDecoration(2, dpToPx(10), true));
            rvKupon.setItemAnimator(new DefaultItemAnimator());
            rvKupon.setAdapter(menuAdapter);

            EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    startIndex += count;
                    //pbLoading.setVisibility(View.VISIBLE);
                    //getMoreData();
                }

            };

            rvKupon.addOnScrollListener(scrollListener);
        }
    }

    private void getMoreData(){

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getKuponList, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    moreList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            moreList.add(new CustomItem(item.getString("id"),item.getString("nomor"),item.getString("merchant"),item.getString("kategori")));
                        }
                    }

                    if(moreList.size() > 0){

                        KuponListAdapter adapter = (KuponListAdapter) rvKupon.getAdapter();
                        adapter.addMoreData(moreList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
            }
        });
    }
}
