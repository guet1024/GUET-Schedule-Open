package com.telephone.coursetable.Webinfo;

public class Webinfoview {
    private String title;
    private int icon;

    public Webinfoview (String title,int icon){
        this.title = title;
        this.icon = icon;
    }
    public String getTitle(){
        return title;
    }
    public int getIcon(){
        return icon;
    }

}
