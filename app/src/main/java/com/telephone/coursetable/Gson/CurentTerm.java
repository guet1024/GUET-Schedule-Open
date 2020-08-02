package com.telephone.coursetable.Gson;

import com.google.gson.Gson;

import java.util.List;

public class CurentTerm {

    public static String[] getCurentTerm(String s){
        return new Gson().fromJson(s,String[].class);
    }
}
