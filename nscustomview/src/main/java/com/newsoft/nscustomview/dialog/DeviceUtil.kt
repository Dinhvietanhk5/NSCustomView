package com.newsoft.nscustomview.dialog

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.util.DisplayMetrics

object DeviceUtil {
    fun getScreenWidth(context: Context): Int {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun getScreenHeight(context: Context): Int {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(context: Context, dp: Float): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp.toInt() * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(context: Context, px: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts sp unit to equivalent pixels, depending on device density.
     *
     * @param sp A value in sp (scale independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to sp depending on device scale
     */
    fun convertSpToPixel(context: Context, sp: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (sp * scaledDensity).toInt()
    }

    /**
     * This method converts device specific pixels to scale independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent sp equivalent to px value
     */
    fun convertPixelsToSp(context: Context, px: Float): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }
}