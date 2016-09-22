package com.zuhlke.tg_mobile.jira_ar_tgmobile.jira

import android.util.Log
import com.zuhlke.tg_mobile.jira_ar_tgmobile.App
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

/**
 * Created by lewa on 23/06/2016.
 */

class JiraRestApi(private val host: String = "https://jira.atlassian.com") {

    val LOGTAG = App.LOGTAG + "JiraRestApi"
    operator fun JSONArray.iterator(): Iterator<JSONObject>
            = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

    val jiraApi: JiraApiInterface

    init {
        Log.d(LOGTAG, "jirarestapi init")
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
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

    fun getIssues(): Call<JiraIssues> {
        Log.d(LOGTAG, "getIssues")
        return jiraApi.getIssues()
    }



}





