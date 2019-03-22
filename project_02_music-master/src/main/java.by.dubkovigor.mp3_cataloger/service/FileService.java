package service;

import com.mpatric.mp3agic.*;
import customexceptions.IllegalNameDirectoryException;
import entities.Song;
import org.apache.commons.codec.digest.DigestUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class FileService {
    ArrayList<Song> listOfSongs = new ArrayList<Song>();

    private long songDuration(String fileName){
        try {
            java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
            AudioFile audioFile = AudioFileIO.read(new File(fileName));
            return audioFile.getAudioHeader().getTrackLength();
        }catch (TagException | ReadOnlyFileException | CannotReadException |InvalidAudioFrameException | IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private Song parseMp3(String fileName) {
        try {
            //образотать fileName
            Mp3File song = new Mp3File(fileName);
            Song fileMP3;
            if (song.hasId3v2Tag()) {
                String album;
                String artist;
                String title;
                ID3v2 id3v2tag = song.getId3v2Tag();

                album = id3v2tag.getAlbum() != null ? id3v2tag.getAlbum() : "Unknown album";
                title = id3v2tag.getTitle() != null ? id3v2tag.getTitle() : "Unknown title";

                if (id3v2tag.getArtist() != null) {
                    artist = id3v2tag.getArtist();
                } else if (id3v2tag.getAlbumArtist() != null) {
                    artist = id3v2tag.getAlbumArtist();
                } else {
                    artist = "Unknown artist";
                }

                return fileMP3 = new Song(artist, album, title, fileName, songDuration(fileName));
            } else if (song.hasId3v1Tag()) {
                String album;
                String artist;
                String title;
                ID3v1 id3v1tag = song.getId3v1Tag();

                album = id3v1tag.getAlbum() != null ? id3v1tag.getAlbum() : "Unknown album";
                title = id3v1tag.getTitle() != null ? id3v1tag.getTitle() : "Unknown title";

                if (id3v1tag.getArtist() != null) {
                    artist = id3v1tag.getArtist();
                } else {
                    artist = "Unknown artist";
                }

                return fileMP3 = new Song(artist, album, title, fileName, songDuration(fileName));
            } else {
                fileMP3 = null;
            }
            return fileMP3;
        }catch (InvalidDataException | IOException | UnsupportedTagException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<Song> parseDirectory(File rootDirectory) throws IllegalNameDirectoryException{

        if (rootDirectory.isDirectory()) {
            for (File item : rootDirectory.listFiles()) {
                String nameFile = item.getName();
                if (item.isDirectory()) {
                    parseDirectory(item);
                } else if (nameFile.endsWith(".mp3")) {
                    if(parseMp3(item.getPath()) != null){
                        listOfSongs.add(parseMp3(item.getPath()));
                    }
                }
            }
        } else if (rootDirectory.getName().endsWith(".mp3")) {
            listOfSongs.add(parseMp3(rootDirectory.getPath()));
        } else if(!rootDirectory.isDirectory() && !rootDirectory.getName().endsWith(".mp3")){
            throw new IllegalNameDirectoryException("Directory path is invalid");
        }

        return listOfSongs;
    }

    public List<Song> sortingSongsByArtistAndAlbum() {
        long startTime = System.currentTimeMillis();
        List<Song> some;
        if(listOfSongs.size() >= 150){
            some = listOfSongs.stream().parallel()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Song::getArtist)
                            .thenComparing(Song::getAlbum).reversed()
                    )
                    .collect(Collectors.toList());
        }
        else {
            some = listOfSongs.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Song::getArtist)
                            .thenComparing(Song::getAlbum).reversed()
                    )
                    .collect(Collectors.toList());
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("---------" + (finishTime - startTime) / 1000 + "sec - sortingSongsByArtistAndAlbum()" + "---------");
        return some;
    }

    public Map<String, String> sortingSongsByDuplicateParameters(ArrayList<Song> listOfSongs) {
        long startTime = System.currentTimeMillis();
        Map<String, String> sortedMap;
        if(listOfSongs.size() >= 150) {
            HashMap<String, String> listHashCode = new HashMap<>();
            for(Song s : listOfSongs) {
                listHashCode.put(s.getSongUrl(), s.getImportantInformation());
            }
            List<String> listDuplicateHashCode = listHashCode.values().stream().parallel()
                    .filter(p -> Collections.frequency(listHashCode.values(), p) > 1)
                    .distinct()
                    .collect(Collectors.toList());

            HashMap<String, String> listAllDuplicateHashCode = new HashMap<>();
            for(Song song : listOfSongs) {
                if(listDuplicateHashCode.stream().parallel().anyMatch(song.getImportantInformation()::equals)) {
                    listAllDuplicateHashCode.put(song.getSongUrl(), song.getImportantInformation());
                }
            }

           sortedMap =
                    listAllDuplicateHashCode.entrySet().stream().parallel()
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }else {
            HashMap<String, String> listHashCode = new HashMap<>();
            for (Song s : listOfSongs) {
                listHashCode.put(s.getSongUrl(), s.getImportantInformation());
            }
            List<String> listDuplicateHashCode = listHashCode.values().stream()
                    .filter(p -> Collections.frequency(listHashCode.values(), p) > 1)
                    .distinct()
                    .collect(Collectors.toList());

            HashMap<String, String> listAllDuplicateHashCode = new HashMap<>();
            for (Song song : listOfSongs) {
                if (listDuplicateHashCode.stream().anyMatch(song.getImportantInformation()::equals)) {
                    listAllDuplicateHashCode.put(song.getSongUrl(), song.getImportantInformation());
                }
            }

            sortedMap =
                    listAllDuplicateHashCode.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("---------" + (finishTime - startTime) / 1000 + "sec - sortingSongsByDuplicateParameters()" + "---------");

        return sortedMap;
    }

    public Map<File, String> sortingSongByDuplicateControlSum(List<Song> listOfSongs) {
        try {
            long startTime = System.currentTimeMillis();
            Map<File, String> sortedMap;
            if(listOfSongs.size() >= 150) {
                HashMap<File, String> listFileSize = new HashMap<>();
                for (Song song : listOfSongs) {
                    File songFile = new File(song.getSongUrl());
                    listFileSize.put(songFile, String.valueOf(songFile.length()));
                }
                List<String> listDuplicateFileSize = listFileSize.values().stream().parallel()
                        .filter(p -> Collections.frequency(listFileSize.values(), p) > 1)
                        .distinct()
                        .collect(Collectors.toList());

                HashMap<File, String> listSongFileWithControlSum = new HashMap<>();
                for (Song song : listOfSongs) {
                    File songFile = new File(song.getSongUrl());
                    if (listDuplicateFileSize.stream().parallel().anyMatch(String.valueOf(songFile.length())::equals)) {
                        listSongFileWithControlSum.put(songFile, DigestUtils.md5Hex(Files.newInputStream(Paths.get(song.getSongUrl()))));
                    }
                }

                sortedMap =
                        listSongFileWithControlSum.entrySet().stream().parallel()
                                .sorted(Map.Entry.comparingByValue())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (e1, e2) -> e1, LinkedHashMap::new));
            }else {
                HashMap<File, String> listFileSize = new HashMap<>();
                for (Song song : listOfSongs) {
                    File songFile = new File(song.getSongUrl());
                    listFileSize.put(songFile, String.valueOf(songFile.length()));
                }
                List<String> listDuplicateFileSize = listFileSize.values().stream()
                        .filter(p -> Collections.frequency(listFileSize.values(), p) > 1)
                        .distinct()
                        .collect(Collectors.toList());

                HashMap<File, String> listSongFileWithControlSum = new HashMap<>();
                for (Song song : listOfSongs) {
                    File songFile = new File(song.getSongUrl());
                    if (listDuplicateFileSize.stream().anyMatch(String.valueOf(songFile.length())::equals)) {
                        listSongFileWithControlSum.put(songFile, DigestUtils.md5Hex(Files.newInputStream(Paths.get(song.getSongUrl()))));
                    }
                }

                sortedMap =
                        listSongFileWithControlSum.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (e1, e2) -> e1, LinkedHashMap::new));
            }
            long finishTime = System.currentTimeMillis();
            System.out.println("---------" + (finishTime - startTime) / 1000 + "sec - sortingSongByDuplicateControlSum()" + "---------");
            return sortedMap;

        }catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
