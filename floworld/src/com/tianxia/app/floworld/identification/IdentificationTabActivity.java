package com.tianxia.app.floworld.identification;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tianxia.app.floworld.R;
import com.tianxia.app.floworld.model.IdentificationInfo;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class IdentificationTabActivity extends Activity {

    private List<IdentificationInfo> mDataList;
    private SmartImageView mSmartImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identification_tab_activity);

        mSmartImageView = (SmartImageView) findViewById(R.id.identification_image);

        loadIdentificationList();

        mSmartImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                randowShowIdentification();
            }
        });
    }

    private void randowShowIdentification() {
        if (mDataList.size() == 0) {
            Toast.makeText(this, "你已经全部识别完成.", Toast.LENGTH_SHORT).show();
            return;
        }

        int random = (int)(Math.random() * mDataList.size());
        if (random >= 0 && random < mDataList.size()) {
            mSmartImageView.setImageUrl(mDataList.get(random).url);
            mDataList.remove(random);
        }
    }

    private void loadIdentificationList() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(IdentificationApi.IDENTIFICATION_CONFIG_URL, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String result) {
                setIdentificationList(result);
                randowShowIdentification();
            }

            @Override
            public void onFailure(Throwable arg0) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private void setIdentificationList(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("list");
            mDataList = new ArrayList<IdentificationInfo>();
            IdentificationInfo identificationInfo = null;
            for(int i = 0; i < jsonArray.length(); i++){
                identificationInfo = new IdentificationInfo();
                identificationInfo.name = jsonArray.getJSONObject(i).optString("category");
                identificationInfo.url = jsonArray.getJSONObject(i).optString("thumbnail");
                identificationInfo.position = i;
                mDataList.add(identificationInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
