package com.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by user on 2017/9/23.
 */
public class EmptyUtil {
    public static boolean isEmpty(String str) {
        return isEmpty(str, true);
    }

    public static boolean isEmpty(String str, boolean trim) {
        return str == null || str.isEmpty() || (trim && str.trim().isEmpty());
    }

    public static <T> boolean isEmpty(T[] arr) {
        return arr == null || arr.length <= 0;
    }

    public static boolean isEmpty(Collection list) {
        return CollectionUtils.isEmpty(list);
    }

    public static boolean isEmpty(Map map) {
        return MapUtils.isEmpty(map);
    }
}
