package com.iceteaviet.fastfoodfinder.data.remote.store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Comment(var userName: String, var avatar: String, var content: String, var mediaUrl: String, var timestamp: Long) : Parcelable {

    @Exclude
    @get:Exclude
    @set:Exclude
    var id: String? = null

    constructor() : this("", "", "", "", 0L)

    constructor(id: String, userName: String, avatar: String, content: String, mediaUrl: String, timestamp: Long) : this(
            userName,
            avatar,
            content,
            mediaUrl,
            timestamp) {
        this.id = id
    }

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userName)
        parcel.writeString(avatar)
        parcel.writeString(content)
        parcel.writeString(mediaUrl)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
