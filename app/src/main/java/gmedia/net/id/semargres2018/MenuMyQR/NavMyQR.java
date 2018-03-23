package gmedia.net.id.semargres2018.MenuMyQR;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class NavMyQR extends Fragment {

    public static Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private View layout;
    private ImageView ivQrCode;
    public static boolean isLoaded = false;
    private ProgressBar pbLoading;

    public NavMyQR() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_nav_my_qr, container, false);
        context = getContext();
        session = new SessionManager(context);
        initUI();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        isLoaded = true;
        //showDialog("selamat","0");
    }

    @Override
    public void onPause() {
        super.onPause();

        isLoaded = false;
    }

    //TODO: cara menangkap dialog
    public static void showDialog(final String message, final String jumlah){

        if(isLoaded){

            try {
                if (Looper.myLooper() == null)
                {
                    Looper.prepare();
                }

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                        View viewDialog = inflater.inflate(R.layout.dialog_get_kupon, null);
                        builder.setView(viewDialog);
                        builder.setCancelable(true);

                        final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
                        final TextView tvKupon = (TextView) viewDialog.findViewById(R.id.tv_kupon);
                        tvText1.setText(message);
                        tvKupon.setText(jumlah);

                        final AlertDialog alert = builder.create();
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        alert.show();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initUI() {

        ivQrCode = (ImageView) layout.findViewById(R.id.iv_qr);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);

        getQR();
    }

    //region Slider Header
    private void getQR() {

        pbLoading.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getQrcode, "","", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = responseAPI.getJSONObject("response");
                        String url = jo.getString("url");
                        ImageUtils iu = new ImageUtils();
                        iu.LoadRealImage(context, url, ivQrCode);
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
