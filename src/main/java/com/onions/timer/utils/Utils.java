package com.onions.timer.utils;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static JSONObject jsonStringToJSONObject(String jsonString) {
        return JSONObject.parseObject(jsonString);
    }
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
