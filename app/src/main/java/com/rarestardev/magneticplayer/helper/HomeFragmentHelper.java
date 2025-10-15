package com.rarestardev.magneticplayer.helper;

import com.rarestardev.magneticplayer.model.AlbumInfo;
import com.rarestardev.magneticplayer.model.LocalArtist;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragmentHelper {

    public static List<LocalArtist> getArtistFromMusic(List<MusicFile> musicFiles) {
        Map<String, LocalArtist> artistMap = new HashMap<>();

        for (MusicFile file : musicFiles) {
            if (file != null) {
                String artistName = file.getArtistName();
                String artistCover = file.getAlbumArtUri();

                if (artistMap.containsKey(artistName)) {
                    LocalArtist existingAlbum = artistMap.get(artistName);
                    int currentTrackCount = existingAlbum.getSongCount();
                    existingAlbum.setSongCount(currentTrackCount + 1);
                } else {
                    int songCount = 0;
                    for (LocalArtist localArtist : artistMap.values()) {
                        songCount = localArtist.getSongCount();
                        localArtist.setSongCount(songCount);
                    }
                    LocalArtist localArtist = new LocalArtist();
                    localArtist.setArtistName(artistName);
                    localArtist.setArtistCover(artistCover);
                    localArtist.setSongCount(songCount);
                    artistMap.put(artistName, localArtist);
                }
            }
        }

        return new ArrayList<>(artistMap.values());
    }

    public static List<AlbumInfo> getAlbumFromMusic(List<MusicFile> musicFiles) {
        Map<String, AlbumInfo> albumMap = new HashMap<>();

        for (MusicFile file : musicFiles) {
            if (file != null) {
                String albumName = file.getAlbumName();
                String artistName = file.getArtistName();
                String albumArt = file.getAlbumArtUri();

                if (albumMap.containsKey(albumName)) {
                    AlbumInfo existingAlbum = albumMap.get(albumName);
                    int currentTrackCount = existingAlbum.getSongCount();
                    existingAlbum.setSongCount(currentTrackCount + 1);
                } else {
                    int songCount = 0;
                    for (AlbumInfo albumInfo : albumMap.values()) {
                        songCount = albumInfo.getSongCount();
                        albumInfo.setSongCount(songCount);
                    }
                    AlbumInfo albumInfo = new AlbumInfo(albumName, artistName, albumArt, songCount);
                    albumMap.put(albumName, albumInfo);
                }
            }
        }

        return new ArrayList<>(albumMap.values());
    }
}
