package com.newsoft.nscustomview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.newsoft.nscustomview.cfalertdialog.CFAlertDialog
import com.newsoft.nscustomview.views.ColorSelectionView
import com.newsoft.nscustomview.views.SampleFooterView

/**
 * Created by rahul on 06/07/17.
 */

class DialogActivity : AppCompatActivity(), SampleFooterView.FooterActionListener {

    private var titleEditText: EditText? = null
    private var messageEditText: EditText? = null
    private var positiveButtonCheckbox: CheckBox? = null
    private var negativeButtonCheckbox: CheckBox? = null
    private var neutralButtonCheckbox: CheckBox? = null
    private var addHeaderCheckBox: CheckBox? = null
    private var addFooterCheckBox: CheckBox? = null
    private var closesOnBackgroundTapCheckBox: CheckBox? = null
    private var itemsRadioButton: RadioButton? = null
    private var multiChoiceRadioButton: RadioButton? = null
    private var singleChoiceRadioButton: RadioButton? = null
    private var textGravityLeft: RadioButton? = null
    private var textGravityCenter: RadioButton? = null
    private var textGravityRight: RadioButton? = null
    private var buttonGravityLeft: RadioButton? = null
    private var buttonGravityRight: RadioButton? = null
    private var buttonGravityCenter: RadioButton? = null
    private var buttonGravityFull: RadioButton? = null
    private var showTitleIcon: CheckBox? = null
    private var topDialogGravityRadioButton: RadioButton? = null
    private var centerDialogGravityRadioButton: RadioButton? = null
    private var bottomDialogGravityRadioButton: RadioButton? = null
    private var selectedBackgroundColorView: View? = null
    private var selectBackgroundColorContainer: View? = null
    private var showDialogFab: FloatingActionButton? = null
    private var alertDialog: CFAlertDialog? = null
    private var colorSelectionDialog: CFAlertDialog? = null
    private var colorSelectionView: ColorSelectionView? = null
    private var isHeaderVisible = false

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        bindViews()
        showDialogFab!!.setOnClickListener { showCFDialog() }
        selectBackgroundColorContainer!!.setOnClickListener { showColorSelectionAlert() }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun showColorSelectionAlert() {
        if (colorSelectionDialog == null) {
            colorSelectionView = ColorSelectionView(this)
            colorSelectionView!!.setSelectedColor(DEFAULT_BACKGROUND_COLOR)
            colorSelectionDialog = CFAlertDialog.Builder(this)
                .addButton(
                    "Done",
                    -1,
                    -1,
                    CFAlertDialog.CFAlertActionStyle.POSITIVE,
                    CFAlertDialog.CFAlertActionAlignment.JUSTIFIED
                ) { dialog, which -> // Update the color preview
                    setSelectedBackgroundColor(colorSelectionView!!.selectedColor)

                    // dismiss the dialog
                    colorSelectionDialog!!.dismiss()
                }
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setHeaderView(colorSelectionView)
                .onDismissListener { // Update the color preview
                    setSelectedBackgroundColor(colorSelectionView!!.selectedColor)
                }
                .create()
        }
        colorSelectionDialog!!.show()
    }

    private fun showCFDialog() {
        val builder = CFAlertDialog.Builder(this)

        // Vertical position of the dialog
        if (topDialogGravityRadioButton!!.isChecked) {
            builder.setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
        }
        if (centerDialogGravityRadioButton!!.isChecked) {
            builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
        }
        if (bottomDialogGravityRadioButton!!.isChecked) {
            builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
        }

        // Background
        var alertBGColor = -1
        if (colorSelectionView != null) {
            alertBGColor = colorSelectionView!!.selectedColor
            builder.setBackgroundColor(alertBGColor)
        }

        // Title and message
        builder.setTitle(titleEditText!!.getText())
        builder.setMessage(messageEditText!!.getText())
        if (textGravityLeft!!.isChecked) {
            builder.setTextGravity(Gravity.START)
        } else if (textGravityCenter!!.isChecked) {
            builder.setTextGravity(Gravity.CENTER_HORIZONTAL)
        } else if (textGravityRight!!.isChecked) {
            builder.setTextGravity(Gravity.END)
        }

        // Title icon
        if (showTitleIcon!!.isChecked) {
//            builder.setIcon(R.drawable.icon_drawable)
        }

        // Buttons
        if (positiveButtonCheckbox!!.isChecked) {

            // Add a sample positive button
            builder.addButton(
                "Positive",
                -1,
                -1,
                CFAlertDialog.CFAlertActionStyle.POSITIVE,
                buttonGravity
            ) { dialog, which ->
                Toast.makeText(this@DialogActivity, "Positive", Toast.LENGTH_SHORT).show()
                alertDialog!!.dismiss()
            }
        }
        if (negativeButtonCheckbox!!.isChecked) {

            // Add a sample negative button
            builder.addButton(
                "Negative",
                -1,
                -1,
                CFAlertDialog.CFAlertActionStyle.NEGATIVE,
                buttonGravity
            ) { dialog, which ->
                Toast.makeText(this@DialogActivity, "Negative", Toast.LENGTH_SHORT).show()
                alertDialog!!.dismiss()
            }
        }
        if (neutralButtonCheckbox!!.isChecked()) {

            // Add a sample neutral button
            builder.addButton(
                "Neutral",
                -1,
                -1,
                CFAlertDialog.CFAlertActionStyle.DEFAULT,
                buttonGravity
            ) { dialog, which ->
                Toast.makeText(this@DialogActivity, "Neutral", Toast.LENGTH_SHORT).show()
                alertDialog!!.dismiss()
            }
        }

        // Add Header
        if (addHeaderCheckBox!!.isChecked) {
            builder.setHeaderView(R.layout.dialog_header_layout)
            isHeaderVisible = true
        }

        // Add Footer
        if (addFooterCheckBox!!.isChecked) {
            val footerView = SampleFooterView(this)
            footerView.setSelecteBackgroundColor(alertBGColor)
            builder.setFooterView(footerView)
        }

        // Selection Items
        if (itemsRadioButton!!.isChecked) {

            // List items
            builder.setItems(
                arrayOf("First", "Second", "Third")
            ) { dialog, which ->
                when (which) {
                    0 -> Toast.makeText(this@DialogActivity, "First", Toast.LENGTH_SHORT)
                        .show()
                    1 -> Toast.makeText(this@DialogActivity, "Second", Toast.LENGTH_SHORT)
                        .show()
                    2 -> Toast.makeText(this@DialogActivity, "Third", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else if (singleChoiceRadioButton!!.isChecked) {

            // Single choice list items
            builder.setSingleChoiceItems(
                arrayOf("First", "Second", "Third"),
                1
            ) { dialog, which ->
                when (which) {
                    0 -> Toast.makeText(this@DialogActivity, "First", Toast.LENGTH_SHORT)
                        .show()
                    1 -> Toast.makeText(this@DialogActivity, "Second", Toast.LENGTH_SHORT)
                        .show()
                    2 -> Toast.makeText(this@DialogActivity, "Third", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else if (multiChoiceRadioButton!!.isChecked) {

            // Multi choice list items
            builder.setMultiChoiceItems(
                arrayOf("First", "Second", "Third"),
                booleanArrayOf(true, false, false)
            ) { dialog, which, isChecked ->
                when (which) {
                    0 -> Toast.makeText(
                        this@DialogActivity,
                        "First " + if (isChecked) "Checked" else "Unchecked",
                        Toast.LENGTH_SHORT
                    ).show()
                    1 -> Toast.makeText(
                        this@DialogActivity,
                        "Second " + if (isChecked) "Checked" else "Unchecked",
                        Toast.LENGTH_SHORT
                    ).show()
                    2 -> Toast.makeText(
                        this@DialogActivity,
                        "Third " + if (isChecked) "Checked" else "Unchecked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Cancel on background tap
        builder.setCancelable(closesOnBackgroundTapCheckBox!!.isChecked)
        alertDialog = builder.show()
        alertDialog!!.setOnDismissListener { onDialogDismiss() }
    }

    private val buttonGravity: CFAlertDialog.CFAlertActionAlignment
        private get() {
            if (buttonGravityLeft!!.isChecked()) {
                return CFAlertDialog.CFAlertActionAlignment.START
            }
            if (buttonGravityCenter!!.isChecked()) {
                return CFAlertDialog.CFAlertActionAlignment.CENTER
            }
            if (buttonGravityRight!!.isChecked()) {
                return CFAlertDialog.CFAlertActionAlignment.END
            }
            return if (buttonGravityFull!!.isChecked()) {
                CFAlertDialog.CFAlertActionAlignment.JUSTIFIED
            } else CFAlertDialog.CFAlertActionAlignment.JUSTIFIED
        }

    private fun bindViews() {
        titleEditText = findViewById<View>(R.id.title_edittext) as EditText?
        messageEditText = findViewById<View>(R.id.message_edittext) as EditText?
        textGravityLeft = findViewById<View>(R.id.text_gravity_left) as RadioButton?
        textGravityCenter = findViewById<View>(R.id.text_gravity_center) as RadioButton?
        textGravityRight = findViewById<View>(R.id.text_gravity_right) as RadioButton?
        positiveButtonCheckbox = findViewById<View>(R.id.positive_button_checkbox) as CheckBox?
        negativeButtonCheckbox = findViewById<View>(R.id.negative_button_checkbox) as CheckBox?
        neutralButtonCheckbox = findViewById<View>(R.id.neutral_button_checkbox) as CheckBox?
        addHeaderCheckBox = findViewById<View>(R.id.add_header_checkbox) as CheckBox?
        addFooterCheckBox = findViewById<View>(R.id.add_footer_checkbox) as CheckBox?
        buttonGravityLeft = findViewById<View>(R.id.button_gravity_left) as RadioButton?
        buttonGravityCenter = findViewById<View>(R.id.button_gravity_center) as RadioButton?
        buttonGravityRight = findViewById<View>(R.id.button_gravity_right) as RadioButton?
        buttonGravityFull = findViewById<View>(R.id.button_gravity_justified) as RadioButton?
        itemsRadioButton = findViewById<View>(R.id.items_radio_button) as RadioButton?
        multiChoiceRadioButton =
            findViewById<View>(R.id.multi_select_choice_items_radio_button) as RadioButton?
        singleChoiceRadioButton =
            findViewById<View>(R.id.single_choice_items_radio_button) as RadioButton?
        showTitleIcon = findViewById<View>(R.id.show_title_icon) as CheckBox?
        topDialogGravityRadioButton =
            findViewById<View>(R.id.top_dialog_gravity_radio_button) as RadioButton?
        centerDialogGravityRadioButton =
            findViewById<View>(R.id.center_dialog_gravity_radio_button) as RadioButton?
        bottomDialogGravityRadioButton =
            findViewById<View>(R.id.bottom_dialog_gravity_radio_button) as RadioButton?
        closesOnBackgroundTapCheckBox =
            findViewById<View>(R.id.closes_on_background_tap) as CheckBox?
        selectedBackgroundColorView = findViewById<View>(R.id.background_color_preview)
        setSelectedBackgroundColor(DEFAULT_BACKGROUND_COLOR)
        selectBackgroundColorContainer =
            findViewById<View>(R.id.background_color_selection_container)
        showDialogFab = findViewById<View>(R.id.fab) as FloatingActionButton?
    }

    private fun setSelectedBackgroundColor(color: Int) {
        val previewBackground: GradientDrawable =
            selectedBackgroundColorView!!.background as GradientDrawable
        previewBackground.setColor(color)
        ViewCompat.setBackground(selectedBackgroundColorView!!, previewBackground)
    }

    override  fun onBackgroundColorChanged(backgroundColor: Int) {
        alertDialog!!.setBackgroundColor(backgroundColor, true)
    }

    override fun onHeaderAdded() {
        if (alertDialog != null) {
            alertDialog!!.setHeaderView(R.layout.dialog_header_layout)
        }
        isHeaderVisible = true
    }

    override fun onHeaderRemoved() {
        if (alertDialog != null) {
            alertDialog!!.setHeaderView(null)
        }
        isHeaderVisible = false
    }

    override fun isHeaderVisible(): Boolean {
        return isHeaderVisible
    }

    private fun onDialogDismiss() {
        isHeaderVisible = false
    }

    companion object {
        private val DEFAULT_BACKGROUND_COLOR = Color.parseColor("#B3000000")
    }
}