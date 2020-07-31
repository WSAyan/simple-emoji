package com.wsayan.simple_emoji.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.PopupWindow
import android.view.WindowManager
import android.util.DisplayMetrics
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.os.SystemClock
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.ImageButton
import android.widget.LinearLayout
import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.view.LayoutInflater
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wsayan.simple_emoji.R
import com.wsayan.simple_emoji.emoji.*
import java.util.*

class EmojiconsPopup : PopupWindow, ViewPager.OnPageChangeListener, EmojiconRecents {

    private var mEmojiTabLastSelectedIndex = -1
    private var mEmojiTabs: Array<View?>? = null
    private var mEmojisAdapter: PagerAdapter? = null
    private var mRecentsManager: EmojiconRecentsManager? = null
    private var keyBoardHeight = 0
    private var pendingOpen = false
    private var isOpened = false
    public var onEmojiconClickedListener: EmojiconGridView.OnEmojiconClickedListener? = null
    private var onEmojiconBackspaceClickedListener: OnEmojiconBackspaceClickedListener? = null
    private var onSoftKeyboardOpenCloseListener: OnSoftKeyboardOpenCloseListener? = null
    var rootView: View? = null
    var mContext: Context? = null
    var mUseSystemDefault = false
    var view: View? = null
    var positionPager = 0
    var setColor = false
    var iconPressedColor = "#495C66"
    var tabsColor = "#DCE1E2"
    var backgroundColor = "#E6EBEF"

    private var emojisPager: ViewPager? = null


    constructor(
        rootView: View,
        mContext: Context,
        useSystemDefault: Boolean,
        iconPressedColor: String,
        tabsColor: String,
        backgroundColor: String
    ) : super(mContext) {
        this.setColor = true
        this.backgroundColor = backgroundColor
        this.iconPressedColor = iconPressedColor
        this.tabsColor = tabsColor
        this.mUseSystemDefault = useSystemDefault
        this.mContext = mContext
        this.rootView = rootView
        val customView = createCustomView()
        contentView = customView
        softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        setSize(LayoutParams.MATCH_PARENT, 255)
        setBackgroundDrawable(null)
    }


    constructor(rootView: View, mContext: Context, useSystemDefault: Boolean) : super(mContext) {
        this.mUseSystemDefault = useSystemDefault
        this.mContext = mContext
        this.rootView = rootView
        val customView = createCustomView()
        contentView = customView
        softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        setSize(LayoutParams.MATCH_PARENT, 255)
        setBackgroundDrawable(null)
    }


    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    fun setOnSoftKeyboardOpenCloseListener(listener: OnSoftKeyboardOpenCloseListener) {
        this.onSoftKeyboardOpenCloseListener = listener
    }

    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    fun setOnEmojiconClickedListener2(listener: EmojiconGridView.OnEmojiconClickedListener) {
        this.onEmojiconClickedListener = listener
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    fun setOnEmojiconBackspaceClickedListener(listener: OnEmojiconBackspaceClickedListener) {
        this.onEmojiconBackspaceClickedListener = listener
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     *
     */
    fun showAtBottom() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
    }

    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    fun showAtBottomPending() {
        if (isKeyBoardOpen()!!)
            showAtBottom()
        else
            pendingOpen = true
    }

    /**
     *
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    fun isKeyBoardOpen(): Boolean? {
        return isOpened
    }

    /**
     * Dismiss the popup
     */
    override fun dismiss() {
        super.dismiss()
        EmojiconRecentsManager
            .getInstance(mContext!!).saveRecents()
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    fun setSizeForSoftKeyboard() {
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            rootView?.getWindowVisibleDisplayFrame(r)

            val screenHeight = getUsableScreenHeight()
            var heightDifference = screenHeight - (r.bottom - r.top)
            val resourceId = mContext?.resources?.getIdentifier(
                "status_bar_height",
                "dimen", "android"
            )!!

            if (resourceId > 0) {
                heightDifference -= mContext?.resources?.getDimensionPixelSize(resourceId)!!
            }
            if (heightDifference > 100) {
                keyBoardHeight = heightDifference
                setSize(LayoutParams.MATCH_PARENT, keyBoardHeight)
                if (isOpened === false) {
                    if (onSoftKeyboardOpenCloseListener != null)
                        onSoftKeyboardOpenCloseListener?.onKeyboardOpen(keyBoardHeight)
                }
                isOpened = true
                if (pendingOpen) {
                    showAtBottom()
                    pendingOpen = false
                }
            } else {
                isOpened = false
                onSoftKeyboardOpenCloseListener?.onKeyboardClose()
            }
        }
    }

    private fun getUsableScreenHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()

            val windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)

            return metrics.heightPixels

        } else {
            return rootView?.getRootView()?.height!!
        }
    }

    /**
     * Manually set the popup window size
     * @param width Width of the popup
     * @param height Height of the popup
     */
    fun setSize(width: Int, height: Int) {
        setWidth(width)
        setHeight(height)
    }

    fun updateUseSystemDefault(mUseSystemDefault: Boolean) {
        if (view != null) {
            mEmojisAdapter = null
            positionPager = emojisPager?.getCurrentItem()!!
            dismiss()

            this.mUseSystemDefault = mUseSystemDefault
            contentView = createCustomView()
            //mEmojisAdapter.notifyDataSetChanged();
            mEmojiTabs?.get(positionPager)?.isSelected = true
            emojisPager?.currentItem = positionPager
            onPageSelected(positionPager)
            if (!isShowing) {

                //If keyboard is visible, simply show the emoji popup
                if (isKeyBoardOpen()!!) {
                    showAtBottom()
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                } else {
                    showAtBottomPending()
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                }//else, open the text keyboard first and immediately after that show the emoji popup
            }


        }
    }


    private fun createCustomView(): View {
        val inflater =
            mContext?.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.emojicons, null, false)
        emojisPager = view?.findViewById(R.id.emojis_pager) as ViewPager
        val tabs = view?.findViewById(R.id.emojis_tab) as LinearLayout

        @Suppress("DEPRECATION")
        emojisPager?.setOnPageChangeListener(this)
        val recents = this
        mEmojisAdapter = EmojisPagerAdapter(
            Arrays.asList(
                EmojiconRecentsGridView(mContext!!, null, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, People.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Nature.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Food.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Sport.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Cars.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Electr.DATA, recents, this, mUseSystemDefault),
                EmojiconGridView(mContext!!, Symbols.DATA, recents, this, mUseSystemDefault)

            )
        )
        emojisPager?.adapter = mEmojisAdapter
        mEmojiTabs = arrayOfNulls<View?>(8)

        mEmojiTabs!![0] = view?.findViewById(R.id.emojis_tab_0_recents)
        mEmojiTabs!![1] = view?.findViewById(R.id.emojis_tab_1_people)
        mEmojiTabs!![2] = view?.findViewById(R.id.emojis_tab_2_nature)
        mEmojiTabs!![3] = view?.findViewById(R.id.emojis_tab_3_food)
        mEmojiTabs!![4] = view?.findViewById(R.id.emojis_tab_4_sport)
        mEmojiTabs!![5] = view?.findViewById(R.id.emojis_tab_5_cars)
        mEmojiTabs!![6] = view?.findViewById(R.id.emojis_tab_6_elec)
        mEmojiTabs!![7] = view?.findViewById(R.id.emojis_tab_7_sym)

        for (i in 0 until mEmojiTabs!!.size) {
            mEmojiTabs!![i]!!.setOnClickListener { emojisPager!!.currentItem = i }
        }

        emojisPager?.setBackgroundColor(Color.parseColor(backgroundColor))
        tabs.setBackgroundColor(Color.parseColor(tabsColor))
        for (element in mEmojiTabs!!) {
            val btn = element as ImageButton
            btn.setColorFilter(Color.parseColor(iconPressedColor))
        }

        val imgBtn = view!!.findViewById(R.id.emojis_backspace) as ImageButton
        imgBtn.setColorFilter(Color.parseColor(iconPressedColor))
        imgBtn.setBackgroundColor(Color.parseColor(backgroundColor))


        view!!.findViewById<View>(R.id.emojis_backspace)
            .setOnTouchListener(RepeatListener(500, 50, object : View.OnClickListener {

                override fun onClick(v: View) {
                    if (onEmojiconBackspaceClickedListener != null)
                        onEmojiconBackspaceClickedListener!!.onEmojiconBackspaceClicked(v)
                }
            }))

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view!!.getContext())
        var page = mRecentsManager?.getRecentPage()
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager?.size === 0) {
            page = 1
        }

        if (page == 0) {
            onPageSelected(page)
        } else {
            page?.let { emojisPager?.setCurrentItem(it, false) }
        }
        return view!!
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        val fragment = (emojisPager?.adapter as EmojisPagerAdapter).recentFragment
        fragment?.addRecentEmoji(context, emojicon)
    }

    override fun onPageScrolled(i: Int, v: Float, i2: Int) {}

    override fun onPageSelected(i: Int) {
        if (mEmojiTabLastSelectedIndex === i) {
            return
        }
        when (i) {
            0, 1, 2, 3, 4, 5, 6, 7 -> {

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs!!.size) {
                    mEmojiTabs!![mEmojiTabLastSelectedIndex]?.isSelected = false
                }
                mEmojiTabs!![i]?.isSelected = true
                mEmojiTabLastSelectedIndex = i
                mRecentsManager?.setRecentPage(i)
            }
        }
    }

    override fun onPageScrollStateChanged(i: Int) {}


    interface OnEmojiconBackspaceClickedListener {
        fun onEmojiconBackspaceClicked(v: View)
    }

    interface OnSoftKeyboardOpenCloseListener {
        fun onKeyboardOpen(keyBoardHeight: Int)
        fun onKeyboardClose()
    }


    companion object {

        private class EmojisPagerAdapter(private val views: List<EmojiconGridView>) :
            PagerAdapter() {
            val recentFragment: EmojiconRecentsGridView?
                get() {
                    for (it in views) {
                        if (it is EmojiconRecentsGridView)
                            return it as EmojiconRecentsGridView
                    }
                    return null
                }

            override fun getCount(): Int {
                return views.size
            }


            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val v = views[position].rootView
                (container as ViewPager).addView(v, 0)
                return v
            }

            override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
                (container as ViewPager).removeView(view as View)
            }

            override fun isViewFromObject(view: View, key: Any): Boolean {
                return key === view
            }
        }


        public class RepeatListener(
            initialInterval: Int,
            normalInterval: Int,
            clickListener: View.OnClickListener?
        ) : View.OnTouchListener {

            private val handler = Handler()

            private var initialInterval: Int = initialInterval
            private var normalInterval: Int = normalInterval
            private var clickListener: View.OnClickListener? = clickListener

            private var downView: View? = null

            init {
                if (clickListener == null)
                    throw IllegalArgumentException("null runnable")
                if (initialInterval < 0 || normalInterval < 0)
                    throw IllegalArgumentException("negative interval")
            }

            private val handlerRunnable = object : Runnable {
                override fun run() {
                    if (downView == null) {
                        return
                    }
                    handler.removeCallbacksAndMessages(downView)
                    handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval)
                    clickListener?.onClick(downView)
                }
            }


            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downView = view
                        handler.removeCallbacks(handlerRunnable)
                        handler.postAtTime(
                            handlerRunnable,
                            downView,
                            SystemClock.uptimeMillis() + initialInterval
                        )
                        clickListener?.onClick(view)
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                        handler.removeCallbacksAndMessages(downView)
                        downView = null
                        return true
                    }
                }
                return false
            }

        }

    }
}