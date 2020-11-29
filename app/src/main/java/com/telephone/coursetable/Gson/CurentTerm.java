package com.telephone.coursetable.Gson;

import com.google.gson.Gson;
import com.telephone.coursetable.MyApp;

import java.util.List;

public class CurentTerm {

    public static String[] getCurentTerm(String s){
        return MyApp.gson.fromJson(s,String[].class);
    }
}
