package com.wsayan.simple_emoji.ui

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.view.ViewGroup
import com.wsayan.simple_emoji.emoji.Emojicon
import com.wsayan.simple_emoji.R

class EmojiAdapter: ArrayAdapter<Emojicon> {
    private var mUseSystemDefault = false
    private var emojiClickListener: EmojiconGridView.OnEmojiconClickedListener? = null

    constructor(context: Context, data: List<Emojicon>, useSystemDefault: Boolean ) : super(context, R.layout.emojicon_item, data){
        mUseSystemDefault = useSystemDefault
    }

    constructor(context: Context, data: Array<Emojicon>, useSystemDefault: Boolean): super(context, R.layout.emojicon_item, data){
        mUseSystemDefault = useSystemDefault
    }

    public fun setEmojiClickListener(listener: EmojiconGridView.OnEmojiconClickedListener) {
        this.emojiClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View?
        val vh: ViewHolder

        if (convertView == null) {
            v = View.inflate(context, R.layout.emojicon_item, null)
            vh = ViewHolder(v)
            v?.setTag(vh)
        }else{
            v = convertView
            vh = v.tag as ViewHolder
        }

        val emoji = getItem(position)
        vh.icon?.text = emoji?.getEmoji()
        vh.icon?.setUseSystemDefault(mUseSystemDefault)
        vh.icon?.setOnClickListener { emojiClickListener?.onEmojiconClicked(getItem(position)) }

        return v!!
    }

    private class ViewHolder(row : View?){
        var icon : EmojiconTextView? = null

        init {
            this.icon = row?.findViewById<View>(R.id.emojicon_icon) as EmojiconTextView

        }
    }
}