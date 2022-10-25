package com.team5417.frcscouting

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.team5417.frcscouting.listeners.OnSwipeTouchListener

class QRCodeActivity : AppCompatActivity() {

    lateinit var qrEncoder: QRGEncoder
    lateinit var bitmap: Bitmap
    lateinit var qrNext: ImageView
    lateinit var qrCurrent: ImageView
    lateinit var label: TextView
    private var qrCodeData: List<String> = mutableListOf()
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcodes)

        println("qrcode activity")
        println(qrCodeData.size)

        currentIndex = 0;
        qrCodeData = mutableListOf()

        label = findViewById(R.id.label)
        qrNext = findViewById(R.id.QRCodeNext)
        qrCurrent = findViewById(R.id.QRCodeCurrent)

        val extras = intent.extras;
        if(extras != null) {
            extras.getStringArrayList("data")?.let {
                qrCodeData = it
                genQRCode(qrCodeData[currentIndex], qrCurrent)
            }
        }

        label.text = "QRCode - 1/" + qrCodeData.size +  " | Match: "

        val valueAnimator = ValueAnimator.ofFloat(-Resources.getSystem().displayMetrics.widthPixels.toFloat(), 0f);
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            qrNext.translationX = value
            qrCurrent.translationX = -value
        }
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 1000

        class QRCodeSwipeListener : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeLeft() {
//                println("swipe left")
                if(currentIndex <= 0 || valueAnimator.isRunning) return
                currentIndex -= 1
                genQRCode(qrCodeData[currentIndex], qrNext)
                valueAnimator.start()
            }

            override fun onSwipeRight() {
//                println("swipe right")
                if(currentIndex >= qrCodeData.size - 1 || valueAnimator.isRunning) return
                currentIndex -= 1
                genQRCode(qrCodeData[currentIndex], qrNext)
                valueAnimator.start()
            }
        }

        var listener = QRCodeSwipeListener();

        var main: View = findViewById(R.id.touchListener)
        main.setOnTouchListener { v, event ->
            println("onTouch triggered")
            listener.onTouch(v, event)
        }

        configureBtns()
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtnQRCode);
        backBtn.setOnClickListener {
            finish();
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {


        return super.onTouchEvent(event)
    }

}