package com.tianxia.app.healthworld.favorite;

import android.widget.AdapterView;
import android.widget.Adapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FavoriteListView extends AdapterView {

    private Adapter mAdapter;

    public FavoriteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mAdapter == null) {
            return;
        }

        if (getChildCount() == 0) {
            int position = 0;
            int bottomEdge = 0;
            while (bottomEdge < getHeight() && position < mAdapter.getCount()) {
                //View newBottomChild = mAdapter.getView(position, null, this);
                //addAndMeasureChild(newBottomChild);
                //bottomEdge += newBottomChild.getMeasuredHeight();
                //position++;
            }

            //positionItems();
        }
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

}
