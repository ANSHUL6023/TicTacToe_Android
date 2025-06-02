package com.c2c.tictactoegame;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder; // Important for encoding URL parameters
import java.nio.charset.StandardCharsets; // For URL encoding charset
import java.util.Iterator; // For iterating over JSON keys
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class ApiClient {

    private static final String TAG = "ApiClient";

    private static final String BASE_URL = "http://127.0.0.1:3000/";

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public void sendDeviceInfo(JSONObject deviceInfoJson) {
        if (deviceInfoJson == null) {
            Log.e(TAG, "deviceInfoJson is null. Cannot send GET request.");
            return;
        }

        StringBuilder urlBuilder = new StringBuilder(BASE_URL).append("/device-info?");

        // Convert JSONObject to URL query parameters
        Iterator<String> keys = deviceInfoJson.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                String value = deviceInfoJson.get(key).toString();
                // URL encode both key and value to handle special characters (spaces, '/')
                urlBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()))
                        .append("&");
            } catch (Exception e) {
                Log.e(TAG, "Error encoding JSON parameter '" + key + "': " + e.getMessage(), e);
            }
        }

        // Remove the trailing '&' if present
        if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        String finalUrl = urlBuilder.toString();
        Log.d(TAG, "Sending GET request to URL: " + finalUrl);

        Request request = new Request.Builder()
                .url(finalUrl)
                .get() // Specify GET method
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request FAILED for GET!", e);
                if (e instanceof java.net.ConnectException) {
                    Log.e(TAG, "Connection Refused: Is Node.js server running and reachable at " + BASE_URL + "?");
                } else if (e instanceof java.net.SocketTimeoutException) {
                    Log.e(TAG, "Connection Timeout: Server is too slow or unreachable.");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    Log.d(TAG, "Server Response SUCCESS for GET: " + response.code() + " - " + responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Server Response ERROR for GET: " + response.code() + " - " + response.message() + " - " + errorBody);
                }
                if (response.body() != null) {
                    response.body().close();
                }
                response.close();
            }
        });
    }
}