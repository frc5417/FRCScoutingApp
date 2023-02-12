package com.team5417.frcscouting

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent.DispatcherState
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.team5417.frcscouting.threads.TBAGetEvents
import com.team5417.frcscouting.threads.TBAGetTeams
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val settingsFile = "settings"
    var events = arrayOf<String>()

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

        val spinnerTeams = findViewById<Spinner>(R.id.spnrTeam)

        val adapterTeams =
            ArrayAdapter.createFromResource(this, R.array.teams, R.layout.support_simple_spinner_dropdown_item)
        adapterTeams.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerTeams.adapter = adapterTeams

        val findTeamCheck : CheckBox = findViewById(R.id.findTeamCheck)
        findTeamCheck.setOnClickListener {

        }
        
    }


    fun addEventMenu(events: List<String>) {
        val spinnerEvents = findViewById<Spinner>(R.id.spnrEvent)

        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, events)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerEvents.adapter = adapter
    }

}