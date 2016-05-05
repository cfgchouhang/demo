package tw.meowdev.cfg.askdemo.views;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tw.meowdev.cfg.askdemo.R;
import tw.meowdev.cfg.askdemo.models.CacheData;
import tw.meowdev.cfg.askdemo.managers.Database;
import tw.meowdev.cfg.askdemo.models.ProfileItem;
import tw.meowdev.cfg.askdemo.network.HttpClient;
import tw.meowdev.cfg.askdemo.utils.CircleTransformation;


public class ProfileFragment extends Fragment {

    private TextView nameText;
    private ImageView imgAvatar;
    private CircleTransformation transformation = new CircleTransformation();

    private ProfileAdapter adapter;
    private ListView listView;

    private SQLiteDatabase db;
    private HttpClient client;
    private String api = "https://api.myjson.com/bins/%s";


    private class MyCallback implements Callback {
        String key;

        public MyCallback(String key) {
            this.key = key;
        }

        @Override
        public void onFailure(Call call, IOException exception) {
            handleError("Network call failed");
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                try {
                    final JSONObject json = new JSONObject(responseStr);
                    CacheData cacheData = new CacheData(this.key, json.toString());
                    cacheData.insert(db);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData(json);
                        }
                    });
                } catch (JSONException e) {
                    handleError(e.getMessage());
                }
            } else {
                handleError(String.format("Response Error: %s", response.code()));
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnBack) {
                getActivity().onBackPressed();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        client = new HttpClient();
        db = Database.getWritableDatabase(getActivity());
        listView = (ListView)view.findViewById(R.id.listView);
        nameText = (TextView)view.findViewById(R.id.nameText);
        imgAvatar = (ImageView)view.findViewById(R.id.imgAvatar);

        view.findViewById(R.id.btnBack).setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData(getArguments().getString("id"));
    }

    private void handleError(final String errMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String id) {
        if(id.startsWith("/")) id = id.substring(1);
        String data = CacheData.getData(db, id, HttpClient.isOnline(getActivity()));
        try {
            if (data == null) { // has no cache data
                String url = String.format(api, id);
                client.get(url, new MyCallback(id));
            } else {
                JSONObject json = new JSONObject(data);
                setData(json);
            }
        } catch (IOException | JSONException e) {
            handleError(e.getMessage());
        }
    }

    public void setData(JSONObject jobj) {
        ArrayList<ProfileItem> arrayList = ProfileItem.fromJson(jobj);
        arrayList.add(new ProfileItem("Math, Phy, Chem", "subjects", R.drawable.ic_favorite_black_24dp));
        if(adapter == null) {

            adapter = new ProfileAdapter(getActivity(), arrayList);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(arrayList);
            adapter.notifyDataSetChanged();
        }

        try {
            JSONObject nameObj = jobj.getJSONObject("name");
            String firstName = nameObj.getString("first"), lastName = nameObj.getString("last");
            String imgUrl = jobj.getString("profile_pic_url");

            Picasso.with(getActivity()).load(imgUrl).transform(transformation).into(imgAvatar);
            nameText.setText(String.format("%s %s", firstName, lastName));

        } catch (JSONException e) {
            handleError(e.getMessage());
        }
    }
}
