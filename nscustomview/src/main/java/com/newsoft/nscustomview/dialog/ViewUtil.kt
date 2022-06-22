package com.newsoft.nscustomview.dialog

import android.view.MotionEvent
import android.view.View
import android.view.animation.ScaleAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator

object ViewUtil {

    fun addBounceEffect(view: View) {
        view.setOnTouchListener { v, event -> onButtonTouch(v, event) }
    }

    private fun onButtonTouch(button: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            onButtonPressed(button)
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_OUTSIDE) {
            onButtonReleased(button)
        }
        return false
    }

    private fun onButtonPressed(button: View) {
        val scaleAnimation = ScaleAnimation(
            1f, 0.9f,
            1f, 0.9f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.interpolator = DecelerateInterpolator()
        scaleAnimation.duration = 100
        scaleAnimation.fillAfter = true
        button.startAnimation(scaleAnimation)
    }

    private fun onButtonReleased(button: View) {
        val scaleAnimation = ScaleAnimation(
            0.9f, 1f,
            0.9f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.interpolator = DecelerateInterpolator()
        scaleAnimation.duration = 100
        scaleAnimation.fillAfter = true
        button.startAnimation(scaleAnimation)
    }
}