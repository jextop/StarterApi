package com.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ding
 */
public class ResUtil {
    public static String readAsStr(String fileName) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(fileName);
        if (stream == null) {
            return null;
        }

        try {
            return IOUtils.toString(stream);
        } finally {
            stream.close();
        }
    }
}
