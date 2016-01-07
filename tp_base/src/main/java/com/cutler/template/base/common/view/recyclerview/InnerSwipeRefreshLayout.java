package com.cutler.template.base.common.view.recyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author cutler
 */
public class InnerSwipeRefreshLayout extends SwipeRefreshLayout {

    public InnerSwipeRefreshLayout(Context context) {
        super(context);
    }

    public InnerSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        // 禁止正在执行下拉刷新的状态下，再次触发下拉刷新。
        return !isRefreshing() && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void setOnRefreshListener(final OnRefreshListener listener) {
        super.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                setEnabled(false);
                listener.onRefresh();
            }
        });
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (!refreshing) {
            setEnabled(true);
        }
        super.setRefreshing(refreshing);
    }
}
