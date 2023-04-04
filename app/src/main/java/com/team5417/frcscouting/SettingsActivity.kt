package com.team5417.frcscouting

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.team5417.frcscouting.threads.TBAGetEvents
import com.team5417.frcscouting.threads.TBAGetTeams
import java.io.File
import java.io.FileOutputStream
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private val settingsFile = "settings"
    private val teamsFile = "teams"

    private var autoIncMatches = true
    private var selectedTeam = "Red 1"
    private var findTeamsOn = false
    private var scouterName = ""

    private var eventNames = mapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val threadWithRunnable = Thread(TBAGetEvents(this))
        threadWithRunnable.start()

        configureBtns()
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            finish();
        }

        val autoIncCheck : CheckBox = findViewById(R.id.autoIncCheck)
        autoIncCheck.isChecked = true
        autoIncCheck.setOnClickListener {
            autoIncMatches = autoIncCheck.isChecked
            saveSettings()
        }

        val name : EditText = findViewById(R.id.scouterName)
        name.setTextColor(Color.WHITE)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                scouterName = name.text.toString()
                saveSettings()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val spinnerTeams = findViewById<Spinner>(R.id.spnrTeam)

        val adapterTeams =
            ArrayAdapter.createFromResource(this, R.array.teams, R.layout.support_simple_spinner_dropdown_item)
        adapterTeams.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerTeams.adapter = adapterTeams
        spinnerTeams.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                selectedTeam = spinnerTeams.selectedItem.toString()
                saveSettings()

                (spinnerTeams.getChildAt(0) as TextView).setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

        val clearBtn : Button = findViewById(R.id.clearMatchesBtn)
        clearBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Clear Saved Matches")
            builder.setMessage("Do you want to clear the current saved matches?")

            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.setPositiveButton("Confirm") { dialog, _ ->
                clearMatches()

                Toast.makeText(
                    applicationContext,
                    "Cleared Match Data!", Toast.LENGTH_SHORT
                ).show()
            }

            builder.show();
        }

        val findTeamCheck : CheckBox = findViewById(R.id.findTeamCheck)
        findTeamCheck.setOnClickListener {
            findTeamsOn = findTeamCheck.isChecked
            saveSettings()
        }

        val fetchBtn : Button = findViewById(R.id.fetchBtn)
        fetchBtn.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Gathering teams!", Toast.LENGTH_SHORT
            ).show()

            val spinnerEvents = findViewById<Spinner>(R.id.spnrEvent)
            val name = if (spinnerEvents.selectedItem == null) "" else spinnerEvents.selectedItem.toString()
            if (name == null || name.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Invalid Event Selected!", Toast.LENGTH_SHORT
                ).show()
            } else {
                val selected = this.eventNames[name]
                if (selected != null){
                    val threadWithRunnable = Thread(TBAGetTeams(this, selected))
                    threadWithRunnable.start()
                }
            }
        }

        // gather settings
        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, settingsFile).exists()) return
        openFileInput(settingsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    if (line.startsWith("selectedTeam=")) {
                        selectedTeam = line.split("=")[1]
                        val spinnerPosition: Int = adapterTeams.getPosition(selectedTeam)
                        spinnerTeams.setSelection(spinnerPosition)
                    } else if (line.startsWith("findTeamsOn=")) {
                        findTeamsOn = line.split("=")[1] == "true"
                        findTeamCheck.isChecked = findTeamsOn
                    } else if (line.startsWith("autoIncMatches=")) {
                        autoIncMatches = line.split("=")[1] == "true"
                        autoIncCheck.isChecked = autoIncMatches
                    } else if (line.startsWith("scouterName=")) {
                        scouterName = line.split("=")[1]
                        name.setText(scouterName)
                    }
                }
            } catch (e: NoSuchElementException) {}
        }
    }

    private fun saveSettings() {
        if(!filesDir.exists()) filesDir.mkdir()

        var settingsStr = "";
        settingsStr += "autoIncMatches=$autoIncMatches\n"
        settingsStr += "selectedTeam=$selectedTeam\n"
        settingsStr += "findTeamsOn=$findTeamsOn\n"
        settingsStr += "scouterName=$scouterName\n"

        val fosSaved: FileOutputStream = openFileOutput(settingsFile, Context.MODE_PRIVATE)
        fosSaved.write(settingsStr.toByteArray())
        fosSaved.close()
    }

    fun addEventMenu(events: Map<String, String>) {
        val spinnerEvents = findViewById<Spinner>(R.id.spnrEvent)

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, events.keys.toList())
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerEvents.adapter = adapter

        spinnerEvents.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                try {
                    (spinnerEvents.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                }catch(e: Exception){
                    println(e.message)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

        this.eventNames = events
    }

    fun saveMatches(matches: List<String>) {
        if(!filesDir.exists()) filesDir.mkdir()

        val fosSaved: FileOutputStream = openFileOutput(teamsFile, Context.MODE_PRIVATE)
        fosSaved.write(matches.joinToString("\n").toByteArray())
        fosSaved.close()
    }

    fun clearMatches() {
        if(!filesDir.exists()) filesDir.mkdir()

        val fosSaved: FileOutputStream = openFileOutput(teamsFile, Context.MODE_PRIVATE)
        fosSaved.write("".toByteArray())
        fosSaved.close()
    }

}