package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.app.Application

/**
 * Created by lewa on 23/06/2016.
 */

class App: Application() {
    private var jiraManager = JiraApiManager("https://jira.atlassian.com/")

    fun getManager(host: String) : JiraApiManager {
        jiraManager = JiraApiManager(host)
        return jiraManager
    }

    fun getManager() : JiraApiManager {
        return jiraManager
    }
}