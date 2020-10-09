package com.telephone.coursetable.GuetTools;

public abstract class ActionInformation {

    public enum Action{
        CLICK, LONG_CLICK, DRAG, LONG_PRESS_THEN_DRAG
    }

    public float down_x;
    public float down_y;
    public float down_raw_x;
    public float down_raw_y;
    public long down_ts = -1;
    public double move_distance = 0d;
    public float move_last_end_x;
    public float move_last_end_y;
    public float move_last_end_raw_x;
    public float move_last_end_raw_y;
    public float up_x;
    public float up_y;
    public float up_raw_x;
    public float up_raw_y;
    private double min_move_distance_limit;

    private volatile long start_move_ts = -1;
    private volatile boolean mLongPress = false;
    private volatile long long_press_detected_ts = -1;
    private volatile long up_ts = -1;

    public synchronized long getUp_ts() {
        return up_ts;
    }

    private synchronized void setUp_ts(long up_ts) {
        this.up_ts = up_ts;
    }

    public synchronized long getStart_move_ts() {
        return start_move_ts;
    }

    private synchronized void setStart_move_ts(long start_move_ts) {
        this.start_move_ts = start_move_ts;
    }

    private synchronized boolean isLongPress() {
        return mLongPress;
    }

    private synchronized void setLongPress() {
        this.mLongPress = true;
    }

    public synchronized long getLong_press_detected_ts() {
        return long_press_detected_ts;
    }

    private synchronized void setLong_press_detected_ts(long long_press_detected_ts) {
        this.long_press_detected_ts = long_press_detected_ts;
    }

    protected abstract void onLongPressDetected(Coordinate pos, Coordinate pos_raw, long detected_time_millis);

    public ActionInformation(float down_x, float down_y, float down_raw_x, float down_raw_y, long down_ts, double min_move_distance_limit, long min_long_click_trigger_millis) {
        this.down_x = down_x;
        this.down_y = down_y;
        this.down_raw_x = down_raw_x;
        this.down_raw_y = down_raw_y;
        this.down_ts = down_ts;

        this.move_last_end_x = down_x;
        this.move_last_end_y = down_y;
        this.move_last_end_raw_x = down_raw_x;
        this.move_last_end_raw_y = down_raw_y;

        this.min_move_distance_limit = min_move_distance_limit;

        new Thread(()->{
            try {
                Thread.sleep(min_long_click_trigger_millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (getStart_move_ts() == -1 && getUp_ts() == -1){
                long dts = System.currentTimeMillis();
                setLongPress();
                setLong_press_detected_ts(dts);
                onLongPressDetected(new Coordinate(down_x, down_y), new Coordinate(down_raw_x, down_raw_y), dts);
            }
        }).start();
    }

    public boolean move(float x, float y, float rx, float ry, long event_time_ts){
        float xabs = Math.abs(x - move_last_end_x);
        float yabs = Math.abs(y - move_last_end_y);
        double distance = Math.sqrt(Math.pow(xabs, 2) + Math.pow(yabs, 2));

        if (distance > min_move_distance_limit) {

            move_last_end_x = x;
            move_last_end_y = y;
            move_last_end_raw_x = rx;
            move_last_end_raw_y = ry;
            move_distance += distance;
            if (getStart_move_ts() == -1){
                setStart_move_ts(event_time_ts);
            }

            return true;
        }
        return false;
    }

    public Action up(float x, float y, float rx, float ry, long up_ts){
        this.up_x = x;
        this.up_y = y;
        this.up_raw_x = rx;
        this.up_raw_y = ry;
        setUp_ts(up_ts);

        if (move_distance > 0){
            if (isLongPress()){
                return Action.LONG_PRESS_THEN_DRAG;
            }else {
                return Action.DRAG;
            }
        }else {
            if (isLongPress()){
                return Action.LONG_CLICK;
            }else {
                return Action.CLICK;
            }
        }
    }

}
