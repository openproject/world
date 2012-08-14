package com.tianxia.lib.baseworld.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.LinearLayout;

public abstract class PreferenceActivity extends BaseActivity {

    public List<List<Map<String,String>>> listDatas = new ArrayList<List<Map<String,String>>>();
    public LinearLayout cornerContainer = null;

    //set the layout and cornerListViews container
    public abstract void setLayout();
    public abstract void setListDatas();
}
