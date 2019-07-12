package com.willpower.editor.http;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestHelper {
    public static RequestBody jsonBody(Object src){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"),new Gson().toJson(src));
    }
}
