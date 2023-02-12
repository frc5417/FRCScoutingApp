package com.team5417.frcscouting.threads

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.team5417.frcscouting.R
import com.team5417.frcscouting.SettingsActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

class TBAGetEvents(settings: SettingsActivity): Runnable {
    private val settingsActivity = settings

    override fun run() {

        val url = URL("https://www.thebluealliance.com/api/v3/events/2023?X-TBA-Auth-Key=" + settingsActivity.resources.getString(R.string.API_KEY))
        try {
            val text = url.readText()
            var comingUpEvents = mutableListOf<String>()

            val events = JSONArray(text)
            for (i in 0 until events.length()) {
                val event = JSONObject(events[i].toString())
                val endDateStr = event["end_date"].toString()
                val name = event["name"].toString()
                val key = event["key"].toString()

    //            val currentDate = System.currentTimeMillis() / 1000 //actually gets the current date
                val currentDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-03-01").time / 1000 //use this to change the current date
                val endDateTime = (SimpleDateFormat("yyyy-MM-dd").parse(endDateStr).time / 1000) + 86400 //get unix time for event end, add 1 day

                if(currentDate <= endDateTime && endDateTime - currentDate <= 432000) { //make current date before and make sure event is within 5 days
                    comingUpEvents.add(name)
                }
            }

            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                settingsActivity.addEventMenu(comingUpEvents)
                Toast.makeText(
                    settingsActivity.applicationContext,
                    "Gathered events!", Toast.LENGTH_SHORT
                ).show()
            }
            handler.post(runnable)
        } catch (err : FileNotFoundException) {
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                Toast.makeText(
                    settingsActivity.applicationContext,
                    "Invalid API Key!", Toast.LENGTH_SHORT
                ).show()
            }
            handler.post(runnable)
        }
    }
}