package tw.meowdev.cfg.askdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<ProfileItem> {

    public ProfileAdapter(Context context, ArrayList<ProfileItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileItem item = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.icon);
            viewHolder.value = (TextView)convertView.findViewById(R.id.valueText);
            viewHolder.column = (TextView)convertView.findViewById(R.id.columnText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.icon.setImageResource(item.iconId);
        viewHolder.value.setText(item.value);
        viewHolder.column.setText(item.column);

        return convertView;
    }

    private static class ViewHolder {
        TextView value, column;
        ImageView icon;
    }
}
