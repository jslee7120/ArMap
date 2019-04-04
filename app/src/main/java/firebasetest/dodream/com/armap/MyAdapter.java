package firebasetest.dodream.com.armap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import firebasetest.dodream.com.armap.model.Item;

public class MyAdapter extends ArrayAdapter<Item> {


    private final Context context;
    private final ArrayList<Item> itemsArrayList;

    public MyAdapter(Context context, ArrayList<Item> itemsArrayList) {

        super(context, R.layout.lv_row, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lv_row, parent, false);

        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);

        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());

        return rowView;
    }
}
