package gmedia.net.id.semargres2018.MenuEkupon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmedia.net.id.semargres2018.R;

/**
 * Created by Shin on 3/1/2017.
 */

public class KuponListAdapter extends RecyclerView.Adapter<KuponListAdapter.MyViewHolder> {

    private Context context;
    private List<CustomItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;
    private boolean isLiked = false;
    private SessionManager user;
    private Uri uri;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNomor;
        public RelativeLayout rvContainer;
        public ImageView ivKupon;

        public MyViewHolder(View view) {
            super(view);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            tvNomor = (TextView) view.findViewById(R.id.tv_nomor);
            rvContainer = (RelativeLayout) view.findViewById(R.id.rv_container);
            ivKupon = (ImageView) view.findViewById(R.id.iv_kupon);
        }
    }

    public KuponListAdapter(Context context, List<CustomItem> masterList, int menuWidth){
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        this.context = context;
        this.masterList = masterList;
        this.menuWidth = menuWidth;
        user = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_kupon, parent, false);

        return new MyViewHolder(itemView);
    }

    public void addMoreData(List list){

        this.masterList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomItem list = masterList.get(position);

        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , holder.rvContainer.getLayoutParams().height);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth - iv.dpToPx(context, 6) , menuWidth + iv.dpToPx(context, 12));
        lp.setMargins(iv.dpToPx(context, 2), 0, iv.dpToPx(context, 2), 0);
        holder.rvContainer.setLayoutParams(lp);
        holder.tvNomor.setText(""+list.getItem2());
        ImageUtils iu = new ImageUtils();

        iu.LoadRealImage(context, R.drawable.ic_kupon, holder.ivKupon);
        // loading image using Picasso library

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}
