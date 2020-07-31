package com.wsayan.simple_emoji.ui

import android.content.Context
import android.util.AttributeSet
import android.text.style.DynamicDrawableSpan
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.wsayan.simple_emoji.R


class EmojiconMultiAutoCompleteTextView: AppCompatMultiAutoCompleteTextView {

    private var mEmojiconSize: Int = 0
    private var mEmojiconAlignment: Int = 0
    private var mEmojiconTextSize: Int = 0
    private var mUseSystemDefault = false

    constructor (context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor (context: Context, attrs: AttributeSet) : super(context, attrs, android.R.attr.editTextStyle) {
        init(attrs)
    }

    constructor (context: Context) : super(context) {
        mEmojiconSize = textSize.toInt()
        mEmojiconTextSize = textSize.toInt()
    }

    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Emojicon)
        mEmojiconSize = a.getDimension(R.styleable.Emojicon_emojiconSize, textSize).toInt()
        mEmojiconAlignment = a.getInt(R.styleable.Emojicon_emojiconAlignment, DynamicDrawableSpan.ALIGN_BASELINE)
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false)
        a.recycle()
        mEmojiconTextSize = textSize.toInt()
        setText(text)
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        updateText()
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        mEmojiconSize = pixels

        updateText()
    }

    private fun updateText() {
        EmojiconHandler.addEmojis(
            context,
            text,
            mEmojiconSize,
            mEmojiconAlignment,
            mEmojiconTextSize,
            mUseSystemDefault
        )
    }

    /**
     * Set whether to use system default emojicon
     */
    fun setUseSystemDefault(useSystemDefault: Boolean) {
        mUseSystemDefault = useSystemDefault
    }
}