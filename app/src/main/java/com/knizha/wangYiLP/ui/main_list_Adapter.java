package com.knizha.wangYiLP.ui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.knizha.wangYiLP.CMN;
import com.knizha.wangYiLP.R;

import java.util.ArrayList;
import java.util.HashMap;

/*列表视图容器*/
class MyViewHolder extends RecyclerView.ViewHolder{
    private ImageView icon;
    public TextView time;
    public TextView line;
    public TextView indicator;
    public EditText url;
    public MyViewHolder(View view)
    {
        super(view);
        url = (EditText) view.findViewById(R.id.et);
        time = (TextView) view.findViewById(R.id.tv1);
        line = (TextView) view.findViewById(R.id.tv0);
        indicator = (TextView) view.findViewById(R.id.tv3);
        //tv.setTextColor(Color.parseColor("#ff000000"));
        //tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,CMN.a.scale(81));//TODO: optimize
    }
}
/*for main list - lyrics
   参见：live pull -> main -> main_list_Adapter
   参见：P.L.O.D -> float search view -> HomeAdapter*/
class main_list_Adapter extends RecyclerView.Adapter<MyViewHolder>
{
    String currentProjId;
    //作为recycler view 的数据存储
    public ArrayList<String> module_set;
    //构造
    main_list_Adapter(ArrayList<String> module_set_){
        module_set = module_set_;
    }

    protected void update_slots_time(int position, String val) {
        if(val!=null)
            slots_time.put(position, val);
        else {
            slots_time.remove(position);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount()
    {
        return module_set.size();
    }

    public HashMap<Integer,String> selectedPositions = new HashMap<Integer,String>();
    public HashMap<Integer,String> slots_time = new HashMap<Integer,String>();
    private int mTouchItemPosition = -1;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    //单击
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //长按
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener)
    {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    //Create
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //TODO find out why CMN.a.mInflater stimes NullPointerException
       MyViewHolder holder = new MyViewHolder(CMN.a.inflater.inflate(R.layout.listview_item1, parent,false));
        return holder;
    }
    //Bind
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        if(selectedPositions.containsKey(position)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#4F7FDF"));//FF4081
            holder.time.setText(selectedPositions.get(position));
        }else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00a0f0f0"));//aaa0f0f0
            holder.time.setText("---");
        }
        //if(startCandidate==position){
        //    holder.indicator.setVisibility(View.VISIBLE);
        //}else{
        //    holder.indicator.setVisibility(View.INVISIBLE);
        //}
        holder.line.setText(position+"");
        holder.url.setTag(R.id.position,position);
        holder.url.clearFocus();
        holder.url.setText(module_set.get(position));
        holder.url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(CMN.a==null) return;//TODO :optimize
                if (CMN.a.getCurrentFocus() == holder.url) {
                    module_set.set((Integer) holder.url.getTag(R.id.position),s.toString());
                }
            }});

        holder.url.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //注意，此处必须使用getTag的方式，不能将position定义为final，写成mTouchItemPosition = position
                mTouchItemPosition = position;
                return false;
            }
        });
        //if(!CMN.a.opt.editor_collapse_item_when_NotFocused) {
        //	holder.url.setSingleLine(false);
        //}else{
        //holder.url.setOnFocusChangeListener(Main_editor_Fragment.this);
        //}
        //处理 Focus 冲突
        if (mTouchItemPosition == position) {
            holder.url.requestFocus();
            //holder.url.setSelection(holder.url.getText().length());
        } else {
            holder.url.clearFocus();
            holder.url.setSingleLine();
        }

        if(mOnItemClickListener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getLayoutPosition?
                    mOnItemClickListener.onItemClick(holder.itemView,position); // 2
                }
            });
        }
        if(mOnItemLongClickListener != null)
        {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }

    }

    public void removeSelected(int position)
    {
        selectedPositions.remove(  position);
        notifyItemChanged(position);
    }
    public boolean containSelected(int position) {
        return selectedPositions.containsKey(position);
    }
}
//![2]