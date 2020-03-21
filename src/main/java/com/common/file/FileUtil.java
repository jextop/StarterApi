package com.common.file;

import com.common.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
    public static boolean mkdirs(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }

        // Check exist or not
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        // Create parent directory
        boolean ret = file.getParentFile().mkdirs();
        if (ret) {
            // Create the last folder
            if (file.isDirectory()) {
                return file.mkdirs();
            }
        }
        return ret;
    }

    public static boolean write(String filePath, String[] lines) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }

        FileWriter writer = new FileWriter(filePath, false);
        if (!writer.open()) {
            System.out.printf("Fail to create output file: %s\n", filePath);
            return false;
        }

        // Write
        if (lines != null && lines.length > 0) {
            for (String line : lines) {
                writer.writeLine(line);
            }
        }

        // Close
        writer.close();
        return true;
    }

    public static boolean write(String filePath, byte[] data) {
        if (StringUtils.isEmpty(filePath) || data == null || data.length <= 0) {
            return false;
        }

        BinaryFileWriter writer = new BinaryFileWriter(filePath);
        boolean ret = writer.open();
        writer.write(data);
        writer.close();
        return ret;
    }

    public static String readAsStr(String filePath) {
        if (filePath == null || filePath.trim().length() <= 0) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        InputStreamReader stream = null;
        try {
            stream = new InputStreamReader(new FileInputStream(file), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream == null) {
            return null;
        }

        String ret = null;
        try {
            ret = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String[] read(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }

        FileReader reader = new FileReader(filePath, false);
        if (!reader.open()) {
            System.out.printf("Fail to open file: %s\n", filePath);
            return null;
        }

        // read
        List<String> lines = new ArrayList<String>();
        String str;
        while ((str = reader.readLine()) != null) {
            lines.add(str);
        }

        // close
        reader.close();

        String[] strs = new String[lines.size()];
        lines.toArray(strs);
        return strs;
    }

    public static byte[] readAsData(String filePath) {
        if (filePath == null || filePath.trim().length() <= 0) {
            return null;
        }

        BinaryFileReader reader = new BinaryFileReader(filePath);
        reader.open();
        byte[] data = reader.read();
        reader.close();
        return data;
    }

    public static String appendFileExt(String fileName, String fileExt) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        fileName = fileName.trim();
        if (StringUtils.isNotEmpty(fileExt) && !fileName.toLowerCase().endsWith(fileExt.toLowerCase())) {
            fileName = String.format("%s%s", fileName, fileExt);
        }
        return fileName;
    }

    public static String removeFileExt(String fileName, String fileExt) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        fileName = fileName.trim();
        if (StringUtils.isNotEmpty(fileExt) && fileName.endsWith(fileExt.toLowerCase())) {
            fileName = fileName.substring(0, fileName.length() - fileExt.length());
        }
        return fileName;
    }

    public static String removeFileExt(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        int index = fileName.lastIndexOf(".");
        if (index < 0 || index >= fileName.length()) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    public static String getFileExt(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        int index = fileName.lastIndexOf(".");
        if (index < 0 || index >= fileName.length()) {
            return "";
        }
        return fileName.substring(index);
    }

    public static String getFileName(String filePath) {
        return getFileName(filePath, null);
    }

    public static String getFileName(String filePath, String basePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }

        // Append the file name directly
        File src = new File(filePath);
        String fileName = src.getName();
        if (StringUtils.isNotEmpty(basePath)) {
            File baseFile = new File(basePath);
            if (baseFile.isFile()) {
                basePath = baseFile.getParent();
            }

            // Remove the base path
            basePath = basePath.trim().toLowerCase();
            int index = filePath.toLowerCase().indexOf(basePath);
            if (index >= 0) {
                fileName = filePath.substring(index + basePath.length());
            }
        }
        return fileName;
    }

    public static File[] findFiles(String filePath) {
        return findFiles(filePath, null, null, null, false);
    }

    public static File[] findFiles(String filePath, boolean recursive) {
        return findFiles(filePath, null, null, null, recursive);
    }

    public static File[] findFiles(String filePath, String fileExt) {
        return findFiles(filePath, null, fileExt, null, false);
    }

    public static File[] findFiles(String filePath, String fileExt, boolean recursive) {
        return findFiles(filePath, null, fileExt, null, recursive);
    }

    public static File[] findFiles(String filePath, String fileExt, String ignoreFileNamePostfix) {
        return findFiles(filePath, fileExt, ignoreFileNamePostfix, false);
    }

    public static File[] findFiles(String filePath, String fileExt, String ignoreFileNamePostfix, boolean recursive) {
        return findFiles(filePath, null, fileExt, ignoreFileNamePostfix, recursive);
    }

    public static File[] findFiles(String filePath, final String fileNamePrefix, final String fileExt, final String ignoreFileNamePostfix, final boolean recursive) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        // File or directory, while not iterate sub folders
        File[] files = null;
        if (file.isFile() && (StringUtils.isEmpty(fileExt) || filePath.toLowerCase().endsWith(fileExt.toLowerCase()))) {
            files = new File[]{file};
        } else if (file.isDirectory()) {
            files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return recursive;
                    } else if (pathname.isFile()) {
                        String name = pathname.getName();
                        if (StringUtils.isEmpty(name)) {
                            return false;
                        }

                        String str = name.toLowerCase();
                        if (StringUtils.isNotEmpty(fileNamePrefix) && !str.startsWith(fileNamePrefix.toLowerCase())
                                || StringUtils.isNotEmpty(fileExt) && !str.endsWith(fileExt.toLowerCase())
                                ) {
                            return false;
                        }
                        return StringUtils.isEmpty(ignoreFileNamePostfix) || !str.endsWith(ignoreFileNamePostfix.toLowerCase());
                    }
                    return false;
                }
            });

            // Filter sub folders
            if (recursive && files != null && files.length > 0) {
                List<File> fileList = new ArrayList<File>();
                for (File tmp : files) {
                    if (tmp.isFile()) {
                        fileList.add(tmp);
                    } else if (recursive) {
                        File[] filesInSubFolder = findFiles(tmp.getPath(), fileNamePrefix, fileExt, ignoreFileNamePostfix, true);
                        if (filesInSubFolder != null && filesInSubFolder.length > 0) {
                            fileList.addAll(Arrays.asList(filesInSubFolder));
                        }
                    }
                }

                // Convert to array
                if (fileList.size() > 0) {
                    files = new File[fileList.size()];
                    fileList.toArray(files);
                } else {
                    files = null;
                }
            }
        }
        return files;
    }

    public static File[] findSubFolders(String filePath) {
        return findSubFolders(filePath, false);
    }

    public static File[] findSubFolders(String filePath, boolean recursive) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }

        // List
        File[] folders = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.exists() && pathname.isDirectory();
            }
        });

        // Loop the sub folders
        if (recursive && folders != null && folders.length > 0) {
            List<File> folderList = new ArrayList<File>();
            folderList.addAll(Arrays.asList(folders));

            for (File folder : folders) {
                File[] subFolders = findSubFolders(folder.getPath(), true);
                if (subFolders != null && subFolders.length > 0) {
                    folderList.addAll(Arrays.asList(subFolders));
                }
            }

            // Convert to array
            if (folderList.size() > 0) {
                folders = new File[folderList.size()];
                folderList.toArray(folders);
            } else {
                folders = null;
            }
        }
        return folders;
    }
}
