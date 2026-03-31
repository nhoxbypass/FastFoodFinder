package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import com.iceteaviet.fastfoodfinder.R
import io.realm.Realm
import io.realm.RealmConfiguration

object DatabaseInitializer {
    fun init(context: Context) {
        Realm.init(context)
        val key = base64ToBytes(context.getString(R.string.realm_db_encryption_key))
        val config = RealmConfiguration.Builder()
            .encryptionKey(key)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}
