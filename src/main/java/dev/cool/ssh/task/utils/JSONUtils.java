package dev.cool.ssh.task.utils;

import com.google.gson.Gson;

public class JSONUtils {
    private static final Gson gson = new Gson();

    public static String toJSON(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
