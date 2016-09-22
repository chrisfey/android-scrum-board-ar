package com.zuhlke.tg_mobile.jira_ar_tgmobile.jira

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.zuhlke.tg_mobile.jira_ar_tgmobile.App
import com.zuhlke.tg_mobile.jira_ar_tgmobile.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL

class JiraListActivity : AppCompatActivity(), Callback<JiraIssues> {



    var avatar : Bitmap? = null
    fun getBitmapFromURL(src: String) {
        GetAvatar().execute(src)
    }

    inner class GetAvatar : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String?): Void? {
            try {
                val url = URL(params[0])
                val connection = url.openConnection()
                connection.setDoInput(true);
                connection.connect();
                val input = connection.getInputStream();
                avatar = BitmapFactory.decodeStream(input);
                Log.d(App.LOGTAG, "Got bitmap")
            } catch (e: IOException) {
                throw RuntimeException("could not get avatar image")
            }
            return null
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }

    override fun onFailure(call: Call<JiraIssues>?, t: Throwable?) {
        throw RuntimeException("Failure getting jira issues")
    }

    override fun onResponse(call: Call<JiraIssues>?, response: Response<JiraIssues>?) {
        Log.d("JIRA", "ASDfasdf")
        val jiraIssues =response?.body()

        lv!!.adapter = ListExampleAdapter(LayoutInflater.from(this), jiraIssues?.items()!!)
        lv!!.onItemClickListener = AdapterView.OnItemClickListener {
            adapterView, view, i, l ->
            Snackbar.make(view, "You clicked ListItem: " + i + ", id: " + l, Snackbar.LENGTH_LONG).setAction("Action", null).show()
            setContentView(R.layout.activity_list_view)
        }
    }

    private var lv: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        JiraRestApi().getIssues().enqueue(this)
        setContentView(R.layout.activity_list_view)

        lv = findViewById(R.id.list) as ListView

        getBitmapFromURL("https://jira.atlassian.com/secure/useravatar?avatarId=10612")
    }


    private class ListExampleAdapter constructor(private val inflator: LayoutInflater, private val jiraItems: List<JiraItem>) : BaseAdapter() {

        override fun getCount(): Int {
            return jiraItems.size
        }

        override fun getItem(position: Int): Any {
            return jiraItems[position].key
        }

        override fun getItemId(position: Int): Long {
            return jiraItems[position].id
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view = convertView ?: newView(parent)

            val jiraItem = jiraItems[position]
            val vh = view.tag as ListRowHolder
            vh.label.text = jiraItem.viewText()
            return view
        }

        fun newView(parent: ViewGroup) : View {
            val view = inflator.inflate(R.layout.list_row, parent, false)
            view.tag = ListRowHolder(view.findViewById(R.id.label) as TextView)
            return view
        }
    }


    private class ListRowHolder(val label: TextView) {
    }
}
