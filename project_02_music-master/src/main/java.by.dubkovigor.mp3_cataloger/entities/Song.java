package entities;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class Song {
    private String artist;
    private String album;
    private String songTitle;
    private String songUrl;
    private String songDuration;
    private String importantInformation;

    public Song(String artist, String album, String songTitle, String songUrl, long songDuration) {
        this.artist = artist;
        this.album = album;
        this.songTitle = songTitle;
        this.songUrl = songUrl;
        this.songDuration = LocalTime.MIN.plus(Duration.ofMinutes(songDuration)).toString();
        this.importantInformation = artist + "-" + album + "-" + songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getImportantInformation() {
        return importantInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return importantInformation.equals(song.importantInformation);
    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(artist, album, songTitle);
//    }
}
