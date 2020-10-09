package com.telephone.coursetable.GuetTools;

public class Drag {
    public static ScalePac drag(ScalePac origin, float x_offset, float y_offset){
        Scale.offset(origin, x_offset, y_offset);
        if (origin.ty_left_up > origin.up_y_max){
            return null;
        }
        if (origin.ty_left_down < origin.down_y_min){
            return null;
        }
        if (origin.tx_left_up > origin.left_x_max){
            return null;
        }
        if (origin.tx_right_up < origin.right_x_min){
            return null;
        }
        return origin;
    }
}
