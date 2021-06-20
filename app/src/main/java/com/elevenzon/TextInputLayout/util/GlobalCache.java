package com.elevenzon.TextInputLayout.util;

import java.util.HashMap;
import java.util.Map;

public class GlobalCache {
    private  static Map<String,Object> map = new HashMap<>();
    public static void put(String key , Object obj)
    {
        map.put(key,obj);
    }
    public static Object get(String key) {
        return map.get(key);
    }
}
