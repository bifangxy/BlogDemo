package com.xy.retrofit.download.data;

import com.xy.retrofit.MyApplication;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
@Database(entities = {DownloadStatus.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase mInstance;

    private static final String DN_NAME = "db_room";

    public abstract DownloadStatusDao downloadStatusDao();

    public static AppDatabase getInstance(){
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(MyApplication.getAppContext(),
                            AppDatabase.class, DN_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return mInstance;
    }
}
