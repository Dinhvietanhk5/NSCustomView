package com.newsoft.nscustomview.dialog

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatDialog
import androidx.cardview.widget.CardView
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import android.graphics.drawable.GradientDrawable
import androidx.core.view.ViewCompat
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.StyleRes
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.annotation.StringRes
import com.newsoft.nscustomview.R
import com.newsoft.nscustomview.dialog.views.CFPushButton
import java.util.ArrayList

class CFAlertDialog : AppCompatDialog {
    // region Enums
    enum class CFAlertStyle {
        NOTIFICATION, ALERT, BOTTOM_SHEET
    }

    enum class CFAlertActionStyle {
        DEFAULT, NEGATIVE, POSITIVE
    }

    enum class CFAlertActionAlignment {
        START, END, CENTER, JUSTIFIED
    }

    // endregion
    // region Properties
    private var style = -1
    private var params: DialogParams? = null
    private var cfDialogBackground: RelativeLayout? = null
    private var cfDialogContainer: RelativeLayout? = null
    private var cfDialogHeaderLinearLayout: LinearLayout? = null
    private var cfDialogBodyContainer: LinearLayout? = null
    private var buttonContainerLinearLayout: LinearLayout? = null
    private var cfDialogFooterLinearLayout: LinearLayout? = null
    private var iconTitleContainer: LinearLayout? = null
    private var selectableItemsContainer: LinearLayout? = null
    private var dialogCardView: CardView? = null
    private var dialogTitleTextView: TextView? = null
    private var dialogMessageTextView: TextView? = null
    private var cfDialogIconImageView: ImageView? = null
    private var cfDialogScrollView: ScrollView? = null

    // endregion
    // region Setup methods
    private constructor(context: Context?) : super(context, R.style.CFDialog) {}
    private constructor(context: Context?, theme: Int) : super(context, theme) {
        style = theme
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        // Inflate the view
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.cfalert_layout, null)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(view)

        // Setup the dialog
        setupSubviews(view)

        // Set the size to adjust when keyboard shown
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        // Disable the view initially
        setEnabled(false)
    }

    private fun setupSubviews(view: View) {
        cfDialogBackground = view.findViewById<View>(R.id.cfdialog_background) as RelativeLayout
        setupBackground()
        cfDialogContainer = view.findViewById<View>(R.id.cfdialog_container) as RelativeLayout

        // Card setup
        createCardView()
    }

    private fun setupBackground() {

        // Background
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        cfDialogBackground!!.setBackgroundColor(params!!.backgroundColor)
        cfDialogBackground!!.setOnClickListener {
            if (params!!.cancelable) {
                dismiss()
            }
        }

        // Dialog position
        adjustBackgroundGravity()
    }

    private fun createCardView() {
        dialogCardView = findViewById<View>(R.id.cfdialog_cardview) as CardView?
        bindCardSubviews()
        cfDialogScrollView!!.setBackgroundColor(params!!.dialogBackgroundColor)

        // Adjust the dialog width
        adjustDialogLayoutParams()
        populateCardView()
        setupCardBehaviour()
    }

    private fun bindCardSubviews() {
        cfDialogScrollView = dialogCardView!!.findViewById(R.id.cfdialog_scrollview)
        cfDialogBodyContainer = dialogCardView!!.findViewById(R.id.alert_body_container)
        cfDialogHeaderLinearLayout = dialogCardView!!.findViewById(R.id.alert_header_container)
        cfDialogHeaderLinearLayout!!.requestLayout()
        cfDialogHeaderLinearLayout!!.setVisibility(View.GONE)
        dialogTitleTextView = dialogCardView!!.findViewById(R.id.tv_dialog_title)
        iconTitleContainer = dialogCardView!!.findViewById(R.id.icon_title_container)
        cfDialogIconImageView = dialogCardView!!.findViewById(R.id.cfdialog_icon_imageview)
        dialogMessageTextView = dialogCardView!!.findViewById(R.id.tv_dialog_content_desc)
        buttonContainerLinearLayout = dialogCardView!!.findViewById(R.id.alert_buttons_container)
        cfDialogFooterLinearLayout = dialogCardView!!.findViewById(R.id.alert_footer_container)
        selectableItemsContainer =
            dialogCardView!!.findViewById(R.id.alert_selection_items_container)
    }

    private fun populateCardView() {
        // Icon
        if (params!!.iconDrawableId != -1) {
            setIcon(params!!.iconDrawableId)
        } else if (params!!.iconDrawable != null) {
            setIcon(params!!.iconDrawable)
        } else {
            setIcon(null)
        }

        // Title
        setTitle(params!!.title)

        // Message
        setMessage(params!!.message)

        // Text color
        if (params!!.textColor != -1) {
            setTitleColor(params!!.textColor)
            setMessageColor(params!!.textColor)
        }

        // Cancel
        setCancelable(params!!.cancelable)

        // Buttons
        populateButtons(params!!.context, params!!.buttons)

        // Text gravity
        setTextGravity(params!!.textGravity)

        // Selection items
        if (params!!.items != null && params!!.items!!.size > 0) {
            setItems(params!!.items, params!!.onItemClickListener)
        } else if (params!!.multiSelectItems != null && params!!.multiSelectItems!!.size > 0) {
            setMultiSelectItems(
                params!!.multiSelectItems,
                params!!.multiSelectedItems,
                params!!.onMultiChoiceClickListener
            )
        } else if (params!!.singleSelectItems != null && params!!.singleSelectItems!!.size > 0) {
            setSingleSelectItems(
                params!!.singleSelectItems,
                params!!.singleSelectedItem,
                params!!.onSingleItemClickListener
            )
        } else {
            selectableItemsContainer!!.removeAllViews()
        }

        // Hide Body container if there is no content in the body
        if (params!!.isDialogBodyEmpty) {
            cfDialogBodyContainer!!.visibility = View.GONE
        }

        // Image/Header
        if (params!!.contentImageDrawableId != -1) {
            setContentImageDrawable(params!!.contentImageDrawableId)
        } else if (params!!.contentImageDrawable != null) {
            setContentImageDrawable(params!!.contentImageDrawable)
        } else if (params!!.headerView != null) {
            setHeaderView(params!!.headerView)
        } else if (params!!.headerViewId != -1) {
            setHeaderView(params!!.headerViewId)
        }

        // Footer
        if (params!!.footerView != null) {
            setFooterView(params!!.footerView)
        } else if (params!!.footerViewId != -1) {
            setFooterView(params!!.footerViewId)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCardBehaviour() {
        // Style specific behaviour
        when (params!!.dialogStyle) {
            CFAlertStyle.NOTIFICATION -> {
                // Swipe to dismiss feature for notification type alerts
                val cardSwipeListener = SwipeToHideViewListener(
                    dialogCardView,
                    params!!.cancelable,
                    object : SwipeToHideViewListener.SwipeToHideCompletionListener {
                        override fun viewDismissed() {
                            super@CFAlertDialog.dismiss()
                        }
                    })
                cfDialogScrollView!!.setOnTouchListener(cardSwipeListener)
            }
            CFAlertStyle.ALERT -> {}
            CFAlertStyle.BOTTOM_SHEET -> {}
        }
    }

    // endregion
    override fun show() {
        super.show()

        // Perform the present animation
        startPresentAnimation()
    }

    override fun dismiss() {

        // Disable the view when being dismissed
        setEnabled(false)

        // perform the dismiss animation
        startDismissAnimation()
    }

    private fun alertPresented() {
        setEnabled(true)

        // Auto dismiss if needed
        if (params!!.autoDismissDuration > 0) {
            val handler = Handler()
            val runnable = Runnable { dismiss() }
            handler.postDelayed(runnable, params!!.autoDismissDuration)
        }
    }

    // region - Setters
    private fun setDialogParams(params: DialogParams) {
        this.params = params
    }

    private fun setEnabled(enabled: Boolean) {
        setViewEnabled(cfDialogBackground, enabled)
    }

    fun setBackgroundColor(color: Int, animated: Boolean) {
        if (animated) {
            val colorFrom = (cfDialogBackground!!.background as ColorDrawable).color
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, color)
            colorAnimation.duration =
                context.resources.getInteger(R.integer.cfdialog_animation_duration)
                    .toLong() // milliseconds
            colorAnimation.addUpdateListener { animator ->
                cfDialogBackground!!.setBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.start()
        } else {
            cfDialogBackground!!.setBackgroundColor(color)
        }
    }

    fun setDialogBackgroundColor(color: Int, animated: Boolean) {
        if (animated) {
            val colorFrom = (dialogCardView!!.background as ColorDrawable).color
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, color)
            colorAnimation.duration =
                context.resources.getInteger(R.integer.cfdialog_animation_duration)
                    .toLong() // milliseconds
            colorAnimation.addUpdateListener { animator ->
                dialogCardView!!.setBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.start()
        } else {
            cfDialogScrollView!!.setBackgroundColor(color)
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (TextUtils.isEmpty(title)) {
            dialogTitleTextView!!.visibility = View.GONE
            if (cfDialogIconImageView!!.visibility == View.GONE) {
                iconTitleContainer!!.visibility = View.GONE
            }
        } else {
            dialogTitleTextView!!.text = title
            dialogTitleTextView!!.visibility = View.VISIBLE
            iconTitleContainer!!.visibility = View.VISIBLE
        }
    }

    override fun setTitle(titleId: Int) {
        setTitle(context.getString(titleId))
    }

    fun setTitleColor(@ColorInt color: Int) {
        dialogTitleTextView!!.setTextColor(color)
    }

    fun setMessage(message: CharSequence?) {
        if (TextUtils.isEmpty(message)) {
            dialogMessageTextView!!.visibility = View.GONE
        } else {
            dialogMessageTextView!!.text = message
            dialogMessageTextView!!.visibility = View.VISIBLE
        }
    }

    fun setMessage(messageId: Int) {
        setMessage(context.getString(messageId))
    }

    fun setMessageColor(@ColorInt color: Int) {
        dialogMessageTextView!!.setTextColor(color)
    }

    /**
     * @param dialogStyle
     */
    fun setDialogStyle(dialogStyle: CFAlertStyle) {
        params!!.dialogStyle = dialogStyle
        adjustBackgroundGravity()
        adjustDialogLayoutParams()
    }

    /**
     * @param textGravity @see android.view.Gravity
     */
    fun setTextGravity(textGravity: Int) {
        (iconTitleContainer!!.layoutParams as LinearLayout.LayoutParams).gravity = textGravity
        dialogMessageTextView!!.gravity = textGravity
    }

    /**
     * @param headerView pass null to remove header
     */
    fun setHeaderView(headerView: View?) {
        cfDialogHeaderLinearLayout!!.removeAllViews()
        if (headerView != null) {
            cfDialogHeaderLinearLayout!!.visibility = View.VISIBLE
            cfDialogHeaderLinearLayout!!.addView(
                headerView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Allows the header view to overlap the alert content if needed
            disableClipOnParents(headerView)
        } else {
            cfDialogHeaderLinearLayout!!.visibility = View.GONE
        }
    }

    fun setHeaderView(@LayoutRes headerResId: Int) {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(headerResId, null)
        setHeaderView(view)
    }

    fun setIcon(@DrawableRes iconDrawableId: Int) {
        setIcon(ContextCompat.getDrawable(context, iconDrawableId))
    }

    fun setIcon(iconDrawable: Drawable?) {
        if (iconDrawable == null) {
            cfDialogIconImageView!!.visibility = View.GONE
            if (dialogTitleTextView!!.visibility == View.GONE) {
                iconTitleContainer!!.visibility = View.GONE
            }
        } else {
            cfDialogIconImageView!!.visibility = View.VISIBLE
            iconTitleContainer!!.visibility = View.VISIBLE
            cfDialogIconImageView!!.setImageDrawable(iconDrawable)
        }
    }

    /**
     * @param imageDrawableId value -1 will remove image
     */
    fun setContentImageDrawable(@DrawableRes imageDrawableId: Int) {
        setContentImageDrawable(ContextCompat.getDrawable(context, imageDrawableId))
    }

    fun setContentImageDrawable(imageDrawable: Drawable?) {
        if (imageDrawable != null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val imageView = layoutInflater.inflate(
                R.layout.cfdialog_imageview_header,
                cfDialogHeaderLinearLayout
            ).findViewById<View>(R.id.cfdialog_imageview_content) as ImageView
            imageView.setImageDrawable(imageDrawable)
            imageView.tag = 111
            cfDialogHeaderLinearLayout!!.visibility = View.VISIBLE
        } else {
            for (i in 0 until cfDialogHeaderLinearLayout!!.childCount) {
                val view = cfDialogHeaderLinearLayout!!.getChildAt(i)
                if (view is ImageView && view.getTag() as Int == 111) {
                    cfDialogHeaderLinearLayout!!.removeView(view)
                    cfDialogHeaderLinearLayout!!.visibility = View.GONE
                    break
                }
            }
        }
    }

    /**
     * @param footerView pass null to remove footer
     */
    fun setFooterView(footerView: View?) {
        cfDialogFooterLinearLayout!!.removeAllViews()
        if (footerView != null) {
            cfDialogFooterLinearLayout!!.addView(
                footerView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            cfDialogFooterLinearLayout!!.visibility = View.VISIBLE

            // Allows the footer view to overlap the alert content if needed
            disableClipOnParents(footerView)
        } else {
            cfDialogFooterLinearLayout!!.visibility = View.GONE
        }
    }

    fun setFooterView(@LayoutRes footerResId: Int) {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(footerResId, null)
        setFooterView(view)
    }

    private fun setViewEnabled(layout: ViewGroup?, enabled: Boolean) {
        layout!!.isEnabled = enabled
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is ViewGroup) {
                setViewEnabled(child, enabled)
            } else {
                child.isEnabled = enabled
            }
        }
    }

    private fun disableClipOnParents(v: View) {
        if (v.parent == null) {
            return
        }
        if (v is ViewGroup) {
            v.clipChildren = false
        }
        if (v.parent is View) {
            disableClipOnParents(v.parent as View)
        }
    }

    private fun populateButtons(context: Context?, buttons: List<CFAlertActionButton>?) {
        buttonContainerLinearLayout!!.removeAllViews()
        if (buttons!!.size > 0) {
            for (i in buttons.indices) {
                val buttonView = createButton(context, buttons[i])
                buttonContainerLinearLayout!!.addView(buttonView)
            }
            buttonContainerLinearLayout!!.visibility = View.VISIBLE
        } else {
            buttonContainerLinearLayout!!.visibility = View.GONE
        }
    }

    private fun createButton(context: Context?, actionButton: CFAlertActionButton): View {
        val button =
            CFPushButton(context, null, if (style != -1) style else R.style.CFDialog_Button)
        button.setOnClickListener(View.OnClickListener {
            actionButton.onClickListener.onClick(
                this@CFAlertDialog,
                0
            )
        })
        setButtonLayout(button, actionButton)
        button.setText(actionButton.buttonText)
        setButtonColors(button, actionButton)
        return button
    }

    private fun setButtonLayout(buttonView: View, actionButton: CFAlertActionButton) {
        var buttonParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        when (actionButton.alignment) {
            CFAlertActionAlignment.JUSTIFIED -> buttonParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            CFAlertActionAlignment.START -> buttonParams.gravity = Gravity.START
            CFAlertActionAlignment.CENTER -> buttonParams.gravity = Gravity.CENTER
            CFAlertActionAlignment.END -> buttonParams.gravity = Gravity.END
        }
        buttonView.layoutParams = buttonParams
        val padding = buttonView.resources.getDimension(R.dimen.cfdialog_button_padding).toInt()
        buttonView.setPadding(padding, padding, padding, padding)
    }

    private fun setButtonColors(button: CFPushButton, actionButton: CFAlertActionButton) {

        //Button background color
        if (actionButton.backgroundColor != -1) {
            val buttonDrawable = GradientDrawable()
            buttonDrawable.setColor(actionButton.backgroundColor)
            buttonDrawable.cornerRadius =
                context.resources.getDimension(R.dimen.cfdialog_button_corner_radius)
            ViewCompat.setBackground(button, buttonDrawable)
        } else if (actionButton.backgroundDrawableId != -1) {
            ViewCompat.setBackground(
                button,
                ContextCompat.getDrawable(context, actionButton.backgroundDrawableId)
            )
        }

        // Button text colors
        button.setTextColor(actionButton.textColor)
    }

    fun setItems(items: Array<String?>?, onClickListener: DialogInterface.OnClickListener?) {
        if (items != null && items.size > 0) {
            selectableItemsContainer!!.removeAllViews()
            selectableItemsContainer!!.visibility = View.VISIBLE
            for (i in items.indices) {
                val item = items[i]
                val view: View =
                    layoutInflater.inflate(R.layout.cfdialog_selectable_item_layout, null)
                val itemTextView =
                    view.findViewById<View>(R.id.cfdialog_selectable_item_textview) as TextView
                itemTextView.text = item
                view.setOnClickListener { onClickListener?.onClick(this@CFAlertDialog, i) }
                selectableItemsContainer!!.addView(view)
            }
        } else {
            selectableItemsContainer!!.visibility = View.GONE
        }
    }

    fun setMultiSelectItems(
        multiSelectItems: Array<String?>?,
        selectedItems: BooleanArray,
        onMultiChoiceClickListener: OnMultiChoiceClickListener?
    ) {
        if (multiSelectItems != null && multiSelectItems.size > 0) {
            require(selectedItems.size == multiSelectItems.size) { "multi select items and boolean array size not equal" }
            selectableItemsContainer!!.removeAllViews()
            selectableItemsContainer!!.visibility = View.VISIBLE
            for (i in multiSelectItems.indices) {
                val item = multiSelectItems[i]
                val view: View =
                    layoutInflater.inflate(R.layout.cfdialog_multi_select_item_layout, null)
                val checkBox =
                    view.findViewById<View>(R.id.cfdialog_multi_select_item_checkbox) as CheckBox
                checkBox.text = item
                checkBox.isChecked = selectedItems[i]
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    onMultiChoiceClickListener?.onClick(
                        this@CFAlertDialog,
                        i,
                        isChecked
                    )
                }
                selectableItemsContainer!!.addView(view)
            }
        } else {
            selectableItemsContainer!!.visibility = View.GONE
        }
    }

    fun setSingleSelectItems(
        singleSelectItems: Array<String?>?,
        selectedItem: Int,
        onClickListener: DialogInterface.OnClickListener?
    ) {
        if (singleSelectItems != null && singleSelectItems.size > 0) {
            selectableItemsContainer!!.removeAllViews()
            selectableItemsContainer!!.visibility = View.VISIBLE
            val radioGroup = layoutInflater.inflate(
                R.layout.cfdialog_single_select_item_layout,
                selectableItemsContainer
            )
                .findViewById<View>(R.id.cfstage_single_select_radio_group) as RadioGroup
            radioGroup.removeAllViews()
            for (i in singleSelectItems.indices) {
                val item = singleSelectItems[i]
                val radioButton = layoutInflater.inflate(
                    R.layout.cfdialog_single_select_radio_button_layout,
                    null
                ) as RadioButton
                radioButton.text = item
                radioButton.id = i
                if (i == selectedItem) {
                    radioButton.isChecked = true
                }
                radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked && onClickListener != null) {
                        onClickListener.onClick(this@CFAlertDialog, i)
                    }
                }
                radioGroup.addView(radioButton)
            }
        } else {
            selectableItemsContainer!!.visibility = View.GONE
        }
    }

    fun setElevation(elevation: Float) {
        dialogCardView!!.cardElevation = elevation
    }

    // endregion
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //cfDialogFooterLinearLayout.removeAllViews();
        //cfDialogHeaderLinearLayout.removeAllViews();
    }

    // region Animation helper methods
    private fun startPresentAnimation() {
        val presentAnimation = getPresentAnimation(params!!.dialogStyle)
        presentAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                alertPresented()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        dialogCardView!!.startAnimation(presentAnimation)
    }

    private fun startDismissAnimation() {
        // Perform the dismiss animation and after that dismiss the dialog
        val dismissAnimation = getDismissAnimation(params!!.dialogStyle)
        dismissAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val handler = Handler()
                handler.post { super@CFAlertDialog.dismiss() }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        dialogCardView!!.startAnimation(dismissAnimation)
    }

    private fun getPresentAnimation(style: CFAlertStyle): Animation {
        return when (style) {
            CFAlertStyle.NOTIFICATION -> AnimationUtils.loadAnimation(
                params!!.context, R.anim.dialog_present_top
            )
            CFAlertStyle.ALERT -> AnimationUtils.loadAnimation(
                params!!.context,
                R.anim.dialog_present_center
            )
            CFAlertStyle.BOTTOM_SHEET -> AnimationUtils.loadAnimation(
                params!!.context, R.anim.dialog_present_bottom
            )
            else -> AnimationUtils.loadAnimation(params!!.context, R.anim.dialog_present_center)
        }
    }

    private fun getDismissAnimation(style: CFAlertStyle): Animation {
        return when (style) {
            CFAlertStyle.NOTIFICATION -> AnimationUtils.loadAnimation(
                params!!.context, R.anim.dialog_dismiss_top
            )
            CFAlertStyle.ALERT -> AnimationUtils.loadAnimation(
                params!!.context,
                R.anim.dialog_dismiss_center
            )
            CFAlertStyle.BOTTOM_SHEET -> AnimationUtils.loadAnimation(
                params!!.context, R.anim.dialog_dismiss_bottom
            )
            else -> AnimationUtils.loadAnimation(params!!.context, R.anim.dialog_dismiss_center)
        }
    }

    // endregion
    // region Layout helper methods
    private fun adjustBackgroundGravity() {
        when (params!!.dialogStyle) {
            CFAlertStyle.NOTIFICATION -> cfDialogBackground!!.gravity = Gravity.TOP
            CFAlertStyle.ALERT -> cfDialogBackground!!.gravity = Gravity.CENTER_VERTICAL
            CFAlertStyle.BOTTOM_SHEET -> cfDialogBackground!!.gravity = Gravity.BOTTOM
        }
    }

    private fun adjustDialogLayoutParams() {

        // Corner radius
        dialogCardView!!.radius = cornerRadius

        // Layout params
        val cardContainerLayoutParams =
            cfDialogContainer!!.layoutParams as RelativeLayout.LayoutParams
        val margin = outerMargin
        var horizontalMargin = margin
        var topMargin = margin
        var maxWidth = context.resources.getDimension(R.dimen.cfdialog_maxwidth).toInt()
        val screenWidth = DeviceUtil.getScreenWidth(context)
        when (params!!.dialogStyle) {
            CFAlertStyle.NOTIFICATION -> {
                horizontalMargin = 0
                topMargin = 0
                maxWidth = screenWidth
            }
        }
        if (isCustomMargin) {
            maxWidth =
                screenWidth // Dialog can extend till the max screen width if custom margin is provided
        }
        var width = screenWidth - 2 * horizontalMargin
        width = Math.min(width, maxWidth)
        cardContainerLayoutParams.width = width
        cardContainerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        cardContainerLayoutParams.setMargins(horizontalMargin, topMargin, horizontalMargin, margin)
        cfDialogContainer!!.layoutParams = cardContainerLayoutParams
    }

    // Use the corner radius from params if provided.
    private var cornerRadius: Float
        private get() {
            var cornerRadius = context.resources.getDimension(R.dimen.cfdialog_card_corner_radius)
            when (params!!.dialogStyle) {
                CFAlertStyle.NOTIFICATION -> cornerRadius = 0f
            }

            // Use the corner radius from params if provided.
            if (params!!.dialogCornerRadius != -1f) {
                cornerRadius = params!!.dialogCornerRadius
            }
            return cornerRadius
        }
        set(radius) {
            params!!.dialogCornerRadius = radius
            dialogCardView!!.radius = cornerRadius
        }
    private var outerMargin: Int
        private get() {
            var margin = context.resources.getDimension(R.dimen.cfdialog_outer_margin).toInt()
            if (params!!.dialogOuterMargin != -1) {
                margin = params!!.dialogOuterMargin
            }
            return margin
        }
        set(margin) {
            params!!.dialogOuterMargin = margin
            adjustDialogLayoutParams()
        }
    private val isCustomMargin: Boolean
        private get() = params!!.dialogOuterMargin != -1

    // endregion
    class Builder {
        private var params: DialogParams

        constructor(context: Context?) {
            params = DialogParams()
            params.context = context
        }

        constructor(context: Context?, @StyleRes theme: Int) {
            params = DialogParams()
            params.context = context
            params.theme = theme
        }

        fun setBackgroundResource(@ColorRes backgroundResource: Int): Builder {
            params.backgroundColor =
                ResourcesCompat.getColor(params.context!!.resources, backgroundResource, null)
            return this
        }

        fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder {
            params.backgroundColor = backgroundColor
            return this
        }

        fun setDialogBackgroundResource(@ColorRes backgroundResource: Int): Builder {
            params.dialogBackgroundColor =
                ResourcesCompat.getColor(params.context!!.resources, backgroundResource, null)
            return this
        }

        fun setDialogBackgroundColor(@ColorInt backgroundColor: Int): Builder {
            params.dialogBackgroundColor = backgroundColor
            return this
        }

        fun setCornerRadius(cornerRadius: Float): Builder {
            params.dialogCornerRadius = cornerRadius
            return this
        }

        fun setOuterMargin(margin: Int): Builder {
            params.dialogOuterMargin = margin
            return this
        }

        fun setMessage(message: CharSequence?): Builder {
            params.message = message
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            params.title = title
            return this
        }

        fun setMessage(@StringRes messageId: Int): Builder {
            params.message = params.context!!.getString(messageId)
            return this
        }

        fun setTitle(@StringRes titleId: Int): Builder {
            params.title = params.context!!.getString(titleId)
            return this
        }

        fun setTextColor(@ColorInt color: Int): Builder {
            params.textColor = color
            return this
        }

        fun setContentImageDrawable(@DrawableRes contentImageDrawableId: Int): Builder {
            params.contentImageDrawableId = contentImageDrawableId
            params.contentImageDrawable = null
            return this
        }

        fun setContentImageDrawable(contentImageDrawable: Drawable?): Builder {
            params.contentImageDrawable = contentImageDrawable
            params.contentImageDrawableId = -1
            return this
        }

        fun setIcon(@DrawableRes iconDrawableId: Int): Builder {
            params.iconDrawableId = iconDrawableId
            params.iconDrawable = null
            return this
        }

        fun setIcon(iconDrawable: Drawable?): Builder {
            params.iconDrawable = iconDrawable
            params.iconDrawableId = -1
            return this
        }

        fun onDismissListener(onDismissListener: DialogInterface.OnDismissListener?): Builder {
            params.onDismissListener = onDismissListener
            return this
        }

        fun setDialogStyle(style: CFAlertStyle): Builder {
            params.dialogStyle = style
            return this
        }

        /**
         * @param textGravity @see android.view.Gravity
         */
        fun setTextGravity(textGravity: Int): Builder {
            params.textGravity = textGravity
            return this
        }

        fun addButton(
            buttonText: String?,
            @ColorInt textColor: Int,
            @ColorInt backgroundColor: Int,
            style: CFAlertActionStyle?,
            alignment: CFAlertActionAlignment?,
            onClickListener: DialogInterface.OnClickListener
        ): Builder {
            val button = CFAlertActionButton(
                params.context,
                buttonText,
                textColor,
                backgroundColor,
                style,
                alignment,
                onClickListener
            )
            params.buttons!!.add(button)
            return this
        }

        fun setItems(
            items: Array<String?>?,
            onItemClickListener: DialogInterface.OnClickListener?
        ): Builder {
            params.items = items
            params.onItemClickListener = onItemClickListener
            return this
        }

        fun setMultiChoiceItems(
            items: Array<String?>?,
            selectedItems: BooleanArray,
            onMultiChoiceClickListener: OnMultiChoiceClickListener?
        ): Builder {
            params.multiSelectItems = items
            params.multiSelectedItems = selectedItems
            params.onMultiChoiceClickListener = onMultiChoiceClickListener
            return this
        }

        fun setSingleChoiceItems(
            items: Array<String?>?,
            selectedItem: Int,
            onItemClickListener: DialogInterface.OnClickListener?
        ): Builder {
            params.singleSelectItems = items
            params.singleSelectedItem = selectedItem
            params.onSingleItemClickListener = onItemClickListener
            return this
        }

        fun setHeaderView(headerView: View?): Builder {
            params.headerView = headerView
            params.headerViewId = -1
            return this
        }

        fun setHeaderView(@LayoutRes headerViewId: Int): Builder {
            params.headerViewId = headerViewId
            params.headerView = null
            return this
        }

        fun setFooterView(footerView: View?): Builder {
            params.footerView = footerView
            params.footerViewId = -1
            return this
        }

        fun setFooterView(@LayoutRes footerViewId: Int): Builder {
            params.footerViewId = footerViewId
            params.footerView = null
            return this
        }

        /**
         * default is true
         *
         * @param cancelable
         */
        fun setCancelable(cancelable: Boolean): Builder {
            params.cancelable = cancelable
            return this
        }

        fun setAutoDismissAfter(duration: Long): Builder {
            params.autoDismissDuration = duration
            return this
        }

        fun create(): CFAlertDialog {
            val cfAlertDialog: CFAlertDialog = if (params.theme == 0) {
                CFAlertDialog(params.context)
            } else {
                CFAlertDialog(params.context, params.theme)
            }
            cfAlertDialog.setOnDismissListener(params.onDismissListener)
            cfAlertDialog.setDialogParams(params)
            return cfAlertDialog
        }

        fun show(): CFAlertDialog {
            val dialog = create()
            dialog.show()
            return dialog
        }
    }

    private class DialogParams {
        var context: Context? = null

        @ColorInt
        var backgroundColor = Color.parseColor("#B3000000")

        @ColorInt
        var dialogBackgroundColor = Color.parseColor("#FFFFFF")
        var dialogCornerRadius = -1f
        var dialogOuterMargin = -1
        var message: CharSequence? = null
        var title: CharSequence? = null

        @ColorInt
        var textColor = -1
        var theme: Int = R.style.CFDialog
        var textGravity = Gravity.LEFT
        var iconDrawableId = -1
        var contentImageDrawableId = -1
        var dialogStyle = CFAlertStyle.ALERT
        var headerView: View? = null
        var footerView: View? = null
        var headerViewId = -1
        var footerViewId = -1
        var contentImageDrawable: Drawable? = null
        var iconDrawable: Drawable? = null
        val buttons: MutableList<CFAlertActionButton>? = ArrayList()
        var onDismissListener: DialogInterface.OnDismissListener? = null
        var cancelable = true
        var multiSelectItems: Array<String?>? = null
        var items: Array<String?>? = null
        var singleSelectItems: Array<String?>? = null
        lateinit var multiSelectedItems: BooleanArray
        var singleSelectedItem = -1
        var onItemClickListener: DialogInterface.OnClickListener? = null
        var onSingleItemClickListener: DialogInterface.OnClickListener? = null
        var onMultiChoiceClickListener: OnMultiChoiceClickListener? = null
        var autoDismissDuration: Long = -1

        // The dialog body is empty if it doesn't contain any of the above items
        val isDialogBodyEmpty: Boolean
            get() {
                if (!TextUtils.isEmpty(title)) return false
                if (!TextUtils.isEmpty(message)) return false
                if (buttons != null && buttons.size > 0) return false
                if (items != null && items!!.isNotEmpty()) return false
                if (singleSelectItems != null && singleSelectItems!!.isNotEmpty()) return false
                return !(multiSelectItems != null && multiSelectItems!!.isNotEmpty())

                // The dialog body is empty if it doesn't contain any of the above items
            }
    }

    private class CFAlertActionButton(
        private val context: Context?,
        val buttonText: String?,
        @ColorInt textColor: Int,
        @ColorInt backgroundColor: Int,
        style: CFAlertActionStyle?,
        alignment: CFAlertActionAlignment?,
        onClickListener: DialogInterface.OnClickListener
    ) {
        val onClickListener: DialogInterface.OnClickListener
        var textColor = -1
        private val style: CFAlertActionStyle?
        var alignment: CFAlertActionAlignment? = CFAlertActionAlignment.JUSTIFIED
        var backgroundColor = -1
        var backgroundDrawableId = -1

        @DrawableRes
        private fun getBackgroundDrawable(style: CFAlertActionStyle?): Int {
            @DrawableRes var backgroundDrawable = 0
            when (style) {
                CFAlertActionStyle.NEGATIVE -> backgroundDrawable =
                    R.drawable.cfdialog_negative_button_background_drawable
                CFAlertActionStyle.POSITIVE -> backgroundDrawable =
                    R.drawable.cfdialog_positive_button_background_drawable
                CFAlertActionStyle.DEFAULT -> backgroundDrawable =
                    R.drawable.cfdialog_default_button_background_drawable
            }
            return backgroundDrawable
        }

        @ColorInt
        private fun getTextColor(style: CFAlertActionStyle?): Int {
            @ColorInt var textColor = -1
            when (style) {
                CFAlertActionStyle.NEGATIVE -> textColor =
                    ContextCompat.getColor(context!!, R.color.cfdialog_button_white_text_color)
                CFAlertActionStyle.POSITIVE -> textColor =
                    ContextCompat.getColor(context!!, R.color.cfdialog_button_white_text_color)
                CFAlertActionStyle.DEFAULT -> textColor =
                    ContextCompat.getColor(context!!, R.color.cfdialog_default_button_text_color)
            }
            return textColor
        }

        init {
            this.textColor = textColor
            this.backgroundColor = backgroundColor
            this.style = style
            backgroundDrawableId = getBackgroundDrawable(style)
            this.alignment = alignment
            this.onClickListener = onClickListener

            // default textColor
            if (textColor == -1) {
                this.textColor = getTextColor(style)
            }
        }
    }
}