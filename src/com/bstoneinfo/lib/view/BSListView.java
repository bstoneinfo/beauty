package com.bstoneinfo.lib.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bstoneinfo.lib.widget.BSBaseAdapter;
import com.bstoneinfo.lib.widget.BSViewCell;

import custom.R;

public class BSListView extends ListView {

    public interface CreateCellDelegate {
        BSViewCell createCell();
    }

    public interface PullUpWillLoadListener {
        boolean onBeginLoading();
    }

    // 用户需要在UI层合适时机设置 normal failed finished 状态，loading状态为此类内部用于判断的一个状态
    public enum PullUpStates {
        NORMAL,
        LOADING,
        FAILED,
        FINISHED
    }

    private BSListViewImpl impl;
    private BSBaseAdapter adapter;
    private CreateCellDelegate createCellDelegate;

    public BSListView(Context context) {
        super(context);
    }

    public BSListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCreateCellDelegate(CreateCellDelegate createCellDelegate) {
        this.createCellDelegate = createCellDelegate;
    }

    public void init(ArrayList<?> dataList) {
        init(dataList, null, null);
    }

    public void init(ArrayList<?> dataList, ArrayList<View> headerViews, ArrayList<View> footerViews) {
        if (headerViews != null) {
            for (View headerView : headerViews) {
                addHeaderView(headerView, null, false);
            }
        }
        if (footerViews != null) {
            for (View footerView : footerViews) {
                addFooterView(footerView, null, false);
            }
        }
        adapter = new BSBaseAdapter(getContext(), dataList) {
            @Override
            public BSViewCell createCell() {
                return createCellDelegate.createCell();
            }
        };
        setAdapter(adapter);
        impl = new BSListViewImpl(this);
    }

    public void setPullUpFooter(int normalResId, int loadingResId, int failedResId, int finishedResId) {
        impl.setPullUpFooter(normalResId, loadingResId, failedResId, finishedResId);
    }

    // 调用者必须设置listener 并在回调中load More
    public void setPullUpWillLoadListener(PullUpWillLoadListener listener) {
        impl.setPullUpWillLoadListener(listener);
    }

    public PullUpStates getPullUpStates() {
        return impl.getPullUpStates();
    }

    public void setPullUpState(PullUpStates state) {
        impl.setPullUpState(state);
    }

    static class BSListViewImpl {
        private ListView listView;
        private PullUpStates currentState;
        private View footerView;
        private boolean bHideFooterView;
        private PullUpWillLoadListener pullUpWillLoadListener;

        BSListViewImpl(ListView listView) {
            this.listView = listView;
            footerView = ((Activity) listView.getContext()).getLayoutInflater().inflate(R.layout.loadmore, null);
            setPullUpFooter(R.id.loadmore_loading, R.id.loadmore_loading, R.id.loadmore_failed, 0);
            listView.addFooterView(footerView);
            listView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        doLoadmore();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    doLoadmore();
                }
            });
        }

        private boolean bGet = true;//用于FAILED状态时，重新拉回到底部时自动重新LOADING

        private void doLoadmore() {
            if (pullUpWillLoadListener == null) {
                setPullUpState(PullUpStates.FINISHED);
                return;
            }
            if (listView.getLastVisiblePosition() <= listView.getCount() - 2) {
                bGet = false;// 可以允许获取新得数据了
            }
            // 风火轮或者刷新按钮item出现时就开始发请求 要判断是否上滑，判断是否failed的重入
            if ((currentState == PullUpStates.NORMAL || currentState == PullUpStates.FAILED) && listView.getLastVisiblePosition() == listView.getCount() - 1 && !bGet) {
                // 开始获取后等到下次才开始获取。
                if (pullUpWillLoadListener != null && pullUpWillLoadListener.onBeginLoading()) {
                    bGet = true;
                    setPullUpState(PullUpStates.LOADING);
                }
            }
        }

        private View normalView;
        private View loadingView;
        private View failedView;
        private View finishedView;
        private View currentFooterView;

        void setPullUpFooter(int normalResId, int loadingResId, int failedResId, int finishedResId) {

            normalView = footerView.findViewById(normalResId);
            loadingView = footerView.findViewById(loadingResId);
            failedView = footerView.findViewById(failedResId);
            finishedView = footerView.findViewById(finishedResId);

            if (normalView != null) {
                normalView.setVisibility(View.GONE);
            }
            if (failedView != null) {
                failedView.setVisibility(View.GONE);
                View load_failed_refresh = failedView.findViewById(R.id.loadmore_refresh);
                if (load_failed_refresh != null) {
                    load_failed_refresh.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (pullUpWillLoadListener != null && pullUpWillLoadListener.onBeginLoading()) {
                                bGet = true;
                                setPullUpState(PullUpStates.LOADING);
                            }
                        }
                    });
                }
            }
            if (loadingView != null) {
                loadingView.setVisibility(View.GONE);
            }
            if (finishedView != null) {
                finishedView.setVisibility(View.GONE);
            }

            setPullUpState(PullUpStates.NORMAL);
        }

        void setPullUpWillLoadListener(PullUpWillLoadListener listener) {
            this.pullUpWillLoadListener = listener;
        }

        PullUpStates getPullUpStates() {
            return currentState;
        }

        void setPullUpState(PullUpStates state) {
            currentState = state;
            View newFooterView;
            if (bHideFooterView) {
                newFooterView = null;
            } else if (state == PullUpStates.NORMAL) {
                newFooterView = normalView;
            } else if (state == PullUpStates.LOADING) {
                newFooterView = loadingView;
            } else if (state == PullUpStates.FAILED) {
                newFooterView = failedView;
            } else if (state == PullUpStates.FINISHED) {
                newFooterView = finishedView;
            } else {
                newFooterView = null;
            }
            if (currentFooterView != newFooterView) {
                if (currentFooterView != null) {
                    currentFooterView.setVisibility(View.GONE);
                }
                if (newFooterView != null) {
                    newFooterView.setVisibility(View.VISIBLE);
                }
                currentFooterView = newFooterView;
            }
        }

        void showPullUpBar() {
            bHideFooterView = false;
            setPullUpState(currentState);
        }

        void hidePullUpBar() {
            bHideFooterView = true;
            setPullUpState(currentState);
        }
    }
}
