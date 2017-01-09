package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;

/**
 * Created by xianrui on 15/5/7.
 */

//single instance;
public class GsonHelper {
    public static SimpleDateFormat gsonDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Gson gson;  //single instance;

    private static class GsonHelperHolder {
        public final static GsonHelper sington = new GsonHelper();
    }

    public static GsonHelper getInstance() {
        return GsonHelperHolder.sington;
    }

    private GsonHelper() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
        }
    }

    public Gson getGson() {
        return gson;
    }
}
