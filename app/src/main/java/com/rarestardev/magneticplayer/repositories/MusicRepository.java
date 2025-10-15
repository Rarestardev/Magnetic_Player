package com.rarestardev.magneticplayer.repositories;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

    public Observable<List<MusicFile>> loadMusic() {
        return Observable.create(emitter -> {
            Map<String, MusicFile> musicFiles = new HashMap<>();
            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection(),
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int albumArtIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int dateAddedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);

                do {
                    int id = cursor.getInt(idIndex);
                    String filePath = cursor.getString(dataIndex);
                    String artist = cursor.getString(artistIndex);
                    String album = cursor.getString(albumIndex);
                    long duration = cursor.getLong(durationIndex);
                    String albumArtUri = MusicUtils.getAlbumArtUri(context, cursor.getLong(albumArtIndex));
                    String songTitle = cursor.getString(titleIndex);
                    long dateAdded = cursor.getLong(dateAddedIndex);

                    String genre = getGenreForAudioId(id);
                    if (genre == null || genre.trim().isEmpty()) {
                        genre = "Unknown";
                    }

                    MusicFile musicFile = new MusicFile(id, filePath, artist, album, genre, duration, albumArtUri, songTitle, dateAdded);
                    musicFiles.put(filePath, musicFile);
                } while (cursor.moveToNext());

                cursor.close();
            }

            emitter.onNext(new ArrayList<>(musicFiles.values()));
            emitter.onComplete();
        });
    }

    @SuppressLint("Range")
    private String getGenreForAudioId(int audioId) {
        Uri genreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", audioId);
        Cursor genreCursor = context.getContentResolver().query(
                genreUri,
                new String[]{MediaStore.Audio.Genres.NAME},
                null,
                null,
                null
        );

        String genre = null;
        if (genreCursor != null && genreCursor.moveToFirst()) {
            genre = genreCursor.getString(genreCursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
            genreCursor.close();
        }

        return genre;
    }

    private String[] projection() {
        return new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATE_ADDED
        };
    }
}
