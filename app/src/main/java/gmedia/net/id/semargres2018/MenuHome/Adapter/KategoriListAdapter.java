package gmedia.net.id.semargres2018.MenuHome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.semargres2018.MenuHome.SystemUtils.NotifPref;
import gmedia.net.id.semargres2018.R;

/**
 * Created by Shin on 3/1/2017.
 */

public class KategoriListAdapter extends RecyclerView.Adapter<KategoriListAdapter.MyViewHolder> {

    private Context context;
    private List<CustomItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private NotifPref np;
    private int menuWidth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llContainer;
        public RelativeLayout cvContainer;
        public ImageView ivIcon;
        public TextView tvTitle;
        public LinearLayout llKatNotif;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (RelativeLayout) view.findViewById(R.id.rl_container);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            llKatNotif = (LinearLayout) view.findViewById(R.id.ll_kat_notif);
            llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        }
    }

    public KategoriListAdapter(Context context, List<CustomItem> masterList, int menuWidth){
        this.context = context;
        this.masterList = masterList;
        this.menuWidth = menuWidth;
        np = new NotifPref(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_katergori, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomItem kategori = masterList.get(position);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , menuWidth);
        holder.llContainer.setLayoutParams(lp);
        holder.tvTitle.setText(kategori.getItem2());
        // loading image using Picasso library
        ImageUtils iu = new ImageUtils();
        iu.LoadCategoryImage(context, kategori.getItem3(), holder.ivIcon);
        HashMap<String, String> lastKat = np.getLastPromo(kategori.getItem1());
        String lastKatString = lastKat.get(NotifPref.TAG_LAST_PROMO);

        if(!lastKatString.trim().equals(kategori.getItem4().trim())){
            holder.llKatNotif.setVisibility(View.VISIBLE);
        }else{
            holder.llKatNotif.setVisibility(View.GONE);
        }

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                np.saveKategori(kategori.getItem1(), kategori.getItem4());

                holder.llKatNotif.setVisibility(View.GONE);
                /*Intent intent = new Intent(context, DetailKategori.class);
                intent.putExtra("id", kategori.getItem1());
                intent.putExtra("title", kategori.getItem2());
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}
