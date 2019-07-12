package com.willpower.editor.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    public static Gson gson() {
        return new Gson();
    }
}
