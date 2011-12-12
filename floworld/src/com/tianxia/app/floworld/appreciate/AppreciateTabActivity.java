package com.tianxia.app.floworld.appreciate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianxia.app.floworld.R;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;

public class AppreciateTabActivity extends AdapterActivity<Map<String,String>> {
//    private AppreciateApi appreciateApi = null;
    private int latestNum = 0;
    private String latestListUrl = null;
    private String mostListUrl = null;
    private String categoryListUrl = null;
    private String archiverListUrl = null;

    private Intent appreciateListIntent = null;

    private TextView itemTextView = null;
    private ImageView itemImageView = null;
    private int imageHeight = 0;
    private int dividerHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListData(false);
        getAppreciateConfig();

    }

    /**
     * 装载列表数据
     * @param isUpdate false=初始化,true=网络获取数据后更新列表数据
     */
    private void setListData(boolean isUpdate){

        listData.clear();
        Map<String,String> map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_new));
        if(!isUpdate){
            map.put("name", "最新(数据加载中...)");
        }else{
            if(latestNum == 0){
                map.put("name", "最新");
            }else{
                map.put("name", "最新(本期:" + latestNum + ")");
            }
        }

        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_category));
        map.put("name", "分类");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_archive));
        map.put("name", "归档");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_search));
        map.put("name", "搜索");
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("image", String.valueOf(R.drawable.appreciate_tab_list_item_hot));
        map.put("name", "合作");
        listData.add(map);

        adapter = new Adapter(this);
        listView.setAdapter(adapter);
    }

    /**
     * 读取所需的Appreciate基本信息
     */
    protected void getAppreciateConfig(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(AppreciateApi.APPRECIATE_CONFIG_URL, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String result){
                try {
                    JSONObject appreciateConfig = new JSONObject(result);

                    String baseUrl = appreciateConfig.getString("base-url");
                    latestNum = appreciateConfig.getJSONObject("latest").getInt("add");
                    latestListUrl = baseUrl + appreciateConfig.getJSONObject("latest").getString("list");
                    mostListUrl = baseUrl + appreciateConfig.getJSONObject("most").getString("list");
                    categoryListUrl = baseUrl + appreciateConfig.getJSONObject("category").getString("list");
                    archiverListUrl = baseUrl + appreciateConfig.getJSONObject("archiver").getString("list");

                    setListData(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void onFailure(Throwable arg0) {
                setListData(true);
            }

        });
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.appreciate_tab_activity);
        setListView(R.id.appreciate_tab_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        dividerHeight = ((ListView) getListView()).getDividerHeight();
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.appreciate_tab_list_item, null);
        }

        itemImageView = (ImageView) view.findViewById(R.id.item_image);
        itemImageView.setImageResource(Integer.parseInt(listData.get(position).get("image")));
        if(MainTabFrame.mainTabContainerHeight != 0){
            if(imageHeight==0){
                imageHeight = MainTabFrame.mainTabContainerHeight/5 - itemImageView.getPaddingTop() - itemImageView.getPaddingBottom() - dividerHeight*2;
            }
            itemImageView.getLayoutParams().height = imageHeight;
        }

        itemTextView = (TextView) view.findViewById(R.id.item_text);
        itemTextView.setText(listData.get(position).get("name"));
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {

        case 0:
            //最新
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateLatestActivity.class);
            appreciateListIntent.putExtra("url", latestListUrl);
            break;

        case 1:
            //分类
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateCategoryActivity.class);
            appreciateListIntent.putExtra("url", categoryListUrl);
            break;

        case 2:
            //归档
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateArchiverActivity.class);
            appreciateListIntent.putExtra("url", archiverListUrl);
            break;

        case 4:
            //合作
            appreciateListIntent = new Intent(AppreciateTabActivity.this, AppreciateCompanyActivity.class);
            appreciateListIntent.putExtra("url", mostListUrl);
            break;

        default:
            appreciateListIntent = null;
            break;
        }

        if (appreciateListIntent != null){
            startActivity(appreciateListIntent);
        }
    }
}
