package gmedia.net.id.semargres2018.MenuNearby.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.semargres2018.CustomView.LeftRoundedCornersBitmap;
import gmedia.net.id.semargres2018.DetailMerchant;
import gmedia.net.id.semargres2018.R;
import gmedia.net.id.semargres2018.Utils.Inisialisasi;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListMerchantAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;

    public ListMerchantAdapter(Activity context, List<CustomItem> items, int menuWidth) {
        super(context, R.layout.adapter_list_merchant, items);
        this.context = context;
        this.items = items;
        this.menuWidth = menuWidth;
    }

    private static class ViewHolder {
        public CardView cvContainer;
        public ImageView ivAdv;

        public ImageView ivIcon;
        public TextView tvTitle, tvSubtitle, tvDesc, tvJarak;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        final CustomItem item = items.get(position);
        String title = item.getItem1();
        // 0. offer
        // 1. merchant
        if(title.equals("0")){
            hasil = 0;
        }else{
            hasil = 1;
        }

        return hasil;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        int tipeViewList = getItemViewType(position);

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            if(tipeViewList == 0){
                convertView = inflater.inflate(R.layout.adapter_list_offer, null);
                holder.ivAdv = (ImageView) convertView.findViewById(R.id.iv_adv);
                holder.cvContainer = (CardView) convertView.findViewById(R.id.cv_container);
            }else{
                convertView = inflater.inflate(R.layout.adapter_list_merchant, null);
                holder.cvContainer = (CardView) convertView.findViewById(R.id.cv_container);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvSubtitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
                holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.tvJarak = (TextView) convertView.findViewById(R.id.tv_jarak);
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(tipeViewList == 0){ // iklan
            final CustomItem itemSelected = items.get(position);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , (int) (menuWidth / Inisialisasi.offerHeight));
            lp.setMargins(0, iv.dpToPx(context, 6), 0, iv.dpToPx(context, 6));
            holder.cvContainer.setLayoutParams(lp);
            ImageUtils iu = new ImageUtils();
            iu.LoadAdvImage(context, itemSelected.getItem2(), holder.ivAdv);
            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(itemSelected.getItem3().length() > 0){

                        String link = itemSelected.getItem3();
                        if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                            link = "http://" + link;
                        }

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        context.startActivity(browserIntent);
                    }
                }
            });
        }else{
            final CustomItem itemSelected = items.get(position);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(holder.cvContainer.getLayoutParams().width , menuWidth / 4);
            lp2.setMargins(iv.dpToPx(context, 12), iv.dpToPx(context, 6), iv.dpToPx(context, 12), iv.dpToPx(context, 6));
            holder.cvContainer.setLayoutParams(lp2);
            holder.tvTitle.setText(itemSelected.getItem3());
            holder.tvSubtitle.setText(itemSelected.getItem4());
            holder.tvDesc.setText(itemSelected.getItem6());
            if(itemSelected.getItem7() != null){
                holder.tvJarak.setText(Html.fromHtml(itemSelected.getItem7() + " km dari <strong>lokasi sekarang</strong>"));
            }
            ImageUtils iu2 = new ImageUtils();
            iu2.LoadRealImageWithSmall(context, itemSelected.getItem5(), holder.ivIcon, context.getResources().getDrawable(R.drawable.logo_semargres), new LeftRoundedCornersBitmap());

            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailMerchant.class);
                    intent.putExtra("id", itemSelected.getItem2());
                    if(itemSelected.getItem8() != null){
                        intent.putExtra("id_k", itemSelected.getItem8());
                    }
                    context.startActivity(intent);
                }
            });
        }
        return convertView;

    }
}
