package com.team5417.frcscouting

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
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
            val models = dataAdapter.getData()
            var isMatchNum = false;
            var isTeamNum = false;
            var dataToSend = ""
            for (model in models) {
                val toAdd = when (model) {
                    is DataModel.Number -> model.id+"="+model.value.toString()
                    is DataModel.Checkbox -> model.id+"="+if (model.value) "1" else "0"
                    is DataModel.Text -> model.id+"="+model.value
                    is DataModel.Slider -> model.id+"="+model.value.toString()
                    is DataModel.MatchAndTeamNum -> {
                        if(model.matchNum == -1) break;
                        isMatchNum = true;
                        if(model.teamNum == -1) break;
                        isTeamNum = true;
                        "mn="+model.matchNum.toString()+",tn="+model.teamNum.toString()
                    }
                    else -> ""
                }

                if(toAdd != "") {
                    if(dataToSend == "") dataToSend += toAdd
                    else dataToSend += ",$toAdd"
                }
            }

            if(!isMatchNum) {
                Toast.makeText(this@ScoutingActivity, "No Match Number!", Toast.LENGTH_SHORT).show()
            } else if(!isTeamNum) {
                Toast.makeText(this@ScoutingActivity, "No Team Number!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, QRCodeActivity::class.java)
                intent.putExtra("data", dataToSend)
                startActivity(intent)
            }
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
            title = "Top Cargo:",
            value = 0
        ),
        DataModel.Number(
            id = "ab",
            title = "Bottom Cargo:",
            value = 0
        ),
        DataModel.Header(
            title = "TeleOP Period"
        ),
        DataModel.Number(
            id = "tt",
            title = "Top Cargo:",
            value = 0
        ),
        DataModel.Number(
            id = "tb",
            title = "Bottom Cargo:",
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