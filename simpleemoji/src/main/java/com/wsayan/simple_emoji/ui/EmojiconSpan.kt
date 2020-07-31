package com.wsayan.simple_emoji.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.DynamicDrawableSpan
import android.graphics.drawable.Drawable
import java.lang.ref.WeakReference

class EmojiconSpan(context: Context, resourceId: Int, size: Int, alignment: Int, textSize: Int) :
    DynamicDrawableSpan(alignment) {

    private var mContext: Context? = context
    private var mResourceId: Int = resourceId
    private val mSize: Int = size
    private var mTextSize: Int = textSize
    private var mHeight: Int = size
    private var mWidth: Int = size
    private var mTop: Int = 0
    private var mDrawable: Drawable? = null
    private var mDrawableRef: WeakReference<Drawable>? = null

    override fun getDrawable(): Drawable {
        if (mDrawable == null) {
            try {
                mDrawable = mContext?.resources?.getDrawable(mResourceId)
                mHeight = mSize
                mWidth = mHeight * mDrawable?.intrinsicWidth!! / mDrawable?.intrinsicHeight!!
                mTop = (mTextSize - mHeight) / 2
                mDrawable?.setBounds(0, mTop, mWidth, mTop + mHeight)
            } catch (e: Exception) {
                // swallow
            }

        }
        return this.mDrawable!!
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        val b = getCachedDrawable()
        canvas.save()

        var transY = bottom - b.bounds.bottom
        if (mVerticalAlignment == DynamicDrawableSpan.ALIGN_BASELINE) {
            transY = top + (bottom - top) / 2 - (b.bounds.bottom - b.bounds.top) / 2 - mTop
        }

        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    private fun getCachedDrawable(): Drawable {
        if (mDrawableRef == null || mDrawableRef?.get() == null) {
            mDrawableRef = WeakReference(drawable)
        }
        return mDrawableRef?.get()!!
    }


}