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
        val mainView = findViewById<RecyclerView>(R.id.mainView);
        mainView.apply {
                layoutManager = LinearLayoutManager(this@ScoutingActivity)
                hasFixedSize()
                this.adapter = dataAdapter
            }

        configureBtns()
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            finish();
        }

        val exportBtn : Button = findViewById(R.id.idBtnGenerateQR)
        exportBtn.setOnClickListener {
            startActivity(Intent(this, QRCodeActivity::class.java))
        }
    }

    private fun getData(): List<DataModel> = listOf(
        DataModel.MatchAndTeamNum(),
        DataModel.Header(
            title = "Autonomous Period"
        ),
        DataModel.Checkbox(
            id = "ta",
            title = "Robot left TARMAC:",
            value = false
        ),
        DataModel.Number(
            id = "at",
            title = "Cargo Scored in Top:",
            value = 0
        ),
        DataModel.Number(
            id = "ab",
            title = "Cargo Scored in Bottom:",
            value = 0
        ),
        DataModel.Header(
            title = "TeleOP Period"
        ),
        DataModel.Number(
            id = "tt",
            title = "Cargo Scored in Top:",
            value = 0
        ),
        DataModel.Number(
            id = "tb",
            title = "Cargo Scored in Bottom:",
            value = 0
        ),
        DataModel.Slider(
            id = "dc",
            title = "Driver Competence",
            value = 0.0f,
            max = 5.0f
        ),
        DataModel.Slider(
            id = "df",
            title = "Defensive",
            value = 0.0f,
            max = 5.0f
        ),
        DataModel.Text(
            id = "n",
            title = "Notes:",
            value = ""
        )
    )
}