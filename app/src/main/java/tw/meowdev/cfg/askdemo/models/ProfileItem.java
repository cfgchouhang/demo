package tw.meowdev.cfg.askdemo.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tw.meowdev.cfg.askdemo.R;

public class ProfileItem {
    public String value, column;
    public int iconId;

    static String[] keys = new String[]{"email", "phone", "school"};
    static HashMap<String, Integer> iconIds = new HashMap<String, Integer>() {{
        put("email", R.drawable.ic_email_black_24dp);
        put("phone", R.drawable.ic_phone_black_24dp);
        put("school", R.drawable.ic_school_black_24dp);
    }};

    public ProfileItem(String value, String column, int id) {
        this.value = value;
        this.column = column;
        this.iconId = id;
    }

    public static ArrayList<ProfileItem> fromJson(JSONObject jobj) {
        ArrayList<ProfileItem> items = new ArrayList<ProfileItem>();
        for(String k: keys) {
            if(jobj.has(k)) {
                try {
                    items.add(new ProfileItem(jobj.getString(k), k, iconIds.get(k)));
                } catch (JSONException e) {
                }
            }
        }

        return items;
    }
}
