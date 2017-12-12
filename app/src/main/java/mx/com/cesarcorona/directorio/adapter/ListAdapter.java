package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;

/**
 * Created by ccabrera on 11/12/17.
 */

public class ListAdapter extends BaseAdapter {


    private LinkedList<String> strinList;
    private Context context;


    public ListAdapter(LinkedList<String> strinList, Context context) {
        this.strinList = strinList;
        this.context = context;
    }




    @Override
    public int getCount() {
        return strinList.size();
    }

    @Override
    public Object getItem(int position) {
        return strinList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.big_layout_row,parent,false);
        TextView title = (TextView) root.findViewById(R.id.myTextView);
        title.setText(strinList.get(position));
        return root;

    }
}
