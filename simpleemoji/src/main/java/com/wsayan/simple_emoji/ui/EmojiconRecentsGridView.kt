package com.wsayan.simple_emoji.ui

import android.content.Context
import com.wsayan.simple_emoji.emoji.Emojicon
import android.widget.GridView
import com.wsayan.simple_emoji.R

class EmojiconRecentsGridView(
    context: Context,
    emojicons: Array<Emojicon>?,
    recents: EmojiconRecents,
    emojiconsPopup: EmojiconsPopup,
    useSystemDefault: Boolean
) : EmojiconGridView(context, emojicons, recents, emojiconsPopup, useSystemDefault),
    EmojiconRecents {
    var mAdapter: EmojiAdapter? = null
    private var mUseSystemDefault = useSystemDefault

    init {
        val recents1 = rootView?.getContext()?.let { EmojiconRecentsManager.getInstance(it) }
        mAdapter = recents1?.let {
            rootView?.context?.let { it1 ->
                EmojiAdapter(
                    it1,
                    it,
                    mUseSystemDefault
                )
            }
        }
        mAdapter?.setEmojiClickListener(OnEmojiconClickedListener { emojicon ->
            mEmojiconPopup?.onEmojiconClickedListener?.onEmojiconClicked(emojicon)
        })

        val gridView = rootView?.findViewById(R.id.Emoji_GridView) as GridView
        gridView.adapter = mAdapter
        if (mAdapter != null)
            mAdapter?.notifyDataSetChanged()
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        EmojiconRecentsManager.getInstance(context).push(emojicon)
        mAdapter?.notifyDataSetChanged()
    }
}