package gmedia.net.id.semargres2018.MenuNearby.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.semargres2018.R;

/**
 * Created by Shinmaul on 3/20/2018.
 */

public class SearchviewAdapter extends CursorAdapter {

    private List<String> items;

    private TextView text;

    public SearchviewAdapter(Context context, Cursor cursor, List<String> items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        text.setText(items.get(cursor.getPosition()));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_sugestion, parent, false);

        text = (TextView) view.findViewById(R.id.text);

        return view;

    }

}
