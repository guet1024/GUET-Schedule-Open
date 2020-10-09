package com.telephone.coursetable.GuetTools;

public class Scale {

    public static ScalePac offset(ScalePac origin, float x_offset, float y_offset){
        origin.tx_left_up += x_offset;
        origin.tx_right_up += x_offset;
        origin.tx_right_down += x_offset;
        origin.tx_left_down += x_offset;
        origin.ty_left_up += y_offset;
        origin.ty_right_up += y_offset;
        origin.ty_right_down += y_offset;
        origin.ty_left_down += y_offset;
        origin.tx_focus += x_offset;
        origin.ty_focus += y_offset;
        return origin;
    }

    public static ScalePac scale(ScalePac origin) {
        if (
                origin.tx_focus < origin.tx_left_up ||
                        origin.tx_focus > origin.tx_right_up ||
                        origin.ty_focus < origin.ty_left_up ||
                        origin.ty_focus > origin.ty_left_down
        ){
            return null;
        }
        float now_center_image_left = Math.abs(origin.tx_left_up - origin.tx_focus);
        float after_center_image_left = now_center_image_left * origin.scale;
        float now_center_image_top = Math.abs(origin.ty_left_up - origin.ty_focus);
        float after_center_image_top = now_center_image_top * origin.scale;
        float now_width = Math.abs(origin.tx_left_up - origin.tx_right_up);
        float now_height = Math.abs(origin.ty_left_up - origin.ty_left_down);
        float after_width = now_width * origin.scale;
        float after_height = now_height * origin.scale;
        origin.tx_left_up = origin.tx_focus - after_center_image_left;
        origin.ty_left_up = origin.ty_focus - after_center_image_top;
        origin.tx_right_up = origin.tx_left_up + after_width;
        origin.ty_right_up = origin.ty_left_up;
        origin.tx_right_down = origin.tx_right_up;
        origin.ty_right_down = origin.ty_right_up + after_height;
        origin.tx_left_down = origin.tx_left_up;
        origin.ty_left_down = origin.ty_right_down;
        float y_offset = 0;
        float x_offset = 0;
        if (origin.ty_left_up > origin.up_y_max){
            y_offset = origin.up_y_max - origin.ty_left_up;
        }
        if (origin.ty_left_down < origin.down_y_min){
            y_offset = origin.down_y_min - origin.ty_left_down;
        }
        if (origin.tx_left_up > origin.left_x_max){
            x_offset = origin.left_x_max - origin.tx_left_up;
        }
        if (origin.tx_right_up < origin.right_x_min){
            x_offset = origin.right_x_min - origin.tx_right_up;
        }
        offset(origin, x_offset, y_offset);
        return origin;
    }
}
