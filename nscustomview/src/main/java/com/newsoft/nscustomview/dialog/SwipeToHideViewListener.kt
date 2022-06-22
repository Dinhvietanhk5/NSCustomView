package com.newsoft.nscustomview.dialog

import android.animation.Animator
import android.view.WindowManager
import android.util.DisplayMetrics
import com.newsoft.nscustomview.dialog.SwipeToHideViewListener.SwipeToHideCompletionListener
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View
import com.newsoft.nscustomview.dialog.SwipeToHideViewListener
import com.newsoft.nscustomview.dialog.SwipeToHideViewListener.AnimatorCompletionListener
import android.view.ViewPropertyAnimator
import com.newsoft.nscustomview.dialog.ViewUtil
import android.view.animation.ScaleAnimation
import android.view.animation.Animation

/**
 * Created by rahul on 31/08/17.
 */
class SwipeToHideViewListener(
    private var animatingView: View?,
    private var shouldDismissView: Boolean,
    private var listener: SwipeToHideCompletionListener
) : OnTouchListener {
    private var isTouching = false
    private var swipeStartX = 0f
    private var swipeStartY = 0f
    private var viewStartX = 0f
    private var deltaX = 0f
    private var deltaY = 0f
    private var isSwipingHorizontal = false

    fun setAnimatingView(animatingView: View?) {
        this.animatingView = animatingView
    }

    fun setShouldDismissView(shouldDismissView: Boolean) {
        this.shouldDismissView = shouldDismissView
    }

    fun setListener(listener: SwipeToHideCompletionListener) {
        this.listener = listener
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // Set Touched view as the animatingView if not set
                if (animatingView == null) animatingView = view
                isTouching = true
                startSwipe(motionEvent)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouching) moveSwipe(motionEvent)
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
                isTouching = false
                isSwipingHorizontal = false
                endSwipe()
            }
            MotionEvent.ACTION_OUTSIDE -> {
                isTouching = false
                isSwipingHorizontal = false
                endSwipe()
            }
        }
        return isSwipingHorizontal
    }

    private fun startSwipe(event: MotionEvent) {

        // Keep the initial swipe action position
        swipeStartX = event.rawX
        swipeStartY = event.rawY
        viewStartX = animatingView!!.x
    }

    private fun moveSwipe(event: MotionEvent) {

        // Check if the motion is horizontal
        deltaX = event.rawX - swipeStartX
        deltaY = event.rawY - swipeStartY

        // Check Vertical Swipe
        if (Math.abs(deltaY) > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {

            // Is swiping vertically
            return
        }
        // Check Horizontal swipe
        if (Math.abs(deltaX) > 0) {
            animateViewHorizontally(deltaX, 0, false, null)
            isSwipingHorizontal = true
        }
    }

    private fun endSwipe() {
        if (shouldDismissView && Math.abs(deltaX) > SWIPE_TO_DISMISS_THRESHOLD) {

            // Check whether view should animate left or right
            val endPos = if (deltaX > 0) animatingView!!.width
                .toFloat() else -animatingView!!.width.toFloat()
            animateViewHorizontally(
                endPos,
                SWIPE_TO_DISMISS_ANIMATION_DURATION,
                true,
                object : AnimatorCompletionListener() {
                    override fun onAnimationCompleted() {
                        if (listener != null) listener!!.viewDismissed()
                    }
                })
        } else {
            animateViewHorizontally(0f, SWIPE_TO_DISMISS_ANIMATION_DURATION, false, null)
        }
    }

    private fun animateViewHorizontally(
        dX: Float,
        duration: Int,
        shouldHide: Boolean,
        listener: AnimatorCompletionListener?
    ) {
        val animatingDistance = viewStartX + dX
        val animator = animatingView!!.animate()
            .x(animatingDistance)
            .setDuration(duration.toLong())
            .setListener(listener)
        if (shouldHide) {
            animator.alpha(0f)
        }
        animator.start()
    }

    private abstract inner class AnimatorCompletionListener : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {}
        override fun onAnimationEnd(animator: Animator) {
            onAnimationCompleted()
        }

        override fun onAnimationCancel(animator: Animator) {}
        override fun onAnimationRepeat(animator: Animator) {}
        abstract fun onAnimationCompleted()
    }

    interface SwipeToHideCompletionListener {
        fun viewDismissed()
    }

    companion object {
        private const val SWIPE_TO_DISMISS_THRESHOLD = 150
        private const val SWIPE_TO_DISMISS_ANIMATION_DURATION = 100
    }
}