package com.cutler.testapplication.test.recyclerview;

/**
 * @author cutler
 */
public class RecyclerViewTest {

    public static void testCode() {
//        把下面代码放到Activity中。


//        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:orientation="vertical">
//
//        <com.cutler.template.base.common.view.recyclerview.CommonRecyclerView
//        android:id="@+id/my_recycler_view"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"/>
//
//        </LinearLayout>


//        final CommonRecyclerView mRecyclerView = (CommonRecyclerView) findViewById(R.id.my_recycler_view);
//        final TestAdapter mAdapter = new TestAdapter();
//        mAdapter.setTotalSize(101);
//        for (int i = 0; i < 20; i++) {
//            mAdapter.getData().add("test - " + i);
//        }
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mRecyclerView.refreshFinish();
//                    }
//                }, 2000);
//            }
//        });
//        mRecyclerView.setOnLoadMoreListener(new CommonRecyclerView.OnLoadMoreListener() {
//            public void onLoadMore() {
//                System.out.println("loadMore");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mRecyclerView.loadMoreFinish();
//                        if (mAdapter.hasNextPage()) {
//                            for (int i = 0; i < 20; i++) {
//                                mAdapter.getData().add("test - " + mAdapter.getData().size());
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }, 1000);
//            }
//        });
    }
}
