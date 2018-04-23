package com.knizha.wangYiLP.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by KnIfER on 2018/3/26.
 */

public class ArrayAdaptermy<T> extends ArrayAdapter<T>{


    public ArrayAdaptermy(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public ArrayAdaptermy(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArrayAdaptermy(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    public ArrayAdaptermy(@NonNull Context context, int resource, int textViewResourceId, @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArrayAdaptermy(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    public ArrayAdaptermy(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View ret = super.getView(position,convertView,parent);
        //if(ret.getTag(R.id.tag_110)==null)
        //    //ret.setBackgroundColor(Color.parseColor("#000000"));
        //    ret.setBackground(getContext().getResources().getDrawable(R.drawable.listviewselector_drawer));
        return ret;
    }


    //@Override
    //public int getCount() {
    //    return 1;
    //}

}
