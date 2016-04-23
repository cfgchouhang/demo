package tw.meowdev.cfg.askdemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private FloatingActionButton fab;
    private HttpClient client;
    private SQLiteDatabase db;
    private String api = "https://api.myjson.com/bins/%s";


    private class MyCallback implements Callback {
        String key;

        public MyCallback(String key) {
            this.key = key;
        }

        @Override
        public void onFailure(Call call, IOException exception) {
            // Something went wrong
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                try {
                    final JSONObject json = new JSONObject(responseStr);
                    CacheData cacheData = new CacheData(this.key, json.toString());
                    cacheData.insert(db);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData(json, "from network");
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Response Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Request not successful
            }
        }
    };

    private void getData(String value) {
        String data = CacheData.getData(db, value);
        try {
            if (data == null) { // has no cache data or need to refresh
                String url = String.format(api, value);
                client.get(url, new MyCallback(value));
            } else {
                JSONObject json = null;

                json = new JSONObject(data);
                setData(json, "from cache");
            }
        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    private void setData(JSONObject json, String from) {
        textView.setText(String.format("data: %s\nfrom: %s", json.toString(), from));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView)findViewById(R.id.text);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                getData("1qzf3");
            }
        });

        db = Database.getWritableDatabase(this);

        client = new HttpClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
