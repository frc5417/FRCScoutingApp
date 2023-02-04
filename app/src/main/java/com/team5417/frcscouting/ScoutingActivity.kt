package com.team5417.frcscouting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team5417.frcscouting.recyclerview.ScoutingAdapter
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ScoutingActivity : AppCompatActivity() {

    private var savedQRCodes = mutableListOf<String>();
    private val filename = "storageFile"
    private val savedFilename = "savedStorageFile"

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                savedQRCodes = data.getStringArrayListExtra("data") as MutableList<String>
            }
        }
    }

    private val dataAdapter: ScoutingAdapter by lazy {
        ScoutingAdapter(this)
    }

    private fun getSavedValues() {
        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, savedFilename).exists()) return
        savedQRCodes.clear()
        openFileInput(savedFilename).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    val matchNum = line.split(",").find { it.startsWith("mn=") }?.split("=")
                    if (matchNum != null) {
                        if(matchNum.size > 1 && matchNum[1] != "-1")
                            savedQRCodes.add(line)
                    };
                }
            } catch (e: NoSuchElementException) {}
        }
    }

    private fun getCachedValues() : List<DataModel> {
        val adapterData = getData()

        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, filename).exists()) return adapterData

        openFileInput(filename).bufferedReader().useLines { lines ->
            try {
                val line = lines.first();
                if(line != "") {
                    line.split(",").forEach { csv ->
                        if (csv.contains('=')) {
                            val id = csv.split("=")[0]
                            val value = csv.split("=")[1]
                            adapterData.forEach { model ->
                                when (model) {
                                    is DataModel.Number -> {
                                        if (model.id == id) {
                                            model.value = value.toInt()
                                        }
                                    }
                                    is DataModel.Checkbox -> {
                                        if (model.id == id) {
                                            model.value = value == "1"
                                        }
                                    }
                                    is DataModel.Text -> {
                                        if (model.id == id) {
                                            model.value = value
                                        }
                                    }
                                    is DataModel.Slider -> {
                                        if (model.id == id) {
                                            model.value = value.toFloat()
                                        }
                                    }
                                    is DataModel.MatchAndTeamNum -> {
                                        if (id == "mn") {
                                            model.matchNum = value.toInt()
                                        } else if (id == "tn") {
                                            model.teamNum = value.toInt()
                                        }
                                    }
                                    else -> ""
                                }
                            }
                        }
                    }
                }
            } catch (e: NoSuchElementException) {}
        }

        return adapterData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scouting)

        dataAdapter.setData(getCachedValues())
        getSavedValues()
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
                val fos: FileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
                fos.write("".toByteArray())
                fos.close()

                dataAdapter.setData(getCachedValues())

                savedQRCodes.add(dataToSend)

                val fosSaved: FileOutputStream = openFileOutput(savedFilename, Context.MODE_PRIVATE)
                fosSaved.write(savedQRCodes.joinToString("\n").toByteArray())
                fosSaved.close()

                val intent = Intent(this, QRCodeActivity::class.java)
                intent.putStringArrayListExtra("data", savedQRCodes as ArrayList<String>)
                resultLauncher.launch(intent)
            }
        }

        val viewBtn : Button = findViewById(R.id.idBtnViewQR)
        viewBtn.setOnClickListener {
            val intent = Intent(this, QRCodeActivity::class.java)
            intent.putStringArrayListExtra("data", savedQRCodes as ArrayList<String>)
            resultLauncher.launch(intent)
        }
    }

    private fun getData(): List<DataModel> = listOf(
        DataModel.MatchAndTeamNum(),
        DataModel.Header(
            title = "Autonomous Period"
        ),
        DataModel.Checkbox(
            id = "lc",
            title = "Robot left Community:",
            value = false
        ),
        DataModel.Number(
            id = "acb",
            title = "Cube Bottom Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "acm",
            title = "Cube Middle Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "act",
            title = "Cube Top Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "aob",
            title = "Cone Bottom Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "aom",
            title = "Cone Middle Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "aot",
            title = "Cone Top Scored:",
            value = 0
        ),
        DataModel.Checkbox(
            id = "adc",
            title = "Robot Docked:",
            value = false
        ),
        DataModel.Checkbox(
            id = "aeg",
            title = "Robot Engaged:",
            value = false
        ),

        DataModel.Header(
            title = "TeleOP Period"
        ),
        DataModel.Number(
            id = "tcb",
            title = "Cube Bottom Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "tcm",
            title = "Cube Middle Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "tct",
            title = "Cube Top Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "tob",
            title = "Cone Bottom Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "tom",
            title = "Cone Middle Scored:",
            value = 0
        ),
        DataModel.Number(
            id = "tot",
            title = "Cone Top Scored:",
            value = 0
        ),
        DataModel.Checkbox(
            id = "tdc",
            title = "Robot Docked:",
            value = false
        ),
        DataModel.Checkbox(
            id = "teg",
            title = "Robot Engaged:",
            value = false
        ),
        DataModel.Checkbox(
            id = "tpk",
            title = "Robot Parked in Community:",
            value = false
        ),
        DataModel.Number(
            id = "cyc",
            title = "Estimated Cycle Time (s):",
            value = 0
        ),

        DataModel.Slider(
            id = "dc",
            title = "Driver Comp:",
            value = 0.0f,
            max = 5.0f
        ),
        DataModel.Slider(
            id = "df",
            title = "Defensive",
            max = 5.0f,
            min = -1.0f,
            value = -1.0f
        ),
        DataModel.Text(
            id = "n",
            title = "Notes:",
            value = ""
        )
    )

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}