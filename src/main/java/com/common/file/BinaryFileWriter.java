package com.common.file;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryFileWriter {
    String filePath;
    DataOutputStream writer;

    public BinaryFileWriter(String filePath) {
        if (filePath == null || filePath.trim().length() <= 0) {
            filePath = "bfw";
        }
        this.filePath = filePath;
    }

    public void write(byte[] data, int off, int len) {
        if (!isOpen()) {
            System.out.printf("Please call open() firstly, %s\n", filePath);
            return;
        }

        try {
            writer.write(data, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        if (!isOpen()) {
            System.out.printf("Please call open() firstly, %s\n", filePath);
            return;
        }

        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return writer != null;
    }

    public boolean open() {
        if (isOpen()) {
            close();
        }

        // 创建文件对象
        File f = new File(filePath);
        // 创建文件路径
        if (f.getParentFile() != null && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        try {
            writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
        } catch (FileNotFoundException e) {
            writer = null;
            e.printStackTrace();
        }
        return isOpen();
    }

    public void close() {
        if (!isOpen()) {
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            writer = null;
        }
    }
}
