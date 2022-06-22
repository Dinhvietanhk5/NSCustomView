package com.newsoft.nscustomview.dialog.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.newsoft.nscustomview.dialog.ViewUtil

/**
 * Created by rahul on 29/06/17.
 */
class CFPushButton @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context!!, attrs, defStyleAttr) {
    fun initButton(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) {

        // Set centered text alignment
        gravity = Gravity.CENTER
        ViewUtil.addBounceEffect(this)
        setTypeface(getTypeface(), Typeface.BOLD)
    }

    init {
        initButton(context, attrs, defStyleAttr)
    }
}