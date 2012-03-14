package com.tianxia.app.floworld.appreciate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.model.DiscussInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;

public class AppreciateSearchActivity extends AdapterActivity<DiscussInfo> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_search_activity);
        setListView(R.id.appreciate_search_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        return null;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }
}
