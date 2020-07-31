package com.wsayan.simple_emoji.ui

import android.content.Context
import com.wsayan.simple_emoji.emoji.Emojicon


interface EmojiconRecents {
    fun addRecentEmoji(context: Context, emojicon: Emojicon)
}