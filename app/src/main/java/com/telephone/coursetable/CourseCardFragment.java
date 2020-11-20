package com.telephone.coursetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CourseCardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.course_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((TextView)view.findViewById(R.id.course_card_cno)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_cname)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_weeks)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_tname)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_croom)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_ccommet)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_grade_point)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_ctype)).setText("中国移动");
        ((TextView)view.findViewById(R.id.course_card_examt)).setText("中国移动");
        view.findViewById(R.id.course_card_cmt_cno).setTag("中国移动"); // the cno
        view.findViewById(R.id.course_card_cmt_teacher).setTag("中国移动"); // the tno
    }
}
