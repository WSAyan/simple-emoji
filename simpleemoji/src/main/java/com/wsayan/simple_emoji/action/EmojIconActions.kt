package com.wsayan.simple_emoji.action

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.wsayan.simple_emoji.ui.EmojiconEditText
import java.util.*
import android.view.KeyEvent.KEYCODE_ENDCALL
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent
import com.wsayan.simple_emoji.emoji.Emojicon
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.wsayan.simple_emoji.R
import com.wsayan.simple_emoji.ui.EmojiconGridView
import com.wsayan.simple_emoji.ui.EmojiconsPopup

class EmojIconActions : View.OnFocusChangeListener {

    private var useSystemEmoji = false
    private var popup: EmojiconsPopup? = null
    private var context: Context? = null
    private var rootView: View? = null
    private var emojiButton: ImageView? = null
    private var KeyBoardIcon = R.drawable.ic_action_keyboard
    private var SmileyIcons = R.drawable.smiley
    private var keyboardListener: KeyboardListener? = null
    private val emojiconEditTextList = ArrayList<EmojiconEditText>()
    private var emojiconEditText: EmojiconEditText? = null


    constructor(ctx: Context, rootView: View, emojiconEditText: EmojiconEditText, emojiButton: ImageView, iconPressedColor: String, tabsColor: String, backgroundColor: String) {
        addEmojiconEditTextList(emojiconEditText)
        this.emojiButton = emojiButton
        this.context = ctx
        this.rootView = rootView
        this.popup = EmojiconsPopup(rootView, ctx, useSystemEmoji, iconPressedColor,
                tabsColor, backgroundColor)

    }

    constructor(ctx: Context, rootView: View, emojiconEditText: EmojiconEditText, emojiButton: ImageView){
        this.emojiButton = emojiButton
        this.context = ctx
        this.rootView = rootView
        addEmojiconEditTextList(emojiconEditText)
        this.popup = EmojiconsPopup(rootView, ctx, useSystemEmoji)
    }


    fun addEmojiconEditTextList(vararg emojiconEditText: EmojiconEditText) {
        Collections.addAll<EmojiconEditText>(emojiconEditTextList, *emojiconEditText)
        for (editText in emojiconEditText) {
            editText.onFocusChangeListener = this
        }
    }


    fun setIconsIds(keyboardIcon: Int, smileyIcon: Int) {
        this.KeyBoardIcon = keyboardIcon
        this.SmileyIcons = smileyIcon
    }

    fun setUseSystemEmoji(useSystemEmoji: Boolean) {
        this.useSystemEmoji = useSystemEmoji
        for (editText in emojiconEditTextList) {
            editText.setUseSystemDefault(useSystemEmoji)
        }
        refresh()
    }


    private fun refresh() {
        popup?.updateUseSystemDefault(useSystemEmoji)
    }

    fun closeEmojIcon() {
        if (popup != null && popup?.isShowing!!)
            popup?.dismiss()

    }

    private fun changeEmojiKeyboardIcon(iconToBeChanged: ImageView, drawableResourceId: Int) {
        iconToBeChanged.setImageResource(drawableResourceId)
    }


    fun showEmojIcon() {
        if (emojiconEditText == null)
            emojiconEditText = emojiconEditTextList[0]
        //Will automatically set size according to the soft keyboard size
        if (popup != null) {
            popup?.setSizeForSoftKeyboard()
        }

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup?.setOnDismissListener(PopupWindow.OnDismissListener { emojiButton?.let {
            changeEmojiKeyboardIcon(
                it, SmileyIcons)
        } })

        //If the text keyboard closes, also dismiss the emoji popup
        popup?.setOnSoftKeyboardOpenCloseListener(object : EmojiconsPopup.OnSoftKeyboardOpenCloseListener {

            override fun onKeyboardOpen(keyBoardHeight: Int) {
                if (keyboardListener != null)
                    keyboardListener?.onKeyboardOpen()
            }

            override fun onKeyboardClose() {
                if (keyboardListener != null)
                    keyboardListener?.onKeyboardClose()
                if (popup?.isShowing!!)
                    popup?.dismiss()
            }
        })

        //On emoji clicked, add it to edittext
        popup?.setOnEmojiconClickedListener2(object : EmojiconGridView.OnEmojiconClickedListener {

            override fun onEmojiconClicked(emojicon: Emojicon) {
                if (emojicon == null) {
                    return
                }

                val start = emojiconEditText?.selectionStart
                val end = emojiconEditText?.selectionEnd
                if (start != null && end != null) {
                    if (start < 0) {
                        emojiconEditText?.append(emojicon.getEmoji())
                    } else {
                        emojiconEditText?.text?.replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length)
                    }
                }
            }
        })

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup?.setOnEmojiconBackspaceClickedListener(object : EmojiconsPopup.OnEmojiconBackspaceClickedListener {

            override fun onEmojiconBackspaceClicked(v: View) {
                val event = KeyEvent(
                        0, 0, 0, KEYCODE_DEL, 0, 0, 0, 0, KEYCODE_ENDCALL)
                emojiconEditText?.dispatchKeyEvent(event)
            }
        })

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        showForEditText()
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun showForEditText() {
        if (emojiButton != null) {
            emojiButton?.setOnClickListener(View.OnClickListener {
                if (emojiconEditText == null)
                    emojiconEditText = emojiconEditTextList.get(0)
                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup?.isShowing!!) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup?.isKeyBoardOpen()!!) {
                        popup?.showAtBottom()
                        changeEmojiKeyboardIcon(emojiButton!!, KeyBoardIcon)
                    } else {
                        emojiconEditText?.isFocusableInTouchMode = true
                        emojiconEditText?.requestFocus()
                        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT)
                        popup?.showAtBottomPending()
                        changeEmojiKeyboardIcon(emojiButton!!, KeyBoardIcon)
                    }//else, open the text keyboard first and immediately after that show the
                    // emoji popup
                } else {
                    popup?.dismiss()
                }//If popup is showing, simply dismiss it to show the undelying text keyboard
            })
        }
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            if (view is EmojiconEditText) {
                emojiconEditText = view
            }
        }
    }


    interface KeyboardListener {
        fun onKeyboardOpen()

        fun onKeyboardClose()
    }

    fun setKeyboardListener(listener: KeyboardListener) {
        this.keyboardListener = listener
    }
}