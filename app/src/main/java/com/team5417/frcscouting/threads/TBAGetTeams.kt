package com.team5417.frcscouting.threads

import android.view.View
import android.widget.EditText
import com.team5417.frcscouting.R
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class TBAGetTeams(itemView: View): Runnable {
    private val view = itemView
    private val saveFile = "teamsFile"

    override fun run() {
        var teamNumEdit : EditText = view.findViewById(R.id.teamNum)

        val url = URL("https://www.thebluealliance.com/api/v3/event/2022txcmp1/matches/simple?X-TBA-Auth-Key=INSERT AUTH KEY")
        val text = url.readText()


        val matches = JSONArray(text)
        for (i in 0 until matches.length()) {
            val match = JSONObject(matches[i].toString())
            val alliances = match["alliances"]
            val blueAlliance = JSONObject(alliances.toString())["blue"]
            val blueTeams = JSONObject(blueAlliance.toString())["team_keys"].toString().replace("frc", "")
            println(blueTeams)
        }
    }
}