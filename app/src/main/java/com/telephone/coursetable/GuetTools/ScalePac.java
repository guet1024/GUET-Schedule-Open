package com.telephone.coursetable.GuetTools;

public class ScalePac {
    public float tx_left_up;
    public float ty_left_up;
    public float tx_right_up;
    public float ty_right_up;
    public float tx_left_down;
    public float ty_left_down;
    public float tx_right_down;
    public float ty_right_down;
    public float tx_focus;
    public float ty_focus;
    public float up_y_max;
    public float down_y_min;
    public float left_x_max;
    public float right_x_min;
    public float scale;

    public ScalePac(float tx_left_up, float ty_left_up, float tx_right_up, float ty_right_up, float tx_left_down, float ty_left_down, float tx_right_down, float ty_right_down, float tx_focus, float ty_focus, float up_y_max, float down_y_min, float left_x_max, float right_x_min, float scale) {
        this.tx_left_up = tx_left_up;
        this.ty_left_up = ty_left_up;
        this.tx_right_up = tx_right_up;
        this.ty_right_up = ty_right_up;
        this.tx_left_down = tx_left_down;
        this.ty_left_down = ty_left_down;
        this.tx_right_down = tx_right_down;
        this.ty_right_down = ty_right_down;
        this.tx_focus = tx_focus;
        this.ty_focus = ty_focus;
        this.up_y_max = up_y_max;
        this.down_y_min = down_y_min;
        this.left_x_max = left_x_max;
        this.right_x_min = right_x_min;
        this.scale = scale;
    }
}
