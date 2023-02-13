package com.team5417.frcscouting

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
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

        val spinnerTeams = findViewById<Spinner>(R.id.spnrTeam)

        val adapterTeams =
            ArrayAdapter.createFromResource(this, R.array.teams, R.layout.support_simple_spinner_dropdown_item)
        adapterTeams.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerTeams.adapter = adapterTeams
        spinnerTeams.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                selectedTeam = spinnerTeams.selectedItem.toString()
                saveSettings()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

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
            val selected = if (spinnerEvents.selectedItem == null) "" else spinnerEvents.selectedItem.toString()
            if (selected == null || selected.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Invalid Event Selected!", Toast.LENGTH_SHORT
                ).show()
            } else {
                val threadWithRunnable = Thread(TBAGetTeams(this, selected))
                threadWithRunnable.start()
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

        val fosSaved: FileOutputStream = openFileOutput(settingsFile, Context.MODE_PRIVATE)
        fosSaved.write(settingsStr.toByteArray())
        fosSaved.close()
    }

    fun addEventMenu(events: List<String>) {
        val spinnerEvents = findViewById<Spinner>(R.id.spnrEvent)

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, events)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerEvents.adapter = adapter
    }

    fun saveMatches(matches: List<String>) {
        if(!filesDir.exists()) filesDir.mkdir()

        val fosSaved: FileOutputStream = openFileOutput(teamsFile, Context.MODE_PRIVATE)
        fosSaved.write(matches.joinToString("\n").toByteArray())
        fosSaved.close()
    }

}