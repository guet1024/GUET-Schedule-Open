package com.telephone.coursetable.GradePoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Get_grade_points_array {
    List<Map.Entry<String, String>> grade_points_array = new LinkedList<>();
    String message = null;

    public Get_grade_points_array(List<Map.Entry<String, String>> grade_points_array, String message) {
        this.grade_points_array = grade_points_array;
        this.message = message;
    }

    public Get_grade_points_array(String message) {
        this.message = message;
    }

    public Get_grade_points_array(List<Map.Entry<String, String>> grade_points_array) {
        this.grade_points_array = grade_points_array;
    }
}
