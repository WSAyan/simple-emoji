package com.wsayan.simple_emoji.emoji

import android.os.Parcel
import android.os.Parcelable

class Emojicon: Parcelable {

    private var icon: Int = 0

    private var value: Char = ' '

    private var emoji: String? = null


    constructor(`in`: Parcel) {
        this.icon = `in`.readInt()
        this.value = `in`.readInt().toChar()
        this.emoji = `in`.readString()
    }


    constructor(icon: Int, value: Char, emoji: String) : super() {
        this.icon = icon;
        this.value = value;
        this.emoji = emoji;
    }

    constructor(emoji: String): super(){
        this.emoji = emoji
    }

    override fun equals(o: Any?): Boolean {
        return o is Emojicon && emoji.equals(o.emoji)
    }

    override fun hashCode(): Int {
        return emoji?.hashCode()!!
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(icon)
        dest?.writeInt(value.toInt())
        dest?.writeString(emoji)
    }

    override fun describeContents(): Int {
        return 0
    }


    fun getValue(): Char {
        return value
    }

    fun getIcon(): Int {
        return icon
    }

    fun getEmoji(): String {
        return this.emoji!!
    }

    companion object {
        val CREATOR: Parcelable.Creator<Emojicon> = object : Parcelable.Creator<Emojicon> {
            override fun createFromParcel(`in`: Parcel): Emojicon {
                return Emojicon(`in`)
            }

            override fun newArray(size: Int): Array<Emojicon?> {
                return arrayOfNulls(size)
            }
        }

        fun fromResource(icon: Int, value: Int): Emojicon {
            val emoji = Emojicon()
            emoji.icon = icon
            emoji.value = value.toChar()
            return emoji
        }

        fun fromCodePoint(codePoint: Int): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = newString(codePoint)
            return emoji
        }

        fun fromChar(ch: Char): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = Character.toString(ch)
            return emoji
        }

        fun fromChars(chars: String): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = chars
            return emoji
        }

        fun newString(codePoint: Int): String {
            return if (Character.charCount(codePoint) == 1) {
                codePoint.toString()
            } else {
                String(Character.toChars(codePoint))
            }
        }
    }

    constructor()


}