package com.common.enc;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {
    public static String md5(String str) {
        if (str == null) {
            return null;
        }

        return DigestUtils.md5Hex(str);
    }
}
