package com.example.demojwtauth.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;

public class SortFileByDate {
    public static void main(String[] args) {
        File dir = new File("C:\\Users\\Anton\\Projects\\demojwtauth\\downloadFiles\\admin");
        File[] files = dir.listFiles();
        if(files == null){
            System.out.println("No files");
            return;
        }
        sortFilesByDateCreated(files, false);
        printFiles(files);

    }
    public static void sortFilesByDateCreated(File[] files, boolean descending){
        Comparator<File> comparator = Comparator.comparingLong(SortFileByDate::getFileCreationEpoch);
        if(descending){
            comparator = comparator.reversed();
        }
        Arrays.sort(files, comparator);
    }
    private static long getFileCreationEpoch(File file){
        try{
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
            return attr.creationTime()
                    .toInstant()
                    .toEpochMilli();
        }catch (IOException e){
            throw new RuntimeException();
        }
    }
    private static void printFiles(File[] files){
        for(File file: files){
            long m = getFileCreationEpoch(file);
            Instant instant = Instant.ofEpochMilli(m);
            LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            System.out.println(date + " - " + file.getName());
        }
    }
}
