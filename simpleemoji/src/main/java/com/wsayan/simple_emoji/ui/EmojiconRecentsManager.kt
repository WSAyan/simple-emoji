package com.wsayan.simple_emoji.ui

import android.content.Context
import android.content.SharedPreferences
import com.wsayan.simple_emoji.emoji.Emojicon
import java.util.*

class EmojiconRecentsManager(ctx: Context): ArrayList<Emojicon>() {

    private val mContext: Context? = ctx.applicationContext

    init {
        loadRecents()
    }

    fun getRecentPage(): Int {
        return getPreferences().getInt(PREF_PAGE, 0)
    }

    fun setRecentPage(page: Int) {
        getPreferences().edit().putInt(PREF_PAGE, page).commit()
    }

    fun push(`object`: Emojicon) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(`object`)) {
            super.remove(`object`)
        }
        add(0, `object`)
    }

    override fun add(`object`: Emojicon): Boolean {
        return super.add(`object`)
    }

    override fun add(index: Int, `object`: Emojicon) {
        super.add(index, `object`)
    }

    override fun remove(`object`: Emojicon): Boolean {
        return super.remove(`object`)
    }

    private fun getPreferences(): SharedPreferences {
        return mContext?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
    }

    private fun loadRecents() {
        val prefs = getPreferences()
        val str = prefs.getString(PREF_RECENTS, "")
        val tokenizer = StringTokenizer(str, "~")
        while (tokenizer.hasMoreTokens()) {
            try {
                add(Emojicon(tokenizer.nextToken()))
            } catch (e: NumberFormatException) {
                // ignored
            }

        }
    }

    fun saveRecents() {
        val str = StringBuilder()
        val c = size
        for (i in 0 until c) {
            val e = get(i)
            str.append(e.getEmoji())
            if (i < c - 1) {
                str.append('~')
            }
        }
        val prefs = getPreferences()
        prefs.edit().putString(PREF_RECENTS, str.toString()).commit()
    }

    companion object {
        private val PREFERENCE_NAME = "emojicon"
        private val PREF_RECENTS = "recent_emojis"
        private val PREF_PAGE = "recent_page"

        private val LOCK = Any()
        private var sInstance: EmojiconRecentsManager? = null

        fun getInstance(context: Context): EmojiconRecentsManager {
            if (sInstance == null) {
                synchronized(LOCK) {
                    if (sInstance == null) {
                        sInstance = EmojiconRecentsManager(context)
                    }
                }
            }
            return sInstance!!
        }
    }
}