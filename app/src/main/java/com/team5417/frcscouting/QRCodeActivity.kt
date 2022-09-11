package com.team5417.frcscouting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class QRCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcodes)

        configureBtns()
    }

    private fun configureBtns() {
        val backBtn : Button = findViewById(R.id.backBtnQRCode);
        backBtn.setOnClickListener {
            finish();
        }

    }

}