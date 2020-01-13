package com.common.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BinaryFileReader {
    String filePath;
    BufferedInputStream reader;
    long fileLen;

    public BinaryFileReader(String filePath) {
        if (filePath == null || filePath.trim().length() <= 0) {
            filePath = "bfr";
        }
        this.filePath = filePath;
    }

    public byte[] read() {
        if (!isOpen()) {
            System.out.printf("Please call open() firstly, %s\n", filePath);
            return null;
        }
        byte[] data = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)fileLen);
        try {
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];

            int len;
            while(-1 != (len = reader.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }

            data = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public int read(byte[] data) {
        if (!isOpen()) {
            System.out.printf("Please call open() firstly, %s\n", filePath);
            return 0;
        }

        try {
            return reader.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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
            return false;
        }
        fileLen = file.length();

        try {
            reader = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            reader = null;
            e.printStackTrace();
        }
        return isOpen();
    }

    public void close() {
        if (!isOpen()) {
            return;
        }

        try {
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            reader = null;
        }
    }
}
