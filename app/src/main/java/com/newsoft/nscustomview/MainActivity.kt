package com.newsoft.nscustomview

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.newsoft.nscustomview.datepicker.DatePickerEnum
import com.newsoft.nscustomview.datepicker.NSDateTimePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest.setOnClickListener {
//            edt.validate()
            NSDateTimePicker(
                DatePickerEnum.DATE_TIME,
                this,
                object : NSDateTimePicker.ICustomDateTimeListener {
                    @SuppressLint("BinaryOperationInTimber")
                    override fun onSet(
                        dialog: Dialog,
                        calendarSelected: Calendar,
                        dateSelected: Date,
                        year: Int,
                        monthFullName: String,
                        monthShortName: String,
                        monthNumber: Int,
                        day: Int,
                        weekDayFullName: String,
                        weekDayShortName: String,
                        hour24: Int,
                        hour12: Int,
                        min: Int,
                        sec: Int,
                        AM_PM: String
                    ) {
                        Log.e("onTime", "${formatDate(calendarSelected)} $monthFullName")
                    }

                    override fun onCancel() {

                    }
                }).apply {
//                set24HourFormat(false)//24hr format is
                setMaxMinDisplayDate(
                    minDate = Calendar.getInstance().apply {
                        add(Calendar.MINUTE, 5)
                    }.timeInMillis,//min date is 5 min after current time
                    maxDate = Calendar.getInstance()
                        .apply { add(Calendar.YEAR, 1) }.timeInMillis//max date is next 1 year
                )
                setMaxMinDisplayedTime(5)//min time is 5 min after current time
                setDate(Calendar.getInstance())//date and time will show in dialog is current time and date. We can change this according to our need
                showDialog()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm:ss dd-MM-YYYY").format(calendar.time)
    }

}