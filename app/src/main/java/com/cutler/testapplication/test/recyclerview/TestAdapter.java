package com.cutler.testapplication.test.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cutler.template.base.common.view.recyclerview.LoadMoreAdapter;
import com.cutler.testapplication.R;

import java.util.ArrayList;

/**
 * @author cutler
 */
public class TestAdapter extends LoadMoreAdapter<String> {

    public TestAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Normal) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_circle, parent, false);
            return new NormalViewHolder(v);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
            normalViewHolder.title.setText(data.get(position));
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;

        public NormalViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println(title.getText());
        }
    }
}
