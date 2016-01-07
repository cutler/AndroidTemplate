package com.cutler.template.base.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cutler.template.base.R;

/**
 * @author cutler
 */
public class BaseActivity extends AppCompatActivity {

    private View mLoadingView;
    private View mContentView;
    private View mLoadingLostView;

    /**
     * 在原有的布局之上，加上loading、loadingLost布局，以便随时切换显示。
     *
     * @param layoutResID
     */
    protected void setContentViewWithLoading(int layoutResID) {
        // Activity真正的布局
        mContentView = LayoutInflater.from(this).inflate(layoutResID, null);
        // 加载中所显示的布局
        mLoadingView = LayoutInflater.from(this).inflate(R.layout.tp_inflate_common_loading, null);
        mLoadingView.setVisibility(View.GONE);
        // 加载失败所显示的布局
        mLoadingLostView = LayoutInflater.from(this).inflate(R.layout.tp_inflate_common_loading_lost, null);
        mLoadingLostView.setVisibility(View.GONE);
        mLoadingLostView.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onReloadButtonClicked();
            }
        });
        FrameLayout realRootView = new FrameLayout(this);
        realRootView.addView(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        realRootView.addView(mLoadingView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        realRootView.addView(mLoadingLostView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(realRootView);
    }

    /**
     * 设置contentView、loadingView、loadingLostView三个布局是否可见。
     *
     * @param contentView     mContentView
     * @param loadingView     mLoadingView
     * @param loadingLostView mLoadingLostView
     */
    protected void setContentViewVisible(boolean contentView, boolean loadingView, boolean loadingLostView) {
        if (mContentView != null) {
            mContentView.setVisibility(contentView ? View.VISIBLE : View.GONE);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(loadingView ? View.VISIBLE : View.GONE);
        }
        if (mLoadingLostView != null) {
            mLoadingLostView.setVisibility(loadingLostView ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 当前是否处于loading状态。
     *
     * @return
     */
    protected boolean isLoading() {
        return mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE;
    }

    /**
     * 当重新加载按钮被点击时回调此方法，子类用来重新发起网络请求。
     */
    protected void onReloadButtonClicked() {
    }

}
