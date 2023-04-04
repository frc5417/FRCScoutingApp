package com.team5417.frcscouting

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.team5417.frcscouting.listeners.OnSwipeTouchListener
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.NoSuchElementException


class QRCodeActivity : AppCompatActivity() {

    lateinit var qrEncoder: QRGEncoder
    lateinit var bitmap: Bitmap
    lateinit var qrNext: ImageView
    lateinit var qrCurrent: ImageView
    lateinit var label: TextView
    var width: Float = 0.0f
    private var qrCodeData: MutableList<String> = mutableListOf()
    private var currentIndex: Int = 0
    private var oldMatchNum: Int = -1

    private val settingsFile = "settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcodes)

//        println("qrcode activity")
//        println(qrCodeData.size)

        currentIndex = 0
        qrCodeData = mutableListOf()

        label = findViewById(R.id.label)
        qrNext = findViewById(R.id.QRCodeNext)
        qrCurrent = findViewById(R.id.QRCodeCurrent)

        val extras = intent.extras
        if(extras != null) {
            extras.getStringArrayList("data")?.let {
                qrCodeData = it
                if(qrCodeData.size > 0) genQRCode(qrCodeData[currentIndex], qrCurrent)
            }
            extras.getInt("prevMatch")?.let {
                oldMatchNum = it
            }
        }

        if(qrCodeData.isNotEmpty()) {

            println(qrCodeData)

            label.text = "QRCode - ${currentIndex + 1}/" + qrCodeData.size +  " | Match: " + qrCodeData[0].split(",").find { it.startsWith("mn=") }!!
                .split("=")[1]+  " | Team: " + qrCodeData[0].split(",").find { it.startsWith("tn=") }!!
                .split("=")[1]

            width = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            val valueAnimatorLeft = ValueAnimator.ofFloat(-width, 0f)
            valueAnimatorLeft.addUpdateListener {
                val value = it.animatedValue as Float
                qrNext.translationX =  -value
                qrCurrent.translationX = -width - value
            }
            valueAnimatorLeft.interpolator = LinearInterpolator()
            valueAnimatorLeft.duration = 500

            val valueAnimatorRight = ValueAnimator.ofFloat(-width, 0f)
            valueAnimatorRight.addUpdateListener {
                val value = it.animatedValue as Float
                qrNext.translationX = value
                qrCurrent.translationX = width + value
            }
            valueAnimatorRight.interpolator = LinearInterpolator()
            valueAnimatorRight.duration = 500

            class QRCodeSwipeListener : OnSwipeTouchListener(applicationContext) {
                override fun onSwipeLeft() {
                    if(currentIndex >= qrCodeData.size - 1 || valueAnimatorLeft.isRunning) return
                    currentIndex += 1
                    genQRCode(qrCodeData[currentIndex], qrNext)
                    valueAnimatorLeft.start()

                    label.text = "QRCode - ${currentIndex + 1}/" + qrCodeData.size +  " | Match: " + qrCodeData[currentIndex].split(",").find { it.startsWith("mn=") }!!
                        .split("=")[1]+  " | Team: " + qrCodeData[currentIndex].split(",").find { it.startsWith("tn=") }!!
                        .split("=")[1]

                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        qrNext.translationX = -width
                        qrCurrent.translationX = 0f
                        genQRCode(qrCodeData[currentIndex], qrCurrent)
                    }, 500)
                }

                override fun onSwipeRight() {
                    if(currentIndex <= 0 || valueAnimatorLeft.isRunning) return
                    currentIndex -= 1
                    genQRCode(qrCodeData[currentIndex], qrNext)
                    valueAnimatorRight.start()

                    label.text = "QRCode - ${currentIndex + 1}/" + qrCodeData.size +  " | Match: " + qrCodeData[currentIndex].split(",").find { it.startsWith("mn=") }!!
                        .split("=")[1]+  " | Team: " + qrCodeData[currentIndex].split(",").find { it.startsWith("tn=") }!!
                        .split("=")[1]

                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        qrNext.translationX = -width
                        qrCurrent.translationX = 0f
                        genQRCode(qrCodeData[currentIndex], qrCurrent)
                    }, 500)
                }
            }

            var listener = QRCodeSwipeListener()

            var main: View = findViewById(R.id.touchListener)
            main.setOnTouchListener { v, event ->
                listener.onTouch(v, event)
            }

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
        val backBtn : Button = findViewById(R.id.backBtnQRCode)
        backBtn.setOnClickListener {
            val previousScreen = Intent(applicationContext, ScoutingActivity::class.java)
            previousScreen.putStringArrayListExtra("data", qrCodeData as ArrayList<String>)
            previousScreen.putExtra("prevMatch", oldMatchNum)
            setResult(RESULT_OK, previousScreen)
            finish()
        }

        val trashBtn : Button = findViewById(R.id.deleteBtnQRCode)
        trashBtn.setOnClickListener {
            if(qrCodeData.size == 0) return@setOnClickListener

            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Delete QR Codes")
            builder.setMessage("Do you want to delete one or all QR Codes?")

            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.setNegativeButton("One") { _, _ ->
                qrCodeData.removeAt(currentIndex)

                if(currentIndex >= qrCodeData.size) currentIndex = qrCodeData.size - 1

                if(currentIndex >= 0) {
                    label.text = "QRCode - ${currentIndex + 1}/" + qrCodeData.size +  " | Match: " + qrCodeData[currentIndex].split(",").find { it.startsWith("mn=") }!!
                    .split("=")[1]+  " | Team: " + qrCodeData[currentIndex].split(",").find { it.startsWith("tn=") }!!
                    .split("=")[1]

                    qrNext.translationX = -width
                    qrCurrent.translationX = 0f
                    genQRCode(qrCodeData[currentIndex], qrCurrent)
                } else {
                    label.text = "QRCode - 0/0"

                    qrNext.translationX = -width
                    qrCurrent.translationX = -width
                }

                val fosSaved: FileOutputStream = openFileOutput("savedStorageFile", Context.MODE_PRIVATE)
                fosSaved.write(qrCodeData.joinToString("\n").toByteArray())
                fosSaved.close()

                Toast.makeText(
                    applicationContext,
                    "Deleted that one!", Toast.LENGTH_SHORT
                ).show()

                if(qrCodeData.size == 0) finish()
            }

            builder.setPositiveButton("All") { _, _ ->
                qrCodeData.clear()

                val fosSaved: FileOutputStream = openFileOutput("savedStorageFile", Context.MODE_PRIVATE)
                fosSaved.write("".toByteArray())
                fosSaved.close()

                val previousScreen = Intent(applicationContext, ScoutingActivity::class.java)
                previousScreen.putStringArrayListExtra("data", qrCodeData as ArrayList<String>)
                previousScreen.putExtra("prevMatch", oldMatchNum)
                setResult(RESULT_OK, previousScreen)
                finish()

                Toast.makeText(
                    applicationContext,
                    "Deleted all!", Toast.LENGTH_SHORT
                ).show()
            }

            builder.show()
        }

    }

    private fun genQRCode(message: String, target: ImageView) {
        // credit: https://www.geeksforgeeks.org/generate-qr-code-in-android-using-kotlin/

        // on below line we are getting service for window manager
        val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // on below line we are initializing a
        // variable for our default display
        val display: Display = windowManager.defaultDisplay

        // on below line we are creating a variable
        // for point which is use to display in qr code
        val point: Point = Point()
        display.getSize(point)

        // on below line we are getting
        // height and width of our point
        val width = point.x
        val height = point.y

        // on below line we are generating
        // dimensions for width and height
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        // on below line we are initializing our qr encoder
        qrEncoder = QRGEncoder(message, null, QRGContents.Type.TEXT, dimen)

        // on below line we are running a try
        // and catch block for initialing our bitmap
        try {
            // on below line we are
            // initializing our bitmap
            bitmap = qrEncoder.encodeAsBitmap()

            // on below line we are setting
            // this bitmap to our image view
            target.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // on below line we
            // are handling exception
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val previousScreen = Intent(applicationContext, ScoutingActivity::class.java)
        previousScreen.putStringArrayListExtra("data", qrCodeData as ArrayList<String>)
        previousScreen.putExtra("prevMatch", oldMatchNum)
        setResult(RESULT_OK, previousScreen)

        super.onBackPressed()
    }

}