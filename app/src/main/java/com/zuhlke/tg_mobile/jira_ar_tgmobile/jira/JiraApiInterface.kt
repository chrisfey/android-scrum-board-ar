package com.zuhlke.tg_mobile.jira_ar_tgmobile.jira


import android.graphics.Bitmap
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface  JiraApiInterface {

    @GET("/rest/auth/1/session")
    fun getCurrentUser(@Header("Authorization") authorization: String) : Call<JiraSessionResponse>;

    @GET("/rest/api/2/project")
    fun getProjects(@Header("Authorization") authorization: String) : Call<List<JiraProjectResponse>>;

    @GET("/rest/api/2/search") // can add jql query ie ?jql=assignee=chris
    fun getIssues() : Call<JiraIssues>;

}
