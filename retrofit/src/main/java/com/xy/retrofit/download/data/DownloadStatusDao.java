package com.xy.retrofit.download.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
@Dao
public interface DownloadStatusDao {

    @Query("SELECT * FROM download_status WHERE name = :name")
    DownloadStatus getDownloadStatusByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDownloadStatus(DownloadStatus downloadStatus);

    @Query("UPDATE download_status SET currentIndex = :currentIndex WHERE name = :name")
    void updateCurrentIndexByName(String name, long currentIndex);

    @Query("UPDATE download_status SET status = :status WHERE name = :name")
    void updateStatusByName(String name, int status);

    @Query("DELETE FROM download_status WHERE url = :url")
    void deleteDataByUrl(String url);

    @Query("DELETE FROM download_status WHERE name = :name")
    void deleteDataByName(String name);
}
