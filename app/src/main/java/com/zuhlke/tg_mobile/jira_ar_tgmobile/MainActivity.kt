package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity()  {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//        val toolbar = findViewById(R.id.toolbar) as Toolbar
//        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            view -> startActivity(Intent(applicationContext, JiraListActivity::class.java));
        }
        val fab2 = findViewById(R.id.fab2) as FloatingActionButton
        //I cant compile with any of the vuforia stuff, need to include the .jar?
//        fab2.setOnClickListener {
//            view ->
//            val i = Intent(applicationContext, VuforiaTargets::class.java)
//            startActivity(i);
//        }
        val fab3 = findViewById(R.id.fab3) as FloatingActionButton
        fab3.setOnClickListener {
            view -> startActivity(Intent(applicationContext, PostItScannerActivity::class.java));
        }
    }








}
