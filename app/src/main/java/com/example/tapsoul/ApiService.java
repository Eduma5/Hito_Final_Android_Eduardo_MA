package com.example.tapsoul;

import android.content.Context;
import android.util.Log;
import com.example.tapsoul.ui.theme.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    public interface ApiCallback {
        void onSuccess(List<User> users);
        void onFailure(Exception e);
    }

    public void getUsers(Context context, ApiCallback callback) {
        new Thread(() -> {
            try {
                InputStream is = context.getAssets().open("ranking.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                JSONArray jsonArray = new JSONArray(json);
                List<User> users = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    int score = obj.getInt("score");
                    users.add(new User(name, score));
                }
                callback.onSuccess(users);
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }).start();
    }
}