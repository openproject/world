package com.tianxia.app.healthworld.infomation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.StatusInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class InfomationTabActivity extends AdapterActivity<StatusInfo>{

    private SmartImageView mItemAvatar;
    private TextView mItemName;
    private TextView mItemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInfomationList();
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(AppApplicationApi.INFOMATION_URL);
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(AppApplicationApi.INFOMATION_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, AppApplicationApi.INFOMATION_URL);
                    showInfomationList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    listView.setAdapter(null);
                }

            });
        }
    }

    private void showInfomationList(String result) {
        try {
            JSONObject statusConfig = new JSONObject(result);

            JSONArray statusList = statusConfig.getJSONArray("statuses");
            StatusInfo statusInfo = null;
            for (int i = 0; i < statusList.length(); i++) {
                statusInfo = new StatusInfo();
                statusInfo.avatar = statusList.getJSONObject(i).getString("avatar");
                statusInfo.name = statusList.getJSONObject(i).getString("name");
                statusInfo.author = statusList.getJSONObject(i).getString("author");
                statusInfo.text = statusList.getJSONObject(i).getString("text");
                statusInfo.id = statusList.getJSONObject(i).getLong("id");
                listData.add(statusInfo);
            }

            adapter = new Adapter(InfomationTabActivity.this);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.infomation_tab_activity);
        setListView(R.id.infomation_tab_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.infomation_tab_list_item, null);
        }

        mItemAvatar = (SmartImageView) view.findViewById(R.id.item_avatar);
        mItemAvatar.setImageUrl(listData.get(position).avatar, R.drawable.icon, R.drawable.icon);

        mItemName = (TextView) view.findViewById(R.id.item_name);
        mItemName.setText(listData.get(position).name);

        mItemText = (TextView) view.findViewById(R.id.item_text);
        mItemText.setText(listData.get(position).text);

        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

}
