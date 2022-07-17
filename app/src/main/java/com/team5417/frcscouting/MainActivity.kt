package com.team5417.frcscouting

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureStartBtn()
    }

    private fun configureStartBtn() {
        val startBtn : Button = findViewById(R.id.btnStart);
        startBtn.setOnClickListener {
            startActivity(Intent(this, ScoutingActivity::class.java))
        }
    }

}