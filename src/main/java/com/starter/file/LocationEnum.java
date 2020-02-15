package com.starter.file;

import java.util.HashMap;
import java.util.Map;

public enum LocationEnum {
    /*
    文件存储位置
     */
    Service(0, "service"),
    Aliyun(1, "aliyun"),
    Qiniu(2, "qiniu");

    LocationEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static Map<Integer, LocationEnum> idItemMap = null;

    /**
     * Easy to get enum
     */
    public static LocationEnum get(int id) {
        if (idItemMap == null) {
            synchronized (LocationEnum.class) {
                if (idItemMap == null) {
                    idItemMap = new HashMap<Integer, LocationEnum>() {{
                        for (LocationEnum item : LocationEnum.values()) {
                            put(item.id, item);
                        }
                    }};
                }
            }
        }
        return idItemMap.get(id);
    }

    public static LocationEnum get(String name) {
        return LocationEnum.valueOf(name);
    }
}
