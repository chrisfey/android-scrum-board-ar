package com.zuhlke.tg_mobile.jira_ar_tgmobile

/**
 * Created by lewa on 23/06/2016.
 */

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface  JiraApiInterface {

    @GET("/rest/auth/1/session")
    fun getCurrentUser(@Header("Authorization") authorization: String) : Call<JiraSessionResponse>;
}
