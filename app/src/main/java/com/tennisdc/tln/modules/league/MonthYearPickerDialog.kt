package com.tennisdc.tln.modules.league

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.NumberPicker
import android.app.DialogFragment
import com.tennisdc.tln.R
import java.util.Calendar

class MonthYearPickerDialog : DialogFragment() {

    private var listener: DatePickerDialog.OnDateSetListener? = null
    private var daysOfMonth = 31

    private var monthPicker: NumberPicker? = null
    private var yearPicker: NumberPicker? = null
    private var dayPicker: NumberPicker? = null
    private var isEveryYearcheckBox: CheckBox? = null

    private val cal = Calendar.getInstance()

    internal var monthVal = -1
    internal var dayVal = -1
    internal var yearVal = -1
    internal var maxYearVal = -1
    internal var minYearVal = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = arguments
        if (extras != null) {
            monthVal = extras.getInt(MONTH_KEY, -1)
            dayVal = extras.getInt(DAY_KEY, -1)
            yearVal = extras.getInt(YEAR_KEY, -1)
            maxYearVal = extras.getInt(MAX_YEAR_KEY, -1)
            minYearVal = extras.getInt(MIN_YEAR_KEY, -1)
        }
        maxYearVal = if (maxYearVal == -1) 2025 else maxYearVal
        minYearVal = if (minYearVal == -1) 1925 else minYearVal

        if (minYearVal > maxYearVal) {
            val tempVal = maxYearVal
            maxYearVal = minYearVal
            minYearVal = tempVal
        }
    }

    fun setListener(listener: DatePickerDialog.OnDateSetListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity.layoutInflater

        val dialog = inflater.inflate(R.layout.month_year_picker, null)
        monthPicker = dialog.findViewById<View>(R.id.datepicker_month) as NumberPicker
        yearPicker = dialog.findViewById<View>(R.id.datepicker_year) as NumberPicker
        dayPicker = dialog.findViewById<View>(R.id.datepicker_day) as NumberPicker

        isEveryYearcheckBox = dialog.findViewById<View>(R.id.datepicker_isyearcheckBox) as CheckBox
        isEveryYearcheckBox!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                yearPicker!!.isEnabled = false
                yearPicker!!.value = minYearVal - 1
            } else {
                yearPicker!!.isEnabled = true
                if (yearVal != -1 && yearVal != 1904)
                    yearPicker!!.value = yearVal
                else {
                    yearPicker!!.value = cal.get(Calendar.YEAR)
                }
            }
            if (monthPicker!!.value == 2) {
                daysOfMonth = 28
                if (isLeapYear(yearPicker!!.value)) {
                    daysOfMonth = 29
                }
                dayPicker!!.maxValue = daysOfMonth
            }
        }

        monthPicker!!.minValue = 1
        monthPicker!!.maxValue = 12

        if (monthVal != -1)
            monthPicker!!.value = monthVal
        else
            monthPicker!!.value = cal.get(Calendar.MONTH) + 1

        monthPicker!!.displayedValues = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec")

        dayPicker!!.minValue = 1
        dayPicker!!.maxValue = daysOfMonth

        if (dayVal != -1)
            dayPicker!!.value = dayVal
        else
            dayPicker!!.value = cal.get(Calendar.DAY_OF_MONTH)

        monthPicker!!.setOnValueChangedListener { picker, oldVal, newVal ->
            when (newVal) {
                1, 3, 5, 7, 8, 10, 12 -> {
                    daysOfMonth = 31
                    dayPicker!!.maxValue = daysOfMonth
                }
                2 -> {
                    daysOfMonth = 28
                    if (isLeapYear(yearPicker!!.value)) {
                        daysOfMonth = 29
                    }
                    dayPicker!!.maxValue = daysOfMonth
                }

                4, 6, 9, 11 -> {
                    daysOfMonth = 30
                    dayPicker!!.maxValue = daysOfMonth
                }
            }
        }

        val maxYear = maxYearVal
        val minYear = minYearVal
        val arraySize = maxYear - minYear + 2

        val tempArray = arrayOfNulls<String>(arraySize)
        tempArray[0] = "---"
        var tempYear = minYear - 1

        for (i in 0 until arraySize) {
            if (i != 0) {
                tempArray[i] = " $tempYear"
            }
            tempYear++
        }
        Log.i("", "onCreateDialog: " + tempArray.size)
        yearPicker!!.minValue = minYear - 1
        yearPicker!!.maxValue = maxYear
        yearPicker!!.displayedValues = tempArray

        if (yearVal != -1 && yearVal != 1904) {
            yearPicker!!.value = yearVal
        } else {
            isEveryYearcheckBox!!.isChecked = false
            yearPicker!!.isEnabled = false
            yearPicker!!.value = minYear - 1
        }
        if (monthPicker!!.value == 2) {
            daysOfMonth = 28
            if (isLeapYear(yearPicker!!.value)) {
                daysOfMonth = 29
            }
            dayPicker!!.maxValue = daysOfMonth
        }

        yearPicker!!.setOnValueChangedListener { picker, oldVal, newVal ->
            try {
                daysOfMonth = 28
                if (isLeapYear(picker.value)) {
                    daysOfMonth = 29
                }
                dayPicker!!.maxValue = daysOfMonth
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.OK) { dialog, id ->
                    var year = yearPicker!!.value
                    if (year == minYear - 1) {
                        year = 1904
                    }
                    listener!!.onDateSet(null, year, monthPicker!!.value, dayPicker!!.value)
                }
                .setNegativeButton(R.string.CANCEL) { dialog, id -> this@MonthYearPickerDialog.dialog.cancel() }

        return builder.create()
    }

    companion object {

        val MONTH_KEY = "monthValue"
        val DAY_KEY = "dayValue"
        val YEAR_KEY = "yearValue"
        val MAX_YEAR_KEY = "maxyearValue"
        val MIN_YEAR_KEY = "minyearValue"

        fun newInstance(monthIndex: Int, daysIndex: Int, yearIndex: Int, maxYearIndex: Int, minYearIndex: Int): MonthYearPickerDialog {

            val f = MonthYearPickerDialog()

            // Supply num input as an argument.
            val args = Bundle()
            args.putInt(MONTH_KEY, monthIndex)
            args.putInt(DAY_KEY, daysIndex)
            args.putInt(YEAR_KEY, yearIndex)
            args.putInt(MAX_YEAR_KEY, maxYearIndex)
            args.putInt(MIN_YEAR_KEY, minYearIndex)
            f.arguments = args

            return f
        }

        fun isLeapYear(year: Int): Boolean {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365
        }
    }

    /* get month name */
    fun getMonthNameByNumber(mMonthNumber : Int) : String{
        var mMonthName = ""
        when (mMonthNumber){
            1-> mMonthName = "Jan"
            2-> mMonthName = "Feb"
            3-> mMonthName = "Mar"
            4-> mMonthName = "Apr"
            5-> mMonthName = "May"
            6-> mMonthName = "June"
            7-> mMonthName = "July"
            8-> mMonthName = "Aug"
            9-> mMonthName = "Sep"
            10-> mMonthName = "Oct"
            11-> mMonthName = "Nov"
            12-> mMonthName = "Dec"
        }

        return mMonthName
    }
}