package com.angcassa.stroyapp.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.angcassa.stroyapp.R

class CustomEdEmail : AppCompatEditText, View.OnTouchListener {

    private lateinit var btClear: Drawable
    private var edEmail: EditText = findViewById(R.id.edMail)


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        btClear = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showBtn() else hideBtn()
            }

            override fun afterTextChanged(s: Editable) {
                if ("@" in s.toString()) {
                    return
                } else {
                    edEmail.error = "email invalid"
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Masukan email Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (btClear.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - btClear.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btClear = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        showBtn()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        btClear = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideBtn()
                        return true
                    }
                    else -> return false
                }
            } else {
                return false
            }
        }
        return false
    }


    private fun showBtn() {
        setBtn(endOfTheText = btClear)
    }

    private fun hideBtn() {
        setBtn()
    }

    private fun setBtn(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }


}