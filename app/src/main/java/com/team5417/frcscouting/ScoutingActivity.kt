package com.team5417.frcscouting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    private val settingsFile = "settings"
    private val teamsFile = "teams"

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                savedQRCodes = data.getStringArrayListExtra("data") as MutableList<String>
                val match = data.getIntExtra("prevMatch", -1)
                if (match != -1) {
                    openFileInput(settingsFile).bufferedReader().useLines { lines ->
                        try {
                            for (line in lines) {
                                if (line.startsWith("autoIncMatches=")) {
                                    if (line.split("=")[1] == "true") {
                                        dataAdapter.setMatchNum(match + 1)
                                    }
                                    break
                                }
                            }
                        } catch (e: NoSuchElementException) {}
                    }
                }
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
                    }
                }
            } catch (e: NoSuchElementException) {}
        }
    }

    fun getTeamNumFromMatch(match: Int): String {
        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, settingsFile).exists() || !File(filesDir, teamsFile).exists()) return ""

        var gatherFromFile = false
        var selectedTeam = "Red 1"
        openFileInput(settingsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    if (line.startsWith("findTeamsOn=")) {
                        gatherFromFile = line.split("=")[1] == "true"
                    } else if (line.startsWith("selectedTeam=")) {
                        selectedTeam = line.split("=")[1]
                    }
                }
            } catch (e: NoSuchElementException) {}
        }

        if (!gatherFromFile) return ""

        openFileInput(teamsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    val args = line.split(" ")
                    if (args.size < 3) continue
                    if (args[0].toInt() == match) {
                        when (selectedTeam) {
                            "Red 1" -> {
                                return args[1].split(",")[0]
                            }
                            "Red 2" -> {
                                return args[1].split(",")[1]
                            }
                            "Red 3" -> {
                                return args[1].split(",")[2]
                            }
                            "Blue 1" -> {
                                return args[2].split(",")[0]
                            }
                            "Blue 2" -> {
                                return args[2].split(",")[1]
                            }
                            "Blue 3" -> {
                                return args[2].split(",")[2]
                            }
                        }
                    }
                }
            } catch (e: NoSuchElementException) {}
        }

        return ""
    }

    private fun getCachedValues() : List<DataModel> {
        val adapterData = getData()

        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, filename).exists()) return adapterData

        openFileInput(filename).bufferedReader().useLines { lines ->
            try {
                val line = lines.first()
                if(line != "") {
                    line.split(",").forEach { csv ->
                        if (csv.contains('=')) {
                            val id = csv.split("=")[0]
                            val value = csv.split("=")[1]
                            adapterData.forEach { model ->
                                when (model) {
                                    is DataModel.Number -> {
                                        if (model.id == id) {
                                            model.value = value.toFloat()
                                        }
                                    }
                                    is DataModel.Checkbox -> {
                                        if (model.id == id) {
                                            model.value = value == "1"
                                        }
                                    }
                                    is DataModel.Text -> {
                                        if (model.id == id) {
                                            model.value = value.replace("|||", "\n")
                                        }
                                    }
                                    is DataModel.Slider -> {
                                        if (model.id == id) {
                                            model.value = value.toFloat()
                                        }
                                    }
                                    is DataModel.DropDown -> {
                                        if (model.id == id) {
                                            model.index = value.toInt()
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

        var selectedTeam = "Red 1"
        var name = ""
        // gather settings
        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, settingsFile).exists()) return
        openFileInput(settingsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    if (line.startsWith("selectedTeam=")) {
                        selectedTeam = line.split("=")[1]
                    } else if (line.startsWith("scouterName=")) {
                        name = line.split("=")[1]
                    }
                }
            } catch (e: NoSuchElementException) {}
        }

        if (name != "") {
            selectedTeam += " - $name"
        }

        val teamText : TextView = findViewById(R.id.team)
        teamText.text = selectedTeam
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            finish();
        }

        val clearBtn : Button = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Clear Data")
            builder.setMessage("Do you want to clear your current data?")

            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.setPositiveButton("Confirm") { dialog, _ ->
                dataAdapter.setData(getData())

                Toast.makeText(
                    applicationContext,
                    "Cleared Data!", Toast.LENGTH_SHORT
                ).show()
            }

            builder.show();
        }

        val exportBtn : Button = findViewById(R.id.idBtnGenerateQR)
        exportBtn.setOnClickListener {
            val models = dataAdapter.getData()
            var isMatchNum = false;
            var isTeamNum = false;
            var dataToSend = ""
            var prevMatch = -1
            for (model in models) {
                val toAdd = when (model) {
                    is DataModel.Number -> model.id+"="+model.value.toString()
                    is DataModel.Checkbox -> model.id+"="+if (model.value) "1" else "0"
                    is DataModel.Text -> model.id+"="+model.value.replace("\n", "|||").replace(",", ";")
                    is DataModel.Slider -> model.id+"="+model.value.toString()
                    is DataModel.DropDown -> model.id+"="+model.index.toString()
                    is DataModel.MatchAndTeamNum -> {
                        if(model.matchNum == -1) break
                        isMatchNum = true
                        if(model.teamNum == -1) break
                        isTeamNum = true
                        prevMatch = model.matchNum
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
                intent.putExtra("prevMatch", prevMatch)
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

    private fun getData(match: Int = -1): List<DataModel> = listOf(
        DataModel.MatchAndTeamNum(
            matchNum = match
        ),
        DataModel.Header(
            title = "Autonomous Period"
        ),
        DataModel.Checkbox(
            id = "lsz",
            title = "Robot left Starting Zone:",
            value = false
        ),
        DataModel.Number(
            id = "ana",
            title = "Notes Scored in Amp:",
            value = 0f
        ),
        DataModel.Number(
            id = "ans",
            title = "Notes Scored in Speaker:",
            value = 0f
        ),

        DataModel.Header(
            title = "TeleOP Period"
        ),
        DataModel.Number(
            id = "tna",
            title = "Notes Scored in Amp:",
            value = 0f
        ),
        DataModel.Number(
            id = "tns",
            title = "Notes Scored in Speaker:",
            value = 0f
        ),
        DataModel.DropDown(
            id = "ts",
            title = "Stage",
            options = mutableListOf("None", "Parked", "On Stage", "SPOTLIT On Stage")
        ),
        DataModel.Checkbox(
            id = "tnt",
            title = "Scored Trap",
            value = false
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