package com.zuhlke.tg_mobile.jira_ar_tgmobile

import org.json.JSONArray
import org.json.JSONObject

class JiraClient {
    data class JiraItem(val id: Long, val key: String, val summary: String) {
        fun viewText() = key + ": " + summary
    }

    operator fun JSONArray.iterator(): Iterator<JSONObject>
            = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

    fun items(): List<JiraItem> = JSONObject(jsonResponseFromJira)
            .getJSONArray("issues")
            .iterator()
            .asSequence()
            .map { issue ->
                val id = issue.getLong("id")
                val key = issue.getString("key")
                val summary = issue.getJSONObject("fields").getString("summary")
                JiraItem(id, key, summary)
            }
            .toList()

    val jsonResponseFromJira: String = """
            {
                "expand": "schema,names",
                "startAt": 0,
                "maxResults": 50,
                "total": 15,
                "issues": [
                    {
                        "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
                        "id": "10209",
                        "self": "https://tgmobile.atlassian.net/rest/api/2/issue/10209",
                        "key": "JIR-18",
                        "fields": {
                            "summary": "Card recognition"
                        }
                    },
                    {
                        "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
                        "id": "102309",
                        "self": "https://tgmobile.atlassian.net/rest/api/2/issue/10209",
                        "key": "JIR-3458",
                        "fields": {
                            "summary": "3 recognition"
                        }
                    },
                    {
                        "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
                        "id": "10223409",
                        "self": "https://tgmobile.atlassian.net/rest/api/2/issue/10209",
                        "key": "JIR-125",
                        "fields": {
                            "summary": "235 recognition"
                        }
                    }
                ]
            }
            """
}