package com.team5417.frcscouting

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ScoutingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scouting)

        configureBackBtn()
    }

    private fun configureBackBtn() {
        val backBtn : Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            finish();
        }
    }
}