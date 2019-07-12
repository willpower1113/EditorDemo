package com.willpower.editor.http;

import com.willpower.log.Timber;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class StringCallback implements Callback<String> {

    public abstract void onSuccess(int code, String msg, String data);

    public void onFailure(int code, Throwable throwable) {
        Timber.e(throwable, "网络日志：", "code:" + code);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.code() == 200) {
            try {
                JSONObject object = new JSONObject(response.body());
                if (object.getBoolean("success")) {
                    onSuccess(response.code(), object.getString("msg"), object.get("data").toString());
                } else {
                    onFailure(response.code(), new Throwable(object.getString("msg")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                onFailure(response.code(), e);
            }
        } else {
            onFailure(response.code(), new Throwable(response.body()));
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable throwable) {
        onFailure(-1, throwable);
    }

}
