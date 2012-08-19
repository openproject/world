package com.tianxia.lib.baseworld.widget;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import java.util.List;

import com.tianxia.lib.baseworld.R;

public class DragListAdapter<T extends DragListAdapter.IDragable> extends ArrayAdapter<T> {
    public DragListAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if ( view == null) {
            //加载列表项模板
            view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item, null);
        }

        TextView textView = (TextView)view.findViewById(R.id.drag_list_item_text);
        textView.setText(getItem(position).getDisplay());
        return view;
    }

    public interface IDragable {
        String getDisplay();
    }
}
