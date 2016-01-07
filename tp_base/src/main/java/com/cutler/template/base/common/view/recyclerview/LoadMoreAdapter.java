package com.cutler.template.base.common.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cutler.template.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cutler
 */
public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final int Normal = 1;
    protected final int Footer = 2;

    // 列表中当前的数据。
    protected List<T> data = new ArrayList<T>();
    // 总共有多少条数据。
    protected int totalSize;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Footer) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_inflate_common_load_more, parent, false);
            return new FooterViewHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (hasNextPage() && position == getItemCount() - 1)
            return Footer;
        else
            return Normal;
    }

    @Override
    public int getItemCount() {
        return hasNextPage() ? data.size() + 1 : data.size();
    }

    public boolean hasNextPage() {
        return data.size() < totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getData() {
        return data;
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
