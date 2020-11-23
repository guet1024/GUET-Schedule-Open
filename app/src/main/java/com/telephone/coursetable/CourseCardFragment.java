package com.telephone.coursetable;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.telephone.coursetable.Gson.CourseCard.ACard;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;
import com.telephone.coursetable.LogMe.LogMe;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CourseCardFragment extends Fragment {

    private CourseCardData total_data;
    private ACard card;

    public CourseCardFragment(@Nullable ACard card, @NonNull CourseCardData total_data){
        this.card = card;
        this.total_data = total_data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (card == null){
            return inflater.inflate(R.layout.no_course_card, container, false);
        }else {
            return inflater.inflate(R.layout.course_card, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final String NAME = "onViewCreated()";
        if (card != null) {
            ((TextView) view.findViewById(R.id.course_card_cno)).setText(card.getCno());
            ((TextView) view.findViewById(R.id.course_card_cname)).setText(card.getCname());
            ((TextView) view.findViewById(R.id.course_card_weeks)).setText(card.getStart_week() + "-" + card.getEnd_week());
            ((TextView) view.findViewById(R.id.course_card_tname)).setText(card.getTname());
            ((TextView) view.findViewById(R.id.course_card_croom)).setText(card.getCroom());
            ((TextView) view.findViewById(R.id.course_card_sysccommet)).setText(card.getSys_comm());
            ((TextView) view.findViewById(R.id.course_card_myccommet)).setText(card.getMy_comm());
            ((TextView) view.findViewById(R.id.course_card_grade_point)).setText(card.getGrade_point() + "");
            ((TextView) view.findViewById(R.id.course_card_ctype)).setText(card.getCtype());
            ((TextView) view.findViewById(R.id.course_card_examt)).setText(card.getExamt());
            if (card.isCustomized()){
                ((TextView) view.findViewById(R.id.course_card_source)).setText("自定义");
                ((TextView) view.findViewById(R.id.course_card_cmt_cno)).setEnabled(true);
                ((TextView) view.findViewById(R.id.course_card_cmt_teacher)).setEnabled(true);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, "自定义的课程不支持评论功能", BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
                    }
                };
                ((TextView) view.findViewById(R.id.course_card_cmt_cno)).setOnClickListener(listener);
                ((TextView) view.findViewById(R.id.course_card_cmt_teacher)).setOnClickListener(listener);
            }else {
                ((TextView) view.findViewById(R.id.course_card_source)).setText("系统");
                ((TextView) view.findViewById(R.id.course_card_cmt_cno)).setEnabled(true);
                ((TextView) view.findViewById(R.id.course_card_cmt_teacher)).setEnabled(true);
            }

            view.findViewById(R.id.course_card_cmt_cno).setTag(card.getCno()); // the cno
            view.findViewById(R.id.course_card_cmt_teacher).setTag(card.getTno()); // the tno
            view.findViewById(R.id.course_card_cname).setTag(card.getCname()); // the cname
            view.findViewById(R.id.course_card_cname).setTag(CourseCard.TITLE_TAG_KEY, "课程名称"); // the cname title
            view.findViewById(R.id.course_card_sysccommet).setTag(card.getSys_comm()); // the system comment
            view.findViewById(R.id.course_card_sysccommet).setTag(CourseCard.TITLE_TAG_KEY, "系统备注"); // the system comment title
            view.findViewById(R.id.course_card_myccommet).setTag(card.getMy_comm()); // the customized comment
            view.findViewById(R.id.course_card_myccommet).setTag(CourseCard.TITLE_TAG_KEY, "自定义备注"); // the customized comment title

            CourseCardData with_a_card = null;
            try {
                with_a_card = total_data.deepClone();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                LogMe.e(NAME, "deep clone errors");
            }
            List<ACard> data_card = new LinkedList<>();
            data_card.add(card);
            with_a_card.setCards(data_card);
            view.findViewById(R.id.course_card_edit).setTag(with_a_card);

            ((TextView) view.findViewById(R.id.course_card_sysccommet)).setMovementMethod(new ScrollingMovementMethod());
            ((TextView) view.findViewById(R.id.course_card_myccommet)).setMovementMethod(new ScrollingMovementMethod());
            ((TextView) view.findViewById(R.id.course_card_cname)).setMovementMethod(new ScrollingMovementMethod());
        }else {
            CourseCardData no_card = null;
            try {
                no_card = total_data.deepClone();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                LogMe.e(NAME, "deep clone errors");
            }
            no_card.setCards(new LinkedList<>());
            view.findViewById(R.id.no_course_card_inner_layout).setTag(no_card);
        }
    }
}
