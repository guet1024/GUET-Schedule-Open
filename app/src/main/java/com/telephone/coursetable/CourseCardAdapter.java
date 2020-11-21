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
import com.telephone.coursetable.R;

import java.util.List;

public class CourseCardAdapter extends FragmentStateAdapter {

    private List<ACard> card_list;

    public CourseCardAdapter(@NonNull Fragment fragment, @NonNull List<ACard> card_list) {
        super(fragment);
        this.card_list = card_list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new CourseCardFragment(card_list.get(position));
    }

    @Override
    public int getItemCount() {
        return card_list.size();
    }
}
