package com.iceteaviet.fastfoodfinder.data.remote.store.model

import java.io.Serializable

class Comment(var userName: String?, var avatar: String?, var content: String?, var mediaUrl: String?, var date: String?, var rating: Int) : Serializable
