package com.telephone.coursetable.AppWidgetProvider;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListRemoteViewsService extends RemoteViewsService {
    final public static String EXTRA_DATA_ARRAY_LIST_OF_STRING = "com.telephone.coursetable.ListRemoteViewsService.extra.data";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListAdapter(intent.getStringArrayListExtra(EXTRA_DATA_ARRAY_LIST_OF_STRING));
    }
}
