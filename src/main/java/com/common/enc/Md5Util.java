package com.common.enc;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {
    public static String md5(String str) {
        return str == null ? null : DigestUtils.md5Hex(str);
    }

    public static String md5(byte[] data) {
        return data == null ? null : DigestUtils.md5Hex(data);
    }
}
