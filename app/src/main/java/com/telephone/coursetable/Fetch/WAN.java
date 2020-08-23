package com.telephone.coursetable.Fetch;

import android.content.Context;
import android.content.res.Resources;

import com.telephone.coursetable.Https.GetBitmap;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.R;

public class WAN {
    /**
     * @return
     * - obj != null : success
     * - obj == null : fail
     * @clear
     */
    public static HttpConnectionAndCode checkcode(Context c, String cookie){
        Resources r = c.getResources();
        return GetBitmap.get(
                r.getString(R.string.wan_get_check_code_url),
                null,
                r.getString(R.string.user_agent),
                r.getString(R.string.wan_get_check_code_referer),
                cookie,
                r.getString(R.string.cookie_delimiter)
        );
    }
}
