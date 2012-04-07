package com.tianxia.app.healthworld.category;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;

public class CategoryTabActivity extends  AdapterActivity<Map<String,String>> {

    private TextView mItemTextView = null;
    private ImageView mItemImageView = null;
    private int mImageHeight = 0;
    private int mDividerHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListData();
    }
    @Override
    protected void setLayoutView() {
        setContentView(R.layout.category_tab_activity);
        setListView(R.id.category_tab_list);
    }

    /**
     * 装载列表数据
     * @param isUpdate false=初始化,true=网络获取数据后更新列表数据
     */
    private void setListData(){

        listData.clear();
        Map<String,String> map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.app_arrow));
        map.put("name", "饮食");

        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.app_arrow));
        map.put("name", "锻炼");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.app_arrow));
        map.put("name", "心理");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.app_arrow));
        map.put("name", "急救");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.app_arrow));
        map.put("name", "其它");
        listData.add(map);

        adapter = new Adapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected View getView(int position, View convertView) {
        mDividerHeight = ((ListView) getListView()).getDividerHeight();
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_tab_list_item, null);
        }

        mItemImageView = (ImageView) view.findViewById(R.id.item_image);
        mItemImageView.setImageResource(Integer.parseInt(listData.get(position).get("image")));
        if(MainTabFrame.mainTabContainerHeight != 0){
            if(mImageHeight==0){
                mImageHeight = MainTabFrame.mainTabContainerHeight/5 - mItemImageView.getPaddingTop() - mItemImageView.getPaddingBottom() - mDividerHeight*2;
            }
            mItemImageView.getLayoutParams().height = mImageHeight;
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_text);
        mItemTextView.setText(listData.get(position).get("name"));
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view,
            int position, long id) {
    }

}
