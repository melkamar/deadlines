package com.melkamar.deadlines.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 17:30
 */
public class JsonPrettyPrinter {
    public static String prettyPrint(String uglyJson){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJson);
        return gson.toJson(je);
    }
}
