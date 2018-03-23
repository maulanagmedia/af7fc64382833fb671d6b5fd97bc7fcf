package gmedia.net.id.semargres2018.MenuEkupon.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.SortedMap;

import gmedia.net.id.semargres2018.CustomView.LeftRoundedCornersBitmap;
import gmedia.net.id.semargres2018.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class MasterKuponAdapter extends ArrayAdapter{

    private Activity context;
    private SortedMap<String, List<CustomItem>> items;
    private List<String> merchantList;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;

    public MasterKuponAdapter(Activity context, SortedMap<String, List<CustomItem>> items, List<String> merchantList, int menuWidth) {
        super(context, R.layout.adapter_ekupon, merchantList);
        this.context = context;
        this.items = items;
        this.menuWidth = menuWidth;
        this.merchantList = merchantList;
    }

    private static class ViewHolder {

        public TextView tvText1;
        public RecyclerView rvEkupon;
    }

    public void addMoreData(SortedMap<String, List<CustomItem>> moreData){

        items.putAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public int getCount() {
        return merchantList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_ekupon, null);
            holder.tvText1 = (TextView) convertView.findViewById(R.id.tv_text1);
            holder.rvEkupon = (RecyclerView) convertView.findViewById(R.id.rv_ekupon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final String merchant = merchantList.get(position);
        final List<CustomItem> itemSelected = items.get(merchant);

        holder.tvText1.setText(merchant);

        if(itemSelected != null && itemSelected.size() > 0){

            KuponListAdapter menuAdapter = new KuponListAdapter(context, itemSelected, menuWidth);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
            holder.rvEkupon.setLayoutManager(mLayoutManager);
//        rvListMenu.addItemDecoration(new NavMenu.GridSpacingItemDecoration(2, dpToPx(10), true));
            holder.rvEkupon.setItemAnimator(new DefaultItemAnimator());
            holder.rvEkupon.setAdapter(menuAdapter);
        }
        return convertView;

    }
}
