package com.starter.file;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum FileTypeEnum {
    /*
    文件类型
     */
    File(0, "file", "f"),
    Image(1, "image", "i"),
    Audio(2, "audio", "a"),

    FaceImage(100, "face_image", "m"),
    FaceProfiled(101, "face_profiled", "r"),
    FaceImageSrc(110, "face_image_source", "s"),
    FaceChanged(111, "face_changed", "c"),
    Profile(200, "profile", "p");

    FileTypeEnum(int id, String name, String flag) {
        this.id = id;
        this.name = name;
        this.flag = flag;
    }

    private int id;
    private String name;
    private String flag;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFlag() {
        return flag;
    }

    private static Map<Integer, FileTypeEnum> idItemMap = null;
    private static Map<String, FileTypeEnum> flagItemMap = null;

    /**
     * Easy to get enum
     */
    public static FileTypeEnum get(int id) {
        if (idItemMap == null) {
            synchronized (FileTypeEnum.class) {
                if (idItemMap == null) {
                    idItemMap = new HashMap<Integer, FileTypeEnum>() {{
                        for (FileTypeEnum item : FileTypeEnum.values()) {
                            put(item.id, item);
                        }
                    }};
                }
            }
        }

        FileTypeEnum ret = idItemMap.get(id);
        return ret == null ? FileTypeEnum.File : ret;
    }

    public static FileTypeEnum get(String idStr) {
        return StringUtils.isEmpty(idStr) ? Image : get(Integer.valueOf(idStr));
    }

    public static FileTypeEnum getByFlag(String flag) {
        if (StringUtils.isEmpty(flag)) {
            return FileTypeEnum.File;
        }

        if (flagItemMap == null) {
            synchronized (FileTypeEnum.class) {
                if (flagItemMap == null) {
                    flagItemMap = new HashMap<String, FileTypeEnum>() {{
                        for (FileTypeEnum item : FileTypeEnum.values()) {
                            put(item.flag, item);
                        }
                    }};
                }
            }
        }

        FileTypeEnum ret = flagItemMap.get(flag);
        return ret == null ? FileTypeEnum.File : ret;
    }
}
