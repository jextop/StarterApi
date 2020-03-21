package com.common.file;

import com.common.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class FileReader {
    private String filePath;
    private BufferedReader reader;
    private Boolean showInfo = true;

    public FileReader(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            filePath = "fr";
        }
        this.filePath = filePath;
    }

    public FileReader(String filePath, boolean showInfo) {
        if (StringUtils.isEmpty(filePath)) {
            filePath = "fr";
        }
        this.filePath = filePath;
        this.showInfo = showInfo;
    }

    public boolean isOpen() {
        return reader != null;
    }

    public boolean open() {
        if (isOpen()) {
            close();
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.printf("File doesn't exist: %s\n", filePath);
            return false;
        }

        try {
            reader = new BufferedReader(new java.io.FileReader(file));
        } catch (IOException e) {
            reader = null;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        boolean isOpen = isOpen();
        if (showInfo) {
            System.out.printf("FileReader open %s: %s\n", isOpen ? "successfully" : "Failed", filePath);
        }
        return isOpen;
    }

    public void close() {
        if (!isOpen()) {
            return;
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            reader = null;
        }

        if (showInfo) {
            System.out.printf("FileReader close successfully: %s\n", filePath);
        }
    }

    public String readLine() {
        if (!isOpen()) {
            System.out.println("Please call open() firstly.");
            return null;
        }

        String str = null;
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
