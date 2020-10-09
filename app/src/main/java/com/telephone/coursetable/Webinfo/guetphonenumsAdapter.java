package com.telephone.coursetable.Webinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.telephone.coursetable.R;

import java.util.List;

public class guetphonenumsAdapter extends ArrayAdapter<Webinfoview> {
    private int resourceId;

    public guetphonenumsAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Webinfoview> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Webinfoview webinfoview = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageview=(ImageView) view.findViewById(R.id.imageView);
            viewHolder.webttitle=(TextView) view.findViewById(R.id.title);
            view.setTag(viewHolder);
        }else
        {
            view = convertView;
            viewHolder=(ViewHolder)view.getTag();

        }
        viewHolder.imageview.setImageResource(webinfoview.getIcon());
        viewHolder.webttitle.setText(webinfoview.getTitle());
        return view;
    }

    class ViewHolder {
        ImageView imageview;
        TextView webttitle;
    }
}
