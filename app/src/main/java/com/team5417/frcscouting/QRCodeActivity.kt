package com.team5417.frcscouting

import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity

class QRCodeActivity : AppCompatActivity() {

    lateinit var qrEncoder: QRGEncoder
    lateinit var bitmap: Bitmap
    lateinit var qrIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcodes)

        qrIV = findViewById(R.id.idIVQrcode)

        val extras = intent.extras;
        if(extras != null) {
            extras.getString("data")?.let { genQRCode(it) }
        }

        configureBtns()
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtnQRCode);
        backBtn.setOnClickListener {
            finish();
        }
    }

    private fun genQRCode(message: String) {
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
            qrIV.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // on below line we
            // are handling exception
            e.printStackTrace()
        }
    }

}