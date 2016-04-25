package tw.meowdev.cfg.askdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainFragment extends Fragment {

    private TextView textView;
    private EditText editText;
    private Button button;
    private Toolbar toolbar;
    private HttpClient client;
    private SQLiteDatabase db;
    private String api = "https://api.myjson.com/bins/%s";

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        client = new HttpClient();
        db = Database.getWritableDatabase(getActivity());
        textView = (TextView)view.findViewById(R.id.text);
        editText = (EditText)view.findViewById(R.id.key);
        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getData(editText.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle("Question");
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData(String value) {
        String data = CacheData.getData(db, value, HttpClient.isOnline(getActivity()));
        try {
            if (data == null) { // has no cache data
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
        textView.setText(String.format("data: %s\n\nfrom: %s\n", json.toString(), from));
    }

    private class MyCallback implements Callback {
        String key;

        public MyCallback(String key) {
            this.key = key;
        }

        @Override
        public void onFailure(Call call, IOException exception) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Network call failed", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                try {
                    final JSONObject json = new JSONObject(responseStr);
                    CacheData cacheData = new CacheData(this.key, json.toString());
                    cacheData.insert(db);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData(json, "from network");
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "JSON Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), String.format("Response Error: %s", response.code()), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
