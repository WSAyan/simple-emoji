# simple-emoji

Kotlin upgraded version of [SuperNova-Emoji](https://github.com/hani-momanii/SuperNova-Emoji)

Fully migrated to androidx. 
Appropriate package names for databinding. 

## Usage

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency
```gradle
dependencies {
	        implementation 'com.github.WSAyan:simple-emoji:1.0.0'
	}
```

Add EmojiconEditText and EmojiconEditText for rendered emojis. 
```xml
<com.wsayan.simple_emoji.ui.EmojiconEditText
        android:id="@+id/edit_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        emojicon:emojiconSize="28sp"/>

<com.wsayan.simple_emoji.ui.EmojiconEditText
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        emojicon:emojiconAlignment="bottom"/>

<ImageView
        android:id="@+id/emoji_btn"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@drawable/smiley" />
```

You can use this inside *onCreate* method to show emojis panel on *emoji_btn* click. 
```kotlin
val emoji = EmojIconActions(applicationContext, root_view, emojicon_edit_text, emoji_btn)
        emoji.showEmojIcon()
```

## Acknowledgements

Based on [SuperNova-Emoji](https://github.com/hani-momanii/SuperNova-Emoji).

Inspired from [Supernova-Emoji-Kotlin](https://github.com/dbh4ck/Supernova-Emoji-Kotlin)