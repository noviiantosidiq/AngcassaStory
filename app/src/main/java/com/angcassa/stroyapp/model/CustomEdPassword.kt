package com.angcassa.stroyapp.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.angcassa.stroyapp.R

class CustomEdPassword : AppCompatEditText, View.OnTouchListener {

    private lateinit var btShow: Drawable
    private var edPassword: EditText = findViewById(R.id.edPass)


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
        btShow =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showBtn()
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length <= 5) edPassword.error = "min 6 character"
                showBtn()
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val showButtonStart: Float
            val showButtonEnd: Float
            var showButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                showButtonEnd = (btShow.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < showButtonEnd -> showButtonClicked = true
                }
            } else {
                showButtonStart = (width - paddingEnd - btShow.intrinsicWidth).toFloat()
                when {
                    event.x > showButtonStart -> showButtonClicked = true
                }
            }
            if (showButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btShow = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_visibility_24
                        ) as Drawable
                        edPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        showBtn()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        btShow = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_visibility_off_24
                        ) as Drawable
                        edPassword.inputType = 129
                        showBtn()
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
        setBtn(endOfTheText = btShow)
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