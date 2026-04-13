package com.iceteaviet.fastfoodfinder.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDao
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.StoreIdEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity

@Database(
    entities = [StoreEntity::class, UserEntity::class, UserStoreListEntity::class, StoreIdEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun userDao(): UserDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "fastfoodfinder.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
