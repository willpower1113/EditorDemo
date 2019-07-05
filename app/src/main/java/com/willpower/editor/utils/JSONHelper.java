package com.willpower.editor.utils;

import com.google.gson.Gson;

import java.io.Serializable;

public class JSONHelper {

    public static String serializable(Object object) {
        return new Gson().toJson(object);
    }
}
