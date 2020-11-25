package com.telephone.coursetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.R;

import java.util.List;

public class CourseCardAdapter extends FragmentStateAdapter {

    private CourseCardData total_data;
    private List<ACard> card_list;

    public CourseCardAdapter(@NonNull Fragment fragment, @NonNull List<ACard> card_list, @NonNull CourseCardData total_data) {
        super(fragment);
        this.card_list = card_list;
        this.total_data = total_data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position >= card_list.size()){
            return new CourseCardFragment(null, total_data);
        }else {
            return new CourseCardFragment(card_list.get(position), total_data);
        }
    }

    @Override
    public int getItemCount() {
        return card_list.size() + 1;
    }
}
