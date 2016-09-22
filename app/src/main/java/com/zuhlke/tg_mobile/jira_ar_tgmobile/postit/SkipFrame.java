package com.zuhlke.tg_mobile.jira_ar_tgmobile.postit;

import android.util.Log;

/**
 * Created by chfe on 20/09/2016.
 */
public class SkipFrame extends RuntimeException{

    public SkipFrame(String s) {
        super(s);
        Log.d("Skip Frame", s);
    }
}
