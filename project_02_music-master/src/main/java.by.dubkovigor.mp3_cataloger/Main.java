import customexceptions.IllegalNameDirectoryException;
import entities.Song;
import service.FileService;
import service.HtmlService;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IllegalNameDirectoryException {
        int numberDirection = 1;
        for(int i = 0; i < args.length-1; i++) {
            String pathFile = args[i];
            System.out.println("...start parsing direction " + pathFile + "...");
            FileService fileService = new FileService();
            HtmlService htmlService = new HtmlService();
            File dir = new File(pathFile.trim());

            ArrayList<Song> listOfAllSongs;
            long startTime = System.currentTimeMillis();
            listOfAllSongs = fileService.parseDirectory(dir);
            long finishTime = System.currentTimeMillis();
            System.out.println("---------" + (finishTime - startTime) / 1000 + "sec - parseDirectory()" + "---------");

            System.out.println("number of files is not null:"+ listOfAllSongs.size());

            List<Song> listOfSongs;
            listOfSongs = fileService.sortingSongsByArtistAndAlbum();
            htmlService.generateHtmlForSortedSongByAuthorAndAlbum(listOfSongs, "Sorting songs by artist and album_" + String.valueOf(numberDirection));

            Map<File, String> sortingSong2;
            sortingSong2 = fileService.sortingSongByDuplicateControlSum(listOfSongs);
            htmlService.generateHtmlForSortedSongsByDuplicateControlSum(sortingSong2, "Sorting songs by control sum_" + String.valueOf(numberDirection));

            Map<String, String> sortingSong3;
            sortingSong3 = fileService.sortingSongsByDuplicateParameters(listOfAllSongs);
            htmlService.generateHtmlForSortedSongsByDuplicateParameters(sortingSong3, "Sorting songs by artist, song name and album_" + String.valueOf(numberDirection));
            System.out.println("...end parsing direction " + numberDirection + "...\n" );
            numberDirection++;
        }
    }
}
