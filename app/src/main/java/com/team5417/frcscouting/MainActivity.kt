package com.team5417.frcscouting

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import java.io.File
import java.io.FileOutputStream
import java.util.NoSuchElementException

class MainActivity : AppCompatActivity() {

    private val settingsFile = "settings"

    private var scouterName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureBtns()
    }

    private fun configureBtns() {
        val startBtn : Button = findViewById(R.id.btnStart);
        startBtn.setOnClickListener {
            startActivity(Intent(this, ScoutingActivity::class.java))
        }

        var settingsBtn : Button = findViewById(R.id.btnSettings)
        settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val name : EditText = findViewById(R.id.scouterName)
        name.setTextColor(Color.WHITE)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                scouterName = name.text.toString()
                saveName()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if(!filesDir.exists()) filesDir.mkdir()
        if(!File(filesDir, settingsFile).exists()) return

        openFileInput(settingsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    if (line.startsWith("scouterName=")) {
                        scouterName = line.split("=")[1]
                        name.setText(scouterName)
                    }
                }
            } catch (e: NoSuchElementException) {}
        }
    }

    private fun saveName() {
        // gather settings
        if(!filesDir.exists()) filesDir.mkdir()

        var settingsStr = ""

        openFileInput(settingsFile).bufferedReader().useLines { lines ->
            try {
                for (line in lines) {
                    if (!line.startsWith("scouterName=")) {
                        settingsStr += line + "\n"
                    } else if (line.startsWith("scouterName=")) {
                        settingsStr += "scouterName=$scouterName"
                    }
                }
            } catch (e: NoSuchElementException) {}
        }

        val fosSaved: FileOutputStream = openFileOutput(settingsFile, Context.MODE_PRIVATE)
        fosSaved.write(settingsStr.toByteArray())
        fosSaved.close()
    }

}