package com.team5417.frcscouting

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team5417.frcscouting.recyclerview.ScoutingAdapter

class ScoutingActivity : AppCompatActivity() {

    private val dataAdapter: ScoutingAdapter by lazy {
        ScoutingAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scouting)

        dataAdapter.setData(getData())
        findViewById<RecyclerView>(R.id.mainView)
            .apply {
                layoutManager = LinearLayoutManager(this@ScoutingActivity)
                hasFixedSize()
                this.adapter = dataAdapter
            }

        configureBackBtn()
    }

    private fun configureBackBtn() {
        val backBtn : Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            finish();
        }
    }

    private fun getData(): List<DataModel> = listOf(
        DataModel.Header(
            title = "Autonomous Period"
        ),
        DataModel.Checkbox(
            title = "Robot left TARMAC:",
            value = false
        ),
        DataModel.Number(
            title = "Cargo Scored in Top:",
            value = 0
        ),
        DataModel.Number(
            title = "Cargo Scored in Bottom:",
            value = 0
        ),
        DataModel.Header(
            title = "TeleOP Period"
        ),
        DataModel.Number(
            title = "Cargo Scored in Top:",
            value = 0
        ),
        DataModel.Number(
            title = "Cargo Scored in Bottom:",
            value = 0
        ),
        DataModel.Slider(
            title = "Driver Competence",
            value = 0.0f,
            max = 5.0f
        ),
        DataModel.Slider(
            title = "Defensive",
            value = 0.0f,
            max = 5.0f
        ),
        DataModel.Text(
            title = "Notes:",
            value = ""
        )
    )
}