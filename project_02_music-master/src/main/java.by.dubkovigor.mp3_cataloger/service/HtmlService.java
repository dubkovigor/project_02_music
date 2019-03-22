package service;

import entities.Song;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static constant.Html.*;

public class HtmlService {
    final private String HTML_TOP = "<!DOCTYPE html>\n"+
            "<html>\n"+

            "<head>"+
            "<style>"+
            ".gradient {"+
            "background: linear-gradient(to top, #fefcea, #f1da36);" +
            "}"+
            "table, th, td {"+
            "    border: 2px solid black;"+
            "}"+
            "</style>"+
            "</head>"+
            "<body>";
    final private String HTML_BOTTOM = "</html>";

    public void generateHtmlForSortedSongByAuthorAndAlbum(List<Song> listSongs, String fileName){
        try {
            File htmlFile = new File(fileName + ".html");

            PrintWriter writer = new PrintWriter(htmlFile, "UTF-8");
            writer.write(HTML_TOP);
            if (!listSongs.isEmpty()) {
                String author = "";
                String album = "";
                for (Song song : listSongs) {
                    String lines = " ";
                    if (!author.equals(song.getArtist())){
                        lines += P_OPEN_NULL + song.getArtist() + P_CLOSE;
                        if (!album.equals(song.getAlbum())) {
                            lines += P_OPEN_ONE + TAB + song.getAlbum() + P_CLOSE +
                                    P_OPEN_TWO + TAB + TAB +
                                    song.getSongTitle() + " " +
                                    song.getSongDuration() + " " +
                                    "<a href=\"" + song.getSongUrl() + "\">" + song.getSongUrl() + "</a>"+
                                    P_CLOSE;
                        }
                        else {
                            lines += P_OPEN_TWO + TAB + TAB +
                                    song.getSongTitle() + " " +
                                    song.getSongDuration() + " " +
                                    "<a href=\"" + song.getSongUrl() + "\">" + song.getSongUrl() + "</a>"+
                                    P_CLOSE;
                        }
                    }
                    else {
                        lines += P_OPEN_TWO + TAB + TAB +
                                song.getSongTitle() + " " +
                                song.getSongDuration() + " " +
                                "<a href=\"" + song.getSongUrl() + "\">" + song.getSongUrl() + "</a>"+
                                P_CLOSE;
                    }
                    writer.write(lines);
                    author = song.getArtist();
                    album = song.getAlbum();
                }
            }
            writer.write(HTML_BOTTOM);
            writer.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File does not exist");
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            System.out.println("The Character Encoding is not supported");
        }
    }

    public void generateHtmlForSortedSongsByDuplicateParameters(Map<String, String> listSortingSongsForHashMap, String fileName) {
        try{
            File htmlFile = new File(fileName + ".html");
            PrintWriter writer = new PrintWriter(htmlFile, "UTF-8");
            writer.write(HTML_TOP);

            if (!listSortingSongsForHashMap.isEmpty()) {
                String duplicateParameters = "";
                for (Map.Entry<String, String> entry : listSortingSongsForHashMap.entrySet()) {
                    String lines = " ";
                    if (!duplicateParameters.equals(String.valueOf(entry.getValue()))){
                        lines += P_OPEN_NULL + "Duplicate: " + entry.getValue() + P_CLOSE;
                            lines += P_OPEN_ONE +
                                    entry.getKey()+ P_CLOSE;
                    }
                    else {
                        lines += P_OPEN_ONE +
                                entry.getKey()+ P_CLOSE;
                    }
                    writer.write(lines);
                    duplicateParameters = entry.getValue();
                }
            }
            writer.write(HTML_BOTTOM);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File does not exist");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            System.out.println("The Character Encoding is not supported");
        }
    }

    public void generateHtmlForSortedSongsByDuplicateControlSum(Map<File, String> listSortingSongsForControlSum, String fileName) {
        try {
            File htmlFile = new File(fileName + ".html");
            PrintWriter writer = new PrintWriter(htmlFile, "UTF-8");
            writer.write(HTML_TOP);

            if (!listSortingSongsForControlSum.isEmpty()) {
                String duplicateControlSum = "";
                for (Map.Entry<File, String> entry : listSortingSongsForControlSum.entrySet()) {
                    String lines = " ";
                    if (!duplicateControlSum.equals(String.valueOf(entry.getValue()))) {
                        lines += P_OPEN_NULL + "Duplicate control sum: " + entry.getValue() + P_CLOSE;
                        lines += P_OPEN_ONE +
                                entry.getKey() + P_CLOSE;
                    } else {
                        lines += P_OPEN_ONE +
                                entry.getKey() + P_CLOSE;
                    }
                    writer.write(lines);
                    duplicateControlSum = String.valueOf(entry.getValue());
                }
            }
            writer.write(HTML_BOTTOM);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File does not exist");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            System.out.println("The Character Encoding is not supported");
        }
    }
}
