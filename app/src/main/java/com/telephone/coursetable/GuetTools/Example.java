package com.telephone.coursetable.GuetTools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.telephone.coursetable.R;

import java.util.LinkedList;
import java.util.List;

public abstract class Example extends AppCompatActivity {

    private float y_up;
    private float y_down;
    private float x_left;
    private float x_right;

    private float y_up_scale;
    private float y_down_scale;
    private float x_left_scale;
    private float x_right_scale;

    private volatile boolean end = true;
    private volatile boolean next = true;

    private synchronized boolean isNext() {
        return next;
    }

    private synchronized void setNext(boolean next) {
        this.next = next;
    }

    private synchronized boolean isEnd() {
        return end;
    }

    private synchronized void setEnd(boolean end) {
        this.end = end;
    }

    protected abstract int getImgResId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guet_tools_example);

        ImageView imv = findViewById(R.id.map_image);
        View mask = findViewById(R.id.mask_of_map_image);

        imv.setImageResource(getImgResId());

        imv.post(()->{
            Bitmap b = ((BitmapDrawable)imv.getDrawable()).getBitmap();
            int iw = b.getWidth();
            int ih = b.getHeight();
            float ih_iw = (float) ih / (float) iw;
            final int vw_origin = imv.getWidth();
            final int vh_origin = imv.getHeight();
            int vw = vw_origin;
            int vh = vh_origin;
            float vh_vw = (float) vh / (float) vw;
            if (vh_vw > ih_iw){
                vh = (int)(vw * ih_iw);
            }else {
                vw = (int)(vh / ih_iw);
            }
            ConstraintLayout.LayoutParams layout = new ConstraintLayout.LayoutParams(vw, vh);
            layout.topToTop = R.id.ac2_layout;
            layout.bottomToBottom = R.id.ac2_layout;
            layout.startToStart = R.id.ac2_layout;
            layout.endToEnd = R.id.ac2_layout;
            imv.setLayoutParams(layout);
            y_up = (float) (vh_origin - vh) / 2f;
            y_down = y_up + vh;
            x_left = (float) (vw_origin - vw) / 2f;
            x_right = x_left + vw;

            mask.setOnTouchListener(new TouchListener(10, 1000000) {
                @Override
                protected void onDrag(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double drag_distance, double distance_from_start, double distance_total) {
                    if (isEnd() && !isNext()){
                        setEnd(false);
                        float x_offset = end.x - start.x;
                        float y_offset = end.y - start.y;
                        float xl = x_left_scale + x_offset;
                        float xr = x_right_scale + x_offset;
                        float yu = y_up_scale + y_offset;
                        float yd = y_down_scale + y_offset;
                        if (xl > x_left){
                            float change = x_left - xl;
                            xl += change;
                            xr += change;
                            x_offset += change;
                        } else if (xr < x_right){
                            float change = x_right - xr;
                            xl += change;
                            xr += change;
                            x_offset += change;
                        }
                        if (yu > y_up){
                            float change = y_up - yu;
                            yu += change;
                            yd += change;
                            y_offset += change;
                        } else if (yd < y_down){
                            float change = y_down - yd;
                            yu += change;
                            yd += change;
                            y_offset += change;
                        }
                        if (Math.abs(x_offset) <= 1e-1f && Math.abs(y_offset) <= 1e-1f){
                            setEnd(true);
                            return;
                        }
                        com.telephone.coursetable.LogMe.LogMe.i("drag info", " xl = " + xl + " xr = " + xr + " yu = " + yu + " yd = " + yd);
                        x_left_scale = xl;
                        x_right_scale = xr;
                        y_up_scale = yu;
                        y_down_scale = yd;
                        imv.setTranslationX(imv.getTranslationX() + x_offset);
                        imv.setTranslationY(imv.getTranslationY() + y_offset);
                        imv.post(()->setEnd(true));
                    }
                }

                @Override
                protected void onDragEnd(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double distance_from_start, double drag_total_distance) {

                }

                @Override
                protected void onClick(Coordinate pos, Coordinate pos_raw) {
                    if (!isEnd()){
                        return;
                    }
                    setEnd(false);
                    if (isNext()){
                        int h = imv.getHeight();
                        int w = imv.getWidth();
                        if (pos.y >= y_down || pos.y <= y_up || pos.x >= x_right || pos.x <= x_left){
                            setEnd(true);
                            return;
                        }
                        float px = pos.x - x_left;
                        float py = pos.y - y_up;
                        imv.setPivotX(px);
                        imv.setPivotY(py);
                        x_left_scale = pos.x - (Math.abs(px) * 3f);
                        x_right_scale = x_left_scale + (w * 3f);
                        y_up_scale = pos.y - (Math.abs(py) * 3f);
                        y_down_scale = y_up_scale + (h * 3f);
                        imv.animate().scaleX(3f).scaleY(3f).withEndAction(()->{
                            setNext(false);
                            setEnd(true);
                        });
                    }else {
                        imv.animate().scaleX(1f).scaleY(1f).translationX(0f).translationY(0f).withEndAction(()->{
                            setNext(true);
                            setEnd(true);
                        });
                    }
                }

                @Override
                protected void onLongClickEnd(Coordinate pos, Coordinate pos_raw, long click_total_millis) {

                }

                @Override
                protected void onLongPressDetected(Coordinate pos, Coordinate pos_raw, long detected_time_millis) {

                }

                @Override
                protected void onLongPressThenDragEnd(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double distance_from_start, double drag_total_distance, long total_time_millis, long long_press_total_time_millis, long drag_total_time_millis) {

                }

                @Override
                protected float getRawX(MotionEvent e, int pointer_index) {
                    return 0;
                }

                @Override
                protected float getRawY(MotionEvent e, int pointer_index) {
                    return 0;
                }
            });

        });
    }
}
