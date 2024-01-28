package com.iceteaviet.fastfoodfinder.data.remote.store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Comment(var userName: String, var avatar: String, var content: String, var mediaUrl: String, var timestamp: Long) : Parcelable {

    @Exclude
    @get:Exclude
    @set:Exclude
    var id: String = ""

    constructor() : this("", "", "", "", 0L)

    constructor(id: String, userName: String, avatar: String, content: String, mediaUrl: String, timestamp: Long)
        : this(userName, avatar, content, mediaUrl, timestamp) {
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

    override fun equals(other: Any?): Boolean {
        return if (other is Comment) {
            id.equals(other.id) && userName.equals(other.userName) && avatar.equals(other.avatar)
                && content.equals(other.content) && mediaUrl.equals(other.mediaUrl) && Math.abs(timestamp - other.timestamp) < 60000
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + mediaUrl.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
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
