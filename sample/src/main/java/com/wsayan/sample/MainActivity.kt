package com.wsayan.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.wsayan.simple_emoji.action.EmojIconActions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize
        val emoji = EmojIconActions(applicationContext, root_view, emojicon_edit_text, emoji_btn)
        emoji.showEmojIcon()

        // keyboard listener
        emoji.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {
                Log.e("Keyboard", "open")
            }

            override fun onKeyboardClose() {
                Log.e("Keyboard", "close")
            }
        })

        // system default emojis
        use_system_default.setOnCheckedChangeListener { _, b ->
            emoji.setUseSystemEmoji(b);
            textView.setUseSystemDefault(b);
        }

        emoji.addEmojiconEditTextList(emojicon_edit_text2)

        submit_btn.setOnClickListener {
            val newText = emojicon_edit_text.text.toString()
            textView.text = newText
        }
    }
}