package tw.meowdev.cfg.askdemo;

import android.database.sqlite.SQLiteBlobTooBigException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    ImageButton btnBack;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnBack) {
                ((MainActivity)getActivity()).putQuestionFragment(null);
            }

        }
    };

    public void ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnBack = (ImageButton)view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
