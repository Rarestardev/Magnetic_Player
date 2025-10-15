package com.rarestardev.magneticplayer.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class MusicRepository{

    private final Context context;

    public MusicRepository(Context context){
        this.context = context;
    }

    public Observable<List<MusicFile>> loadMusic(){
        return Observable.create(emitter -> {
            Map<String,MusicFile> musicFiles = new HashMap<>();

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int artistId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);  // file path
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);  // artist name
                int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);  // album name
                int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);  // music duration
                int albumArtIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);// cover id
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE); // music name
                int dateAddedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);

                do {
                    int id = cursor.getInt(artistId);
                    String filePath = cursor.getString(dataIndex);
                    String artist = cursor.getString(artistIndex);
                    String album = cursor.getString(albumIndex);
                    long duration = cursor.getLong(durationIndex);
                    String albumArtUri = MusicUtils.getAlbumArtUri(context,cursor.getLong(albumArtIndex));  // cover uri
                    String songTitle = cursor.getString(titleIndex);
                    long dateAdded = cursor.getLong(dateAddedIndex);

                    MusicFile musicFile = new MusicFile(id, filePath, artist, album, duration, albumArtUri, songTitle, dateAdded);
                    musicFiles.put(filePath,musicFile);
                } while (cursor.moveToNext());

                cursor.close();
            }

            emitter.onNext(new ArrayList<>(musicFiles.values()));
            emitter.onComplete();
        });
    }
}
