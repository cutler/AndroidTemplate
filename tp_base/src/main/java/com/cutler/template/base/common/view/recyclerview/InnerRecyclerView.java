package com.cutler.template.base.common.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author cutler
 */
public class InnerRecyclerView extends RecyclerView {

    public InnerRecyclerView(Context context) {
        super(context);
    }

    public InnerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        // 解决RecyclerView与SwipeRefreshLayout的滑动冲突。
        if (direction < 1) {
            boolean original = super.canScrollVertically(direction);
            return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
        }
        return super.canScrollVertically(direction);

    }
}