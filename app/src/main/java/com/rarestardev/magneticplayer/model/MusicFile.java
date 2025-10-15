package com.rarestardev.magneticplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "music_tracks")
public class MusicFile implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "musicFileId")
    private int musicFileId;

    @ColumnInfo(name = "filePath")
    private String filePath;

    @ColumnInfo(name = "artistName")
    private String artistName;

    @ColumnInfo(name = "albumName")
    private String albumName;

    @ColumnInfo(name = "music_genre")
    private String musicGenre;

    @ColumnInfo(name = "duration")
    private long duration;

    @ColumnInfo(name = "albumArtUri")
    private String albumArtUri;

    @ColumnInfo(name = "songTitle")
    private String songTitle;

    @ColumnInfo(name = "dateAdded")
    private long dateAdded;

    @Ignore
    public MusicFile(int musicFileId ,String filePath, String artistName, String albumName,String musicGenre, long duration, String albumArtUri, String songTitle, long dateAdded) {
        this.musicFileId = musicFileId;
        this.filePath = filePath;
        this.artistName = artistName;
        this.albumName = albumName;
        this.musicGenre = musicGenre;
        this.duration = duration;
        this.albumArtUri = albumArtUri;
        this.songTitle = songTitle;
        this.dateAdded = dateAdded;
    }

    public MusicFile() {
    }

    @Ignore
    protected MusicFile(Parcel in) {
        musicFileId = in.readInt();
        filePath = in.readString();
        artistName = in.readString();
        albumName = in.readString();
        musicGenre = in.readString();
        duration = in.readLong();
        albumArtUri = in.readString();
        songTitle = in.readString();
        dateAdded = in.readLong();
    }

    @Ignore
    public static final Creator<MusicFile> CREATOR = new Creator<>() {
        @Override
        public MusicFile createFromParcel(Parcel in) {
            return new MusicFile(in);
        }

        @Override
        public MusicFile[] newArray(int size) {
            return new MusicFile[size];
        }
    };

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(musicFileId);
        dest.writeString(filePath);
        dest.writeString(artistName);
        dest.writeString(albumName);
        dest.writeString(musicGenre);
        dest.writeLong(duration);
        dest.writeString(albumArtUri);
        dest.writeString(songTitle);
        dest.writeLong(dateAdded);
    }

    public String getFilePath() {
        return filePath;
    }

    public int getMusicFileId() {
        return musicFileId;
    }

    public void setMusicFileId(int musicFileId) {
        this.musicFileId = musicFileId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getMusicGenre() {
        return musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }

    public long getDuration() {
        return duration;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
