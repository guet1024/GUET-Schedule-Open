package com.telephone.coursetable.GuetTools;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class TouchListener implements View.OnTouchListener {

    private double min_move_distance_limit;
    private long min_long_click_trigger_millis;

    protected Map<Integer, ActionInformation> map;

    public TouchListener(double min_move_distance_limit, long min_long_click_trigger_millis){
        this.min_move_distance_limit = min_move_distance_limit;
        this.min_long_click_trigger_millis = min_long_click_trigger_millis;
        this.map = new HashMap<>();
    }

    protected abstract void onDrag(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double drag_distance, double distance_from_start, double distance_total);
    protected abstract void onDragEnd(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double distance_from_start, double drag_total_distance);
    protected abstract void onClick(Coordinate pos, Coordinate pos_raw);
    protected abstract void onLongClickEnd(Coordinate pos, Coordinate pos_raw, long click_total_millis);
    protected abstract void onLongPressDetected(Coordinate pos, Coordinate pos_raw, long detected_time_millis);
    protected abstract void onLongPressThenDragEnd(Coordinate start, Coordinate start_raw, Coordinate end, Coordinate end_raw, double distance_from_start, double drag_total_distance, long total_time_millis, long long_press_total_time_millis, long drag_total_time_millis);
    protected abstract float getRawX(MotionEvent e, int pointer_index);
    protected abstract float getRawY(MotionEvent e, int pointer_index);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pid = getPId(event);
        int pindex = event.findPointerIndex(pid);
        switch (event.getAction()){
            case MotionEvent.ACTION_POINTER_DOWN | 0x0100:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0200:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0300:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0400:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0500:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0600:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0700:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0800:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0900:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0A00:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0B00:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0C00:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0D00:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0E00:
            case MotionEvent.ACTION_POINTER_DOWN | 0x0F00:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                if (map.size() < 2 && !map.containsKey(pid)){
                    ActionInformation af = new ActionInformation(event.getX(pindex), event.getY(pindex), getRawX(event, pindex), getRawY(event, pindex), event.getEventTime(), min_move_distance_limit, min_long_click_trigger_millis) {
                        @Override
                        protected void onLongPressDetected(Coordinate pos, Coordinate pos_raw, long detected_time_millis) {
                            TouchListener.this.onLongPressDetected(pos, pos_raw, detected_time_millis);
                        }
                    };
                    map.put(pid, af);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP | 0x0100:
            case MotionEvent.ACTION_POINTER_UP | 0x0200:
            case MotionEvent.ACTION_POINTER_UP | 0x0300:
            case MotionEvent.ACTION_POINTER_UP | 0x0400:
            case MotionEvent.ACTION_POINTER_UP | 0x0500:
            case MotionEvent.ACTION_POINTER_UP | 0x0600:
            case MotionEvent.ACTION_POINTER_UP | 0x0700:
            case MotionEvent.ACTION_POINTER_UP | 0x0800:
            case MotionEvent.ACTION_POINTER_UP | 0x0900:
            case MotionEvent.ACTION_POINTER_UP | 0x0A00:
            case MotionEvent.ACTION_POINTER_UP | 0x0B00:
            case MotionEvent.ACTION_POINTER_UP | 0x0C00:
            case MotionEvent.ACTION_POINTER_UP | 0x0D00:
            case MotionEvent.ACTION_POINTER_UP | 0x0E00:
            case MotionEvent.ACTION_POINTER_UP | 0x0F00:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (map.containsKey(pid)){
                    ActionInformation pidaf = map.get(pid);
                    if (pidaf != null) {
                        switch (pidaf.up(event.getX(pindex), event.getY(pindex), getRawX(event, pindex), getRawY(event, pindex), event.getEventTime())){
                            case CLICK:
                                Coordinate pos = new Coordinate(pidaf.up_x, pidaf.up_y);
                                Coordinate pos_raw = new Coordinate(pidaf.up_raw_x, pidaf.up_raw_y);
                                onClick(pos, pos_raw);
                                break;
                            case DRAG:
                                Coordinate start = new Coordinate(pidaf.down_x, pidaf.down_y);
                                Coordinate start_raw = new Coordinate(pidaf.down_raw_x, pidaf.down_raw_y);
                                Coordinate end = new Coordinate(pidaf.up_x, pidaf.up_y);
                                Coordinate end_raw = new Coordinate(pidaf.up_raw_x, pidaf.up_raw_y);
                                onDragEnd(start, start_raw, end, end_raw, Coordinate.getDistance(start.x, start.y, end.x, end.y), pidaf.move_distance);
                                break;
                            case LONG_CLICK:
                                pos = new Coordinate(pidaf.up_x, pidaf.up_y);
                                pos_raw = new Coordinate(pidaf.up_raw_x, pidaf.up_raw_y);
                                onLongClickEnd(pos, pos_raw, pidaf.getUp_ts() - pidaf.down_ts);
                                break;
                            case LONG_PRESS_THEN_DRAG:
                                start = new Coordinate(pidaf.down_x, pidaf.down_y);
                                start_raw = new Coordinate(pidaf.down_raw_x, pidaf.down_raw_y);
                                end = new Coordinate(pidaf.up_x, pidaf.up_y);
                                end_raw = new Coordinate(pidaf.up_raw_x, pidaf.up_raw_y);
                                onLongPressThenDragEnd(start, start_raw, end, end_raw, Coordinate.getDistance(start.x, start.y, end.x, end.y), pidaf.move_distance, pidaf.getUp_ts() - pidaf.down_ts, pidaf.getStart_move_ts() - pidaf.down_ts, pidaf.getUp_ts() - pidaf.getStart_move_ts());
                                break;
                        }
                        map.remove(pid);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for (int pointer_id : map.keySet()){
                    int pointer_index = event.findPointerIndex(pointer_id);
                    ActionInformation pidaf = map.get(pointer_id);
                    if (pidaf != null){
                        Coordinate start = new Coordinate(pidaf.move_last_end_x, pidaf.move_last_end_y);
                        Coordinate start_raw = new Coordinate(pidaf.move_last_end_raw_x, pidaf.move_last_end_raw_y);
                        if (pidaf.move(event.getX(pointer_index), event.getY(pointer_index), getRawX(event, pointer_index), getRawY(event, pointer_index), event.getEventTime())){
                            Coordinate end = new Coordinate(pidaf.move_last_end_x, pidaf.move_last_end_y);
                            Coordinate end_raw = new Coordinate(pidaf.move_last_end_raw_x, pidaf.move_last_end_raw_y);
                            onDrag(start, start_raw, end, end_raw, Coordinate.getDistance(start.x, start.y, end.x, end.y), Coordinate.getDistance(pidaf.down_x, pidaf.down_y, end.x, end.y), pidaf.move_distance);
                        }
                    }
                }
                break;
        }
        return true;
    }

    private int getPId(MotionEvent e){
        return e.getPointerId(e.getActionIndex());
    }
}
