package com.telephone.coursetable;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @clear
 */
public class PickerPanel {
    public ImageView im;
    public NumberPicker p1;
    public NumberPicker p2;
    public TextView t1;
    public TextView t2;
    public FloatingActionButton btn;

    public PickerPanel(ImageView im, NumberPicker p1, NumberPicker p2, TextView t1, TextView t2, FloatingActionButton btn) {
        this.im = im;
        this.p1 = p1;
        this.p2 = p2;
        this.t1 = t1;
        this.t2 = t2;
        this.btn = btn;
    }

    public void hide(Activity c){
        c.runOnUiThread(() -> {
            im.setVisibility(View.INVISIBLE);
            p1.setVisibility(View.INVISIBLE);
            p2.setVisibility(View.INVISIBLE);
            t1.setVisibility(View.INVISIBLE);
            t2.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
        });
    }

    public void show(Activity c){
        c.runOnUiThread(() -> {
            im.setVisibility(View.VISIBLE);
            p1.setVisibility(View.VISIBLE);
            p2.setVisibility(View.VISIBLE);
            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
        });
    }

    public boolean isShown(){
        return (im.getVisibility() == View.VISIBLE);
    }
}
