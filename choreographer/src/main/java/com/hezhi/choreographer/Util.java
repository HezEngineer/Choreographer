package com.hezhi.choreographer;

import android.content.Context;

/**
 * Created by yf11 on 2017/3/29.
 */

public class Util {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        System.out.println("=======a.getDimensi    "+scale);

        return (int) (dpValue * scale + 0.5f);
    }

    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (pxValue / scale + 0.5f);
    }
}
