package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.zuhlke.tg_mobile.jira_ar_tgmobile.JiraClient.JiraItem

class JiraListActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


         val jiraItems = JiraClient().items()

        setContentView(R.layout.activity_list_view)

        val lv = findViewById(R.id.list) as ListView
        lv.adapter = ListExampleAdapter(LayoutInflater.from(this), jiraItems)
        lv.onItemClickListener = AdapterView.OnItemClickListener {
            adapterView, view, i, l ->
            Snackbar.make(view, "You clicked ListItem: " + i + ", id: " + l, Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

    }


    private class ListExampleAdapter constructor(private val inflator: LayoutInflater, private val jiraItems: List<JiraItem>) : BaseAdapter() {
//        operator fun JSONArray.iterator(): Iterator<JSONObject>
//                = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
//
//
//        // Temporary stub value

//
//
//        private val issues = JSONObject(jsonResponseFromJira()).getJSONArray("issues")
//        val idsArray = setUpIdsArray(issues)
//
//        private fun setUpIdsArray(issuesArray: JSONArray): Sequence<Long> {
//            return issuesArray.iterator().asSequence().map { issue -> issue.getLong("id")  }
//        }
//
//        val descArray = setUpDescArray(issues)
//
//        private fun setUpDescArray(issuesArray: JSONArray?): ArrayList<String> {
//            val arr =  arrayListOf<String>()
//            var i = 0;
//            while (i < issuesArray!!.length() -1 ) {
//                arr.add((issuesArray!!.get(i) as JSONObject).getString("key"))
//                i++
//            }
//            return arr
//        }
//
//        val summaryArray = setUpSumArray(issues)
//
//        private fun setUpSumArray(issuesArray: JSONArray?): ArrayList<String> {
//            val arr =  arrayListOf<String>()
//            var i = 0;
//            while (i < issuesArray!!.length() -1 ) {
//                arr.add((issuesArray!!.get(i) as JSONObject).getJSONObject("fields").getString("summary"))
//                i++
//            }
//            return arr
//        }

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
