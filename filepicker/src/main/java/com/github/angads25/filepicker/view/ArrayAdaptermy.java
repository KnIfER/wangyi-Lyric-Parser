package com.github.angads25.filepicker.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by KnIfER on 2018/3/26.
 */

public class ArrayAdaptermy<T> extends ArrayAdapter<T>{


    public ArrayAdaptermy(Context context, int resource) {
        super(context, resource);
    }

    public ArrayAdaptermy(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArrayAdaptermy(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    public ArrayAdaptermy(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArrayAdaptermy(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public ArrayAdaptermy(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        View ret = super.getView(position,convertView,parent);
        //if(ret.getTag(R.id.tag_110)==null)
        //    ret.setBackground(getContext().getResources().getDrawable(R.drawable.listviewselector_drawer));
        ((TextView)ret).setText(getItem(position).toString().replace("OVIHCS","/"));
        return ret;
    }
}
