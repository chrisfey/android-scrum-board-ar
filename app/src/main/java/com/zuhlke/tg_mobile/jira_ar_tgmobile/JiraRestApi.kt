package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.util.Base64
import android.util.Log
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * Created by lewa on 23/06/2016.
 */

class JiraRestApi(private val host : String) {

    val jiraApi: JiraApiInterface
    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        jiraApi = retrofit.create(JiraApiInterface::class.java)
    }

    fun login(auth: String): Boolean {
        val response = jiraApi.getCurrentUser(auth).execute()
        return response.code() == HttpURLConnection.HTTP_OK
    }

    fun getProjects(auth: String): Call<List<JiraProjectResponse>> {
        return jiraApi.getProjects(auth)
    }


}