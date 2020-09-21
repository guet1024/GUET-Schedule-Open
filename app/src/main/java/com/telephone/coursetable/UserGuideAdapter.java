package com.telephone.coursetable;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserGuideAdapter extends RecyclerView.Adapter<UserGuideAdapter.ViewPagerViewHolder> {
    private ImageView[] mImageViews;
    private List<Integer> imgIdArray = new ArrayList<>();
    {
        imgIdArray.add(R.drawable.page0);
        imgIdArray.add(R.drawable.userpage_login);
        imgIdArray.add(R.drawable.page1);
        imgIdArray.add(R.drawable.page2);
        imgIdArray.add(R.drawable.page3);
        imgIdArray.add(R.drawable.page4);
        imgIdArray.add(R.drawable.userguide_xiaobujian);
        imgIdArray.add(R.drawable.userguide_noclass);
        imgIdArray.add(R.drawable.userguide_hasclass);
        imgIdArray.add(R.drawable.userguide_keepalive);
        imgIdArray.add(R.drawable.userpage_wakeapp);
        imgIdArray.add(R.drawable.nextlessonnotify);
    }
    private List<String> title = new ArrayList<>();
    {
        title.add("登录页面");
        title.add("登录页面");
        title.add("课程表页面");
        title.add("更多功能页面");
        title.add("修改上课时间页面");
        title.add("更改学期周数页面");
        title.add("GUET课程表小部件");
        title.add("GUET课程表小部件");
        title.add("GUET课程表小部件");
        title.add("提示");
        title.add("提示");
        title.add("通知");
    }
    @NonNull
    @Override
    public UserGuideAdapter.ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.userguider_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserGuideAdapter.ViewPagerViewHolder holder, int position) {
        if(position==imgIdArray.size()){
            holder.textView3.setText("我们提供的一些功能需要保持APP在后台运行/开机自启。如果您能够保持此应用在后台运行/开机自启，我们将能够为您自动同步数据、实时刷新小部件、及时提醒即将发生的事件、及时检测新版本应用。\n" +
                    "一般将应用加入后台运行白名单需要用到四个设置：电池设置、启动管理、手机管家白名单设置、后台运行应用设置:\n" +
                    "　　1.将省电模式切换为关闭\n" +
                    "　　2.在后台节能将此应用更改为无限制\n" +
                    "　　3.在启动管理将此应用设置为开机自启动，后台自启动\n" +
                    "　　4.在多任务页面将此应用上锁以避免被系统误杀\n" +
                    "　　5.将此应用加入手机管家后台运行白名单\n" +
                    "　　6.在手机设置中允许此应用后台运行\n" +
                    "各品牌手机间存在差异，若上述步骤无法让APP保持后台运行/开机自启，则需要您自行寻找解决方案。" +
                    "后续更新中我们将提供各品牌手机加入白名单的详细步骤。");
            String S =String.valueOf(position+1)+"/"+String.valueOf(imgIdArray.size()+1);
            holder.textView2.setText(S);
            holder.textView3.setMovementMethod(ScrollingMovementMethod.getInstance());
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.textView.setVisibility(View.INVISIBLE);
            holder.textView3.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageView.setImageResource(imgIdArray.get(position));
            holder.textView.setText(title.get(position));
            String S =String.valueOf(position+1)+"/"+String.valueOf(imgIdArray.size()+1);
            holder.textView2.setText(S);
            holder.textView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView3.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return imgIdArray.size()+1;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView2;
        TextView textView3;
        public ViewPagerViewHolder(@NonNull View itemView){
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.pageinfo);
            textView2 = itemView.findViewById(R.id.pagenum);
            textView3 = itemView.findViewById(R.id.hugetextview);
        }
    }
}
