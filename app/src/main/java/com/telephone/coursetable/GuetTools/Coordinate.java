package com.telephone.coursetable.GuetTools;

public class Coordinate {
    public volatile float x;
    public volatile float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static double getDistance(float x1, float y1, float x2, float y2){
        float xabs = Math.abs(x1 - x2);
        float yabs = Math.abs(y1 - y2);
        return Math.sqrt(Math.pow(xabs, 2) + Math.pow(yabs, 2));
    }
}
