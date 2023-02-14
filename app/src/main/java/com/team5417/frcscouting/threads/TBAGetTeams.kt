package com.team5417.frcscouting.threads

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.team5417.frcscouting.R
import com.team5417.frcscouting.SettingsActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL

class TBAGetTeams(settings: SettingsActivity, eventCode: String): Runnable {
    private val settingsActivity = settings
    private val eventCode = eventCode

    override fun run() {
        try {
            val url = URL("https://www.thebluealliance.com/api/v3/event/" + eventCode + "/matches/simple?X-TBA-Auth-Key=" + settingsActivity.resources.getString(R.string.API_KEY))
            val text = url.readText()

            var matchesList = mutableListOf<String>();
            val matches = JSONArray(text)

            for (i in 0 until matches.length()) {
                val match = JSONObject(matches[i].toString())
                if (match["comp_level"] != "qm") continue
                val matchNum = match["match_number"]

                val alliances = match["alliances"]
                val redAlliance = JSONObject(alliances.toString())["red"]
                val redTeams = JSONObject(redAlliance.toString())["team_keys"].toString().replace("frc", "").replace("\"", "").replace("[", "").replace("]", "")
                val blueAlliance = JSONObject(alliances.toString())["blue"]
                val blueTeams = JSONObject(blueAlliance.toString())["team_keys"].toString().replace("frc", "").replace("\"", "").replace("[", "").replace("]", "")

                matchesList.add("$matchNum $redTeams $blueTeams")
            }

            val sortedMatches = matchesList.sortedBy{ it.split(" ")[0].toInt() }

            val handler = Handler(Looper.getMainLooper())
            if(matchesList.isNotEmpty()) {
                val runnable = Runnable {
                    settingsActivity.saveMatches(sortedMatches)
                    Toast.makeText(
                        settingsActivity.applicationContext,
                        "Gathered teams!", Toast.LENGTH_SHORT
                    ).show()
                }
                handler.post(runnable)
            }else{
                val runnable = Runnable {
                    Toast.makeText(
                        settingsActivity.applicationContext,
                        "There are no matches for the event!", Toast.LENGTH_SHORT
                    ).show()
                }
                handler.post(runnable)
            }


        } catch (err : FileNotFoundException) {
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                Toast.makeText(
                    settingsActivity.applicationContext,
                    "Invalid API Key!", Toast.LENGTH_SHORT
                ).show()
            }
            handler.post(runnable)
        } catch (err : Exception) {
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                Toast.makeText(
                    settingsActivity.applicationContext,
                    "No Internet Connection!", Toast.LENGTH_SHORT
                ).show()
            }
            handler.post(runnable)
        }
    }
}