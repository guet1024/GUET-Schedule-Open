package com.telephone.coursetable.GuetTools;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebLinksAdapter implements ListAdapter {

    private DataSetObservable dataSetObservable;
    private List<Map.Entry<String, String>> list;
    private Context c;
    private boolean qq = true;

    public WebLinksAdapter(List<Map.Entry<String, String>> list, Context c, boolean qq){
        this.dataSetObservable = new DataSetObservable();
        this.list = list;
        this.c = c;
        this.qq = qq;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = ((LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.web_links_item, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.weblinks_item_link)).setText(list.get(position).getKey());
        if (qq) {
            if (list.get(position).getKey().contains("公众号")){
                convertView.setOnClickListener(view -> ImageActivity.initmap(c, Integer.parseInt(list.get(position).getValue()), list.get(position).getKey()));
            }else {
                String value = list.get(position).getValue();
                String delimiter = " | ";
                int deli_index = value.indexOf(delimiter);
                String qq_key = value.substring(0, deli_index);
                int id = Integer.parseInt(value.substring(deli_index + delimiter.length()));
                convertView.setOnClickListener(view -> {
                    if (!joinQQGroup(qq_key)) {
//                        Snackbar.make(view, "未安装手Q或安装的版本不支持", BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.WHITE).show();
                        ImageActivity.initmap(c, id, list.get(position).getKey());
                    }
                });
            }
        } else {
            convertView.setOnClickListener(view -> c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getValue()))));
        }
        return convertView;
    }

    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        if (key.length() > 20){
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        }else {
            intent.setData(Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + key));
//            Toast.makeText(c, "正在跳转到QQ…", Toast.LENGTH_SHORT).show();
        }
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            c.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    //all items are with the same view type
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    //all items are with the same view type
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    //all items are enabled
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    //all items are enabled
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
