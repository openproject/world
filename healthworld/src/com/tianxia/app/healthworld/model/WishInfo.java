package com.tianxia.app.healthworld.model;

import java.util.List;
import com.tianxia.lib.baseworld.widget.TagCloudInfo;
import com.tianxia.lib.baseworld.widget.DragListAdapter.IDragable;

public class WishInfo extends TagCloudInfo implements IDragable {

    @Override
    public String getDisplay() {
        return title;
    }
}
