package gmedia.net.id.semargres2018;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.semargres2018.Utils.ServerURL;

public class DetailPromoActivity extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv =  new ItemValidation();
    private String jenis = ""; //event, promo
    private String id = "";
    private ImageView ivLogo;
    private TextView tvTitle, tvDecl, tvLink;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_promo);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );*/

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDecl = (TextView) findViewById(R.id.tv_desc);
        tvLink = (TextView) findViewById(R.id.tv_link);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        int[] display = iv.getScreenResolution(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(display[0], display[0]);
        ivLogo.setLayoutParams(lp);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            id = bundle.getString("id_promo", "");
            jenis = bundle.getString("kategori", "");

            if(id.length() > 0 && jenis.length() > 0){

                getDetailPromo();
            }
        }
    }

    private void getDetailPromo(){

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getPromo+id, "", "", 0, new ApiVolley.VolleyCallback() {

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
                            String title = item.getString("title");
                            String gambar = item.getString("gambar");
                            final String link = item.getString("link");
                            String keterangan = item.getString("keterangan");

                            tvTitle.setText(title);
                            ImageUtils iu = new ImageUtils();
                            iu.LoadRealImage(context, gambar, ivLogo);
                            tvDecl.setText(keterangan);
                            tvLink.setText(link);

                            if(link.length() > 0){

                                tvLink.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                        context.startActivity(browserIntent);
                                    }
                                });
                            }

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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
