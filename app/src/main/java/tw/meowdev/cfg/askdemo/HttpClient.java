package tw.meowdev.cfg.askdemo;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    private static OkHttpClient client = null;

    public HttpClient() {
        if(client == null) {
            client = new OkHttpClient();
        }
    }

    public String get(String url) {
        Response response = null;
        String content = null;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            response = client.newCall(request).execute();
            content = response.body().string();
            Log.e("Http", String.format("get %s\ncontent: %s", url, content));
        } catch (IOException e){

        }

        return content;
    }
}
