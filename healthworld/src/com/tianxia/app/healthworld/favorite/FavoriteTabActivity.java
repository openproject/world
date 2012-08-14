package com.tianxia.app.healthworld.favorite;

import android.os.Bundle;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.Menu;

public class FavoriteTabActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_tab_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_add_wish:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite_edit_wish:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite_clear_wish:
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
