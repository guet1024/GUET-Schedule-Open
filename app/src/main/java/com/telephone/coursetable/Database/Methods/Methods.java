package com.telephone.coursetable.Database.Methods;

import androidx.annotation.NonNull;

import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.Key.GoToClassKey;
import com.telephone.coursetable.Database.KeyAndValue.GoToClassKeyAndValue;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Methods {
    /**
     * traverse the {@link com.telephone.coursetable.Database.GoToClass} database and
     * {@link com.telephone.coursetable.Database.ClassInfo} database using specified username, delete
     * the cno in {@link com.telephone.coursetable.Database.ClassInfo} those are not referred by
     * {@link com.telephone.coursetable.Database.GoToClass} database
     * @param u the username
     * @param gdao the {@link GoToClassDao}
     * @param cdao the {@link ClassInfoDao}
     * @clear
     */
    public static void refreshAndDeleteNotReferredClassInfo(@NonNull String u, @NonNull GoToClassDao gdao, @NonNull ClassInfoDao cdao){
        List<String> cnoList = gdao.selectAllCno(u);
        List<String> cnoListClassInfo = cdao.selectAllCno(u);
        for(String ccno : cnoListClassInfo){
            if (!cnoList.contains(ccno)){
                cdao.deleteCno(u, ccno);
            }
        }
    }

    public static HashMap<GoToClassKey, String> getMyCommentMap(@NonNull GoToClassDao gdao, @NonNull ClassInfoDao cdao){
        List<GoToClassKeyAndValue> my_cmt_list = gdao.getMyCommentPairs_all_users();
        HashMap<GoToClassKey, String> my_cmt_map = new HashMap<>();
        for(GoToClassKeyAndValue pair : my_cmt_list){
            my_cmt_map.put(pair.getKey(), pair.getValue());
        }
        return my_cmt_map;
    }

    public static <KT, VT> Map.Entry<KT, VT> entry(KT k, VT v){
        return new AbstractMap.SimpleEntry<>(k, v);
    }
}
