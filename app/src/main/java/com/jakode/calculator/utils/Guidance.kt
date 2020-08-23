package com.jakode.calculator.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.jakode.calculator.R
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.Circle

class Guidance(private val activity: Activity) {
    companion object {
        private const val STATUS = "firstRun"
    }

    val targets = ArrayList<Target>()
    private lateinit var spotlight: Spotlight

    private val preferences = activity.getSharedPreferences("Login", Context.MODE_PRIVATE)
    var first = preferences.getBoolean(STATUS, true)
        set(value) = preferences.edit().putBoolean(STATUS, value).apply()

    fun start() {
        // create spotlight
        if (targets.isNotEmpty()) {
            spotlight = Spotlight.Builder(activity)
                .setTargets(targets)
                .setBackgroundColor(R.color.spotlightBackground)
                .setDuration(1000L)
                .setAnimation(DecelerateInterpolator(2f))
                .setOnSpotlightListener(object : OnSpotlightListener {
                    override fun onStarted() {}
                    override fun onEnded() {}
                })
                .build()
            spotlight.start()
        }
    }

    fun next() = spotlight.next()
    fun finish() = spotlight.finish()

    fun add(recourse: Int, anchor: View, radiusCircle: Float, effect: Boolean) {
        val root = FrameLayout(activity)
        val layout = activity.layoutInflater.inflate(recourse, root)
        val target = with(Target.Builder()) {
            setAnchor(anchor)
            setShape(Circle(radiusCircle))
            if (effect) setEffect(RippleEffect(100f, 200f, Color.argb(30, 124, 255, 90)))
            setOverlay(layout)
            build()
        }
        targets.add(target)
    }
}