package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.content.Context
import android.os.Bundle
import android.os.Environment
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*

class JiraListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        val lv = findViewById(R.id.list) as ListView
        lv.adapter = ListExampleAdapter(this)
        lv.onItemClickListener = AdapterView.OnItemClickListener {
            adapterView, view, i, l ->
            Snackbar.make(view, "You clicked ListItem: " + i + ", id: " + l, Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }



    private class ListExampleAdapter(context: Context) : BaseAdapter() {

//        val path = Environment.getRootDirectory().absolutePath + "/issuesForProjectJson.json";
//        val file = File("/Users/sabu/Coding/tg-mobile-android-app/issuesForProjectJson.json")
//        val input = FileInputStream(file)
//        val jsonString = InputStreamReader(input)
//        val buf = BufferedReader(jsonString)
//        val sb = stringBuilder(buf)
//
//        private fun stringBuilder(buf: BufferedReader): StringBuilder {
//            var sb = StringBuilder()
//            var line = buf.readLine()
//            while (line != null){
//                sb.append(line + "\n");
//                var line = buf.readLine()
//            }
//            return sb
//        }


        fun getJSONresponse(): String = "{\n" +
                "    \"expand\": \"schema,names\",\n" +
                "    \"startAt\": 0,\n" +
                "    \"maxResults\": 50,\n" +
                "    \"total\": 15,\n" +
                "    \"issues\": [\n" +
                "        {\n" +
                "            \"expand\": \"operations,versionedRepresentations,editmeta,changelog,renderedFields\",\n" +
                "            \"id\": \"10209\",\n" +
                "            \"self\": \"https://tgmobile.atlassian.net/rest/api/2/issue/10209\",\n" +
                "            \"key\": \"JIR-18\",\n" +
                "            \"fields\": {\n" +
                "                \"summary\": \"Card recognition\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"expand\": \"operations,versionedRepresentations,editmeta,changelog,renderedFields\",\n" +
                "            \"id\": \"102309\",\n" +
                "            \"self\": \"https://tgmobile.atlassian.net/rest/api/2/issue/10209\",\n" +
                "            \"key\": \"JIR-3458\",\n" +
                "            \"fields\": {\n" +
                "                \"summary\": \"3 recognition\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"expand\": \"operations,versionedRepresentations,editmeta,changelog,renderedFields\",\n" +
                "            \"id\": \"10223409\",\n" +
                "            \"self\": \"https://tgmobile.atlassian.net/rest/api/2/issue/10209\",\n" +
                "            \"key\": \"JIR-125\",\n" +
                "            \"fields\": {\n" +
                "                \"summary\": \"235 recognition\"\n" +
                "            }\n" +
                "        },\n" +
                "    ]\n" +
                "}"


        //        val obj = JSONObject(sb.toString().trim())
        val obj = JSONObject(getJSONresponse())
        var issuesArray = obj.getJSONArray("issues")
        val idsArray = setUpIdsArray(issuesArray)

        private fun setUpIdsArray(issuesArray: JSONArray?): ArrayList<Long> {
            val arr =  arrayListOf<Long>()
            var i = 0;
            while (i < issuesArray!!.length() -1 ) {
                arr.add((issuesArray!!.get(i) as JSONObject).getLong("id"))
                i++
            }
            return arr
        }

        val descArray = setUpDescArray(issuesArray)

        private fun setUpDescArray(issuesArray: JSONArray?): ArrayList<String> {
            val arr =  arrayListOf<String>()
            var i = 0;
            while (i < issuesArray!!.length() -1 ) {
                arr.add((issuesArray!!.get(i) as JSONObject).getString("key"))
                i++
            }
            return arr
        }

        val summaryArray = setUpSumArray(issuesArray)

        private fun setUpSumArray(issuesArray: JSONArray?): ArrayList<String> {
            val arr =  arrayListOf<String>()
            var i = 0;
            while (i < issuesArray!!.length() -1 ) {
                arr.add((issuesArray!!.get(i) as JSONObject).getJSONObject("fields").getString("summary"))
                i++
            }
            return arr
        }


        internal var sList = arrayOf("One", "Two", "Three", "Four", "Calm", "Down", "You",
                "Well", "Respected", "Human", "Eleven", "Twelve", "Thirteen")
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return issuesArray!!.length() -1
//            return sList.size
        }

        override fun getItem(position: Int): Any {
            return descArray!!.get(position) as Any
//            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return idsArray!!.get(position)
//            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.list_row, parent, false)
                vh = ListRowHolder(view)
                view!!.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.label.text = descArray!!.get(position) + ": " + summaryArray!!.get(position)
//            vh.label.text = "blah"
            return view
        }


    }


    private class ListRowHolder(row: View?) {
        public val label: TextView

        init {
            this.label = row?.findViewById(R.id.label) as TextView
        }

    }

}
