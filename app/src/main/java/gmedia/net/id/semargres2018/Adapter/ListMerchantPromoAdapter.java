package gmedia.net.id.semargres2018.Adapter;

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

public class ListMerchantPromoAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;

    public ListMerchantPromoAdapter(Activity context, List<CustomItem> items, int menuWidth) {
        super(context, R.layout.adapter_list_promo, items);
        this.context = context;
        this.items = items;
        this.menuWidth = menuWidth;
    }

    private static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle, tvSubtitle;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_list_promo, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvSubtitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        ImageUtils iu2 = new ImageUtils();
        iu2.LoadRealImageWithSmall(context, itemSelected.getItem1(), holder.ivIcon, new LeftRoundedCornersBitmap());
        holder.tvTitle.setText(itemSelected.getItem2());
        holder.tvSubtitle.setText(itemSelected.getItem3());
        return convertView;

    }


}
