package com.rarestardev.magneticplayer.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.MusicFolder;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicUtils {

    public static String getAlbumArtUri(Context context, long albumId) {
        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
        ContentResolver resolver = context.getContentResolver();

        try (InputStream inputStream = resolver.openInputStream(albumArtUri)) {
            if (inputStream != null) {
                return albumArtUri.toString();
            }
        } catch (Exception e) {
            Log.e("AlbumArt", "Album art not found for albumId: " + albumId);
        }

        return "";
    }

    public static List<MusicFolder> getMusicFolders(Context context) {
        List<MusicFolder> musicFolders = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            HashMap<String, Integer> folderMap = new HashMap<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(0);
                File file = new File(filePath);
                String folderPath = file.getParent();
                folderMap.put(folderPath, folderMap.getOrDefault(folderPath, 0) + 1);
            }
            cursor.close();

            for (Map.Entry<String, Integer> entry : folderMap.entrySet()) {
                String folderPath = entry.getKey();
                int count = entry.getValue();
                File folder = new File(folderPath);
                musicFolders.add(new MusicFolder(folder.getName(), folderPath, count));
            }
        }
        return musicFolders;
    }

    public static List<MusicFile> getMusicFilesFromFolder(Context context, String folderPath) {
        List<MusicFile> musicFiles = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{folderPath + "%"};
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        String order = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, order);

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
                String albumArtUri = getAlbumArtUri(context, cursor.getLong(albumArtIndex));  // cover uri
                String songTitle = cursor.getString(titleIndex);
                long dateAdded = cursor.getLong(dateAddedIndex);

                musicFiles.add(new MusicFile(id, filePath, artist, album, duration, albumArtUri, songTitle, dateAdded));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return musicFiles;
    }

    public static ArrayList<MusicFile> getMusicFilesFromSearch(Context context, String query) {
        ArrayList<MusicFile> musicFiles = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.TITLE + " like ?";
        String[] selectionArgs = new String[]{query + "%"};
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        String order = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, order);

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
                String albumArtUri = getAlbumArtUri(context, cursor.getLong(albumArtIndex));  // cover uri
                String songTitle = cursor.getString(titleIndex);
                long dateAdded = cursor.getLong(dateAddedIndex);

                musicFiles.add(new MusicFile(id, filePath, artist, album, duration, albumArtUri, songTitle, dateAdded));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return musicFiles;
    }

}

