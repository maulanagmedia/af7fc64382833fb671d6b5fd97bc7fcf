package gmedia.net.id.semargres2018;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2018.Adapter.ListMerchantPromoAdapter;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class DetailMerchant extends AppCompatActivity {

    private Context context;
    private String title;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvToolbarTitle;
    private AppBarLayout appBarLayout;
    private Toolbar mToolbar;
    private ImageView ivProfile;
    private String idMerchant = "";
    private TextView tvTitle, tvAddress, tvTelepon, tvWaktu;
    private ImageView ivFacebook, ivInstagram, ivMap, ivShare;
    private ProgressBar pbLoading;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> listPromo;
    private String namaMerchant;
    private String latitudeString = "0", longitudeString = "0";
    private ListView lvListPromo;
    private LinearLayout ll1;
    private RelativeLayout rv2;
    private NestedScrollView nsContainer;
    private String linkFacebook = "", linkInstagram = "";
    private String fotoLink = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_merchant);

        context = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back_white));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        tvToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivFacebook = (ImageView) findViewById(R.id.iv_facebook);
        ivInstagram = (ImageView) findViewById(R.id.iv_instagram);
        ivMap = (ImageView) findViewById(R.id.iv_map);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvTelepon = (TextView) findViewById(R.id.tv_telephon);
        tvWaktu = (TextView) findViewById(R.id.tv_waktu);
        lvListPromo = (ListView) findViewById(R.id.lv_list_promo);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        initCollapsingToolbar();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idMerchant = bundle.getString("id", "");

            if(idMerchant.length() > 0){

                getDetailMerchant();
            }
        }
    }

    private void getDetailMerchant(){

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getMerchant+idMerchant, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            namaMerchant = item.getString("nama");
                            latitudeString = item.getString("latitude");
                            longitudeString = item.getString("longitude");
                            String alamat = item.getString("alamat");
                            fotoLink = item.getString("foto");
                            String telephone = (item.getString("handphone").length() > 0) ? item.getString("handphone") : item.getString("notelp");
                            String waktu = item.getString("jam_buka");
                            linkFacebook = item.getString("link_fb");
                            linkInstagram = item.getString("link_ig");

                            tvTitle.setText(namaMerchant);
                            tvAddress.setText(alamat);
                            tvTelepon.setText(telephone);
                            tvWaktu.setText(waktu);

                            ImageUtils iu = new ImageUtils();
                            iu.LoadRealImage(context, fotoLink, ivProfile);

                            listPromo = new ArrayList<>();
                            JSONArray jsonPromo = item.getJSONArray("promo");

                            for(int j = 0; j < jsonPromo.length();j++){

                                JSONObject jo = jsonPromo.getJSONObject(j);
                                listPromo.add(new CustomItem(jo.getString("gambar"), jo.getString("title"), jo.getString("keterangan"), jo.getString("link"), jo.getString("id_i")));
                            }

                            lvListPromo.setAdapter(null);
                            if(listPromo.size() > 0){

                                final ListMerchantPromoAdapter adapter = new ListMerchantPromoAdapter((Activity) context, listPromo, 1);
                                lvListPromo.setAdapter(adapter);
                                lvListPromo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CustomItem selected = (CustomItem) adapterView.getItemAtPosition(i);
                                        Intent intent = new Intent(context, DetailPromoActivity.class);
                                        intent.putExtra("id_promo", selected.getItem5());
                                        intent.putExtra("kategori", "promo");
                                        context.startActivity(intent);
                                    }
                                });
                            }

                            initEvent();

                            initTampilan();
                            break;
                        }
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

    private void initEvent() {

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap icon = ((BitmapDrawable) ivProfile.getDrawable()).getBitmap();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String sAux = namaMerchant +", More info:\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=gmedia.net.id.semargres2018" + "\n";
                share.putExtra(Intent.EXTRA_TEXT, sAux);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "semaranggreatsale.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                redirrectToLink(linkFacebook);
            }
        });

        ivInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirrectToLink(linkInstagram);
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callGoogleMap(latitudeString, longitudeString);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fotoLink.length() > 0){
                    new DownloadFileFromURL().execute(fotoLink);
                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(fotoLink), "image*//**//**//**//**//**//**//**//*");
                    context.startActivity(intent);*/
                }
            }
        });
    }

    private void redirrectToLink(String link){

        if(link.length() > 0){
            try{

                if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                    link = "http://" + link;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initTampilan() {

        nsContainer = (NestedScrollView) findViewById(R.id.ns_container);
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        rv2 = (RelativeLayout) findViewById(R.id.rv_2);

        int[] display = iv.getScreenResolution(context);

        // Calculate ActionBar height

        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        CoordinatorLayout.LayoutParams lpc = (CoordinatorLayout.LayoutParams) nsContainer.getLayoutParams();
        //lpc.height = nsContainer.getHeight() - actionBarHeight ;
        //lpc.setMargins(0, -1 * actionBarHeight,);
        //nsContainer.setLayoutParams(lpc);

        int line1Height = ll1.getHeight();
        int lastViewHeight = rv2.getHeight();

        int sisa = nsContainer.getHeight() - (line1Height + lastViewHeight + iv.dpToPx(context, 2) + actionBarHeight);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lvListPromo.getLayoutParams();
        lp.height = sisa;
        //lvListPromo.setLayoutParams(lp);
    }

    private void callGoogleMap(String latitude, String longitude){

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + ","+longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(DetailMerchant.this, "Cannot find google map, Please install latest google map.",
                    Toast.LENGTH_LONG).show();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
            startActivity(browserIntent);
        }
    }

    private void initCollapsingToolbar() {

        title = getResources().getString(R.string.app_name);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.ctl_main);
        appBarLayout = (AppBarLayout) findViewById(R.id.abl_main);
        appBarLayout.setExpanded(true);

        // hiding & showing the tvTitle when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                    collapsingToolbar.setTitle(namaMerchant);
                    tvToolbarTitle.setText(namaMerchant);
                }
                else
                {
                    //Expanded
                    collapsingToolbar.setTitle("");
                    tvToolbarTitle.setText("");
                }
            }
        });
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "downloadedfile.jpg");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
            // setting downloaded into image view
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(imagePath), "image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
