package com.telephone.coursetable;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.telephone.coursetable.Gson.CourseCard.ACard;

public class CourseCardFragment extends Fragment {

    private ACard card;

    public CourseCardFragment(@Nullable ACard card){
        this.card = card;
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
        if (card != null) {
            ((TextView) view.findViewById(R.id.course_card_cno)).setText(card.getCno());
            ((TextView) view.findViewById(R.id.course_card_cname)).setText(card.getCname());
            ((TextView) view.findViewById(R.id.course_card_weeks)).setText(card.getStart_week() + "-" + card.getEnd_week());
            ((TextView) view.findViewById(R.id.course_card_tname)).setText(card.getTname());
            ((TextView) view.findViewById(R.id.course_card_croom)).setText(card.getCroom());
            ((TextView) view.findViewById(R.id.course_card_ccommet)).setText(card.getComment());
            ((TextView) view.findViewById(R.id.course_card_grade_point)).setText(card.getGrade_point() + "");
            ((TextView) view.findViewById(R.id.course_card_ctype)).setText(card.getCtype());
            ((TextView) view.findViewById(R.id.course_card_examt)).setText(card.getExamt());

            view.findViewById(R.id.course_card_cmt_cno).setTag(card.getCno()); // the cno
            view.findViewById(R.id.course_card_cmt_teacher).setTag(card.getTno()); // the tno
            view.findViewById(R.id.course_card_cname).setTag(card.getCname()); // the cname
            view.findViewById(R.id.course_card_cname).setTag(CourseCard.TITLE_TAG_KEY, "课程名称"); // the cname title
            view.findViewById(R.id.course_card_ccommet).setTag(card.getComment()); // the comment
            view.findViewById(R.id.course_card_ccommet).setTag(CourseCard.TITLE_TAG_KEY, "备注"); // the comment title

            ((TextView) view.findViewById(R.id.course_card_ccommet)).setMovementMethod(new ScrollingMovementMethod());
            ((TextView) view.findViewById(R.id.course_card_cname)).setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
