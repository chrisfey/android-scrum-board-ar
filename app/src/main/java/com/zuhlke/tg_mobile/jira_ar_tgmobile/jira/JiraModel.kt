package com.zuhlke.tg_mobile.jira_ar_tgmobile.jira

import org.json.JSONObject


data class JiraSessionResponse(val name: String)

data class JiraProjectResponse(val id: String, val key: String, val name: String);


data class JiraIssues(val issues: List<Map<String,Any>>) {

    fun items(): List<JiraItem> {
        return issues.iterator()
                .asSequence()
                .map { issue ->
                    val jsonIssue = JSONObject(issue)
                    val id = jsonIssue.getLong("id")
                    val key = jsonIssue.getString("key")
                    val summary = jsonIssue.getJSONObject("fields").getString("summary")
                    val priority = jsonIssue.getJSONObject("fields").getJSONObject("priority").getString("name")
                    val issueType = jsonIssue.getJSONObject("fields").getJSONObject("issuetype").getString("name")
                    JiraItem(id, key, summary, priority, issueType)
                }
                .toList()
    }
}

data class JiraItem(val id: Long,
                    val key: String,
                    val summary: String,
                    val priority :String,
                    val issueType :String) {
    fun viewText() = key + ": " + summary
}