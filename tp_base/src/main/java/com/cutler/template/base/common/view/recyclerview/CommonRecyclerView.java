package com.cutler.template.base.common.view.recyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cutler.template.base.R;

/**
 * @author cutler
 */
public class CommonRecyclerView extends LinearLayout {
    private RecyclerView mRecyclerView;
    private InnerSwipeRefreshLayout mSwipeRefreshLayout;
    private OnLoadMoreListener mLoadMoreListener;
    private DividerItemDecoration itemDecoration;
    private boolean isLoadMore;

    public CommonRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化SwipeRefreshLayout。
        mSwipeRefreshLayout = new InnerSwipeRefreshLayout(context);

        // 初始化RecyclerView。
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.tp_inflate_common_recyclerview, null);
        mRecyclerView.setLayoutManager(mLayoutManager);
        itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mRecyclerView.getAdapter().getItemCount() - 1) {
                    if (mLoadMoreListener != null && !isLoadMore) {
                        isLoadMore = true;
                        mLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
        // 将二者加入到布局中。
        mSwipeRefreshLayout.addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mSwipeRefreshLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void removeItemDecoration() {
        mRecyclerView.removeItemDecoration(itemDecoration);
    }


    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    /**
     * 下拉刷新完毕时，调用此方法。
     */
    public void refreshFinish() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 加载更多完毕时，调用此方法。
     */
    public void loadMoreFinish() {
        isLoadMore = false;
    }


    public static interface OnLoadMoreListener {
        public void onLoadMore();
    }
}
