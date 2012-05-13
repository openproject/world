package com.tianxia.lib.baseworld.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tianxia.lib.baseworld.R;

/**
 * 下拉刷新，底部更多
 *
 */
public class RefreshListView extends ListView implements OnScrollListener{

    private float mDownY;
    private float mMoveY;
    private float mUpY;

    private int mHeaderInitTopPadding;
    private int mHeaderTopPadding;

    private int mCurrentScrollState;

    private LinearLayout mHeaderLinearLayout = null;
    private LinearLayout mFooterLinearLayout = null;
    private TextView mFooterTextView;

    public RefreshListView(Context context) {
        this(context, null);
    }
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        mHeaderLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_list_header, null);
        addHeaderView(mHeaderLinearLayout);

        mFooterLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_list_footer, null);
        addFooterView(mFooterLinearLayout);
        mFooterTextView = (TextView) mFooterLinearLayout.findViewById(R.id.refresh_list_footer_text);
        mFooterTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFooterTextView.setText(R.string.app_list_footer_loading);
            }
        });
        setSelection(1);
        setOnScrollListener(this);
        measureView(mHeaderLinearLayout);
        mHeaderInitTopPadding = mHeaderLinearLayout.getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getY();
                mHeaderTopPadding++;
            case MotionEvent.ACTION_UP:
                mHeaderTopPadding = 0;
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                        0,
                        mHeaderLinearLayout.getPaddingRight(),
                        mHeaderLinearLayout.getPaddingBottom());
        }
        System.out.println("MotionEvent.action:" + ev.getAction());
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            
        } else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0) {
            setSelection(1);
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mCurrentScrollState == SCROLL_STATE_IDLE) {
            mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                    0,
                    mHeaderLinearLayout.getPaddingRight(),
                    mHeaderLinearLayout.getPaddingBottom());
        }
        System.out.println("scroll:" + scrollState);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
}
