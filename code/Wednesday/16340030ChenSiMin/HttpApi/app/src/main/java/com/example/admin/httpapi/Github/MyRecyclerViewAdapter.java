package com.example.admin.httpapi.Github;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class MyRecyclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
    private List<T> data;
    private Context context;
    private int layoutId;

    public MyRecyclerViewAdapter(Context _context, int _layoutId, List<T> _data) {
        data = _data;
        layoutId = _layoutId;
        context = _context;
    }

    public abstract void convert(MyViewHolder holder, T t);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        convert(holder, data.get(position)); // convert函数需要重写
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public void addItem(T item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position < data.size()) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    public void refresh(List<T> list_) {
        data = list_;
        notifyDataSetChanged();
    }

    public  T getItem(int i) {
        if (i >=0 && i < data.size())
            return data.get(i);
        return null;
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}

