package com.common.enc;

import com.common.util.LogUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class Md5Util {
    public static String md5(String str) {
        return str == null ? null : DigestUtils.md5Hex(str);
    }

    public static String md5(byte[] data) {
        return data == null ? null : DigestUtils.md5Hex(data);
    }

    public static String md5(InputStream stream) {
        try {
            return stream == null ? null : DigestUtils.md5Hex(stream);
        } catch (IOException e) {
            LogUtil.error("Error when md5 with stream");
        }
        return null;
    }
}
