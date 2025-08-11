package com.example.outerdisplay

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var displayManager: DisplayManager
    private var presentation: Presentation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val btnShow = findViewById<Button>(R.id.btnShowOnExternal)
        val btnHide = findViewById<Button>(R.id.btnHideExternal)

        tvStatus.text = availableDisplaysInfo()

        btnShow.setOnClickListener {
            val external = findExternalDisplay()
            if (external != null) {
                showPresentationOnDisplay(external)
                tvStatus.text = "Shown on display id=${external.displayId}\n" + availableDisplaysInfo()
            } else {
                tvStatus.text = "External display not found.\n" + availableDisplaysInfo()
            }
        }

        btnHide.setOnClickListener {
            dismissPresentation()
            tvStatus.text = "Presentation hidden.\n" + availableDisplaysInfo()
        }
    }

    private fun availableDisplaysInfo(): String {
        val displays = displayManager.displays
        val sb = StringBuilder()
        sb.append("Available displays:\n")
        for (d in displays) {
            sb.append("id=${d.displayId}, name='${d.name}', flags=${d.flags}\n")
        }
        return sb.toString()
    }

    private fun findExternalDisplay(): Display? {
        for (d in displayManager.displays) {
            if (d.displayId != Display.DEFAULT_DISPLAY) {
                return d
            }
        }
        return null
    }

    private fun showPresentationOnDisplay(display: Display) {
        if (presentation?.display == display) return
        dismissPresentation()
        presentation = object : Presentation(this, display) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val view = LayoutInflater.from(context).inflate(R.layout.presentation_layout, null)
                setContentView(view)
            }
        }
        presentation?.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
        presentation?.show()
    }

    private fun dismissPresentation() {
        presentation?.dismiss()
        presentation = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissPresentation()
    }
}
