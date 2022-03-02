package com.auth.server.utils.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static <T> T stringToJson(String json, Class<T> typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    public static <T> List<T> stringToList(String json, Class<T> typeOfT) {
        JsonArray jsonArray = (JsonArray) stringToJson(json);
        List<T> objectList = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            String object = jsonElement.getAsJsonObject().toString();
            objectList.add(stringToJson(object, typeOfT));
        }
        return objectList;
    }

    public static Object stringToJson(String json, String type) {
        try {
            Class typeClass = Class.forName(type);
            return new Gson().fromJson(json, typeClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("invalid type:" + type);
        }
    }

    public static Object stringToJson(String json) {
        return new JsonParser().parse(json);
    }

    public static String objectToString(Object jsonObject) {
        return new Gson().toJson(jsonObject);
    }

    public static String objectToString(Object jsonObject, boolean serializeNulls) {
        if (serializeNulls) {
            return new GsonBuilder().serializeNulls().create().toJson(jsonObject);
        }
        return objectToString(jsonObject);
    }

    public static String objectToStringWithDateFormat(Object jsonObject, String dateFormat) {
        return new GsonBuilder().setDateFormat(dateFormat).create().toJson(jsonObject);
    }

    public static String getAsString(JsonObject jsonObject, String memberName) {
        JsonElement element = jsonObject.get(memberName);
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    public static String getAsString(Object javaObject, String memberName) {
        if (javaObject != null) {
            String jString = gson.toJson(javaObject);
            JsonObject jobj = new JsonParser().parse(jString).getAsJsonObject();
            return getAsString(jobj, memberName);
        }
        return "";
    }

    public static Map<String, String> jsonToMap(String json) {
        return stringToJson(json, Map.class);
    }

    public static void main(String[] args) {
    }
}