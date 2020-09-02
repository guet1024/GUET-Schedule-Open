package com.telephone.coursetable.AppWidgetProvider;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListRemoteViewsService extends RemoteViewsService {
    public static final String EXTRA_ARRAY_LIST_OF_STRING_TO_GET_A_NEW_REMOTE_ADAPTER = "EXTRA_ARRAY_LIST_OF_STRING_TO_GET_A_NEW_REMOTE_ADAPTER";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {// create a new remote adapter
        return new ListAdapter();
    }
}
