package com.tianxia.lib.baseworld.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppUpdateService extends Service{

    public static final int APP_VERSION_LATEST = 0;
    public static final int APP_VERSION_OLDER = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int check() {
        return 0;
    }

}
