package com.telephone.coursetable;

import android.content.Context;

import com.telephone.coursetable.Database.PersonInfoDao;

public class DATALIST {
    /**
     * @gson
     *      - {@link com.telephone.coursetable.Gson.PersonInfo}
     *      - {@link com.telephone.coursetable.Gson.StudentInfo}
     * @fetch
     *      - {@link com.telephone.coursetable.Fetch.LAN#personInfo(Context, String)}
     *      - {@link com.telephone.coursetable.Fetch.LAN#studentInfo(Context, String)}
     * @database
     *      {@link com.telephone.coursetable.Database.PersonInfo}
     * @merge
     *      {@link com.telephone.coursetable.Merge.Merge#personInfo(String, String, PersonInfoDao)}
     * @clear
     */
    public final String PersonInfo = null;
}
