package tw.meowdev.cfg.askdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import tw.meowdev.cfg.askdemo.views.ProfileFragment;
import tw.meowdev.cfg.askdemo.views.QuestionFragment;

public class MainActivity extends AppCompatActivity {

    public String lastQId = null; // the last question id can restore question view if come back from profile page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setFragment(intent);
    }

    private void setFragment(Intent intent) {
        Uri deeplink = intent.getData();
        if(deeplink != null) {
            String type = deeplink.getHost(), id = deeplink.getPath();

            if(type == null || id == null) {
                putQuestionFragment(null);
            } else if(type.equals("question")) {
                putQuestionFragment(id);
            } else if(type.equals("user")) {
                putProfileFragment(id);
            }
        } else {
            putQuestionFragment(null);
        }
    }

    protected void putQuestionFragment(String id) {
        lastQId = id;
        QuestionFragment fragment = (QuestionFragment)getSupportFragmentManager().findFragmentByTag("question");
        if(fragment == null) {
            fragment = new QuestionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            fragment.setArguments(bundle);

            FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
            if(getSupportFragmentManager().findFragmentByTag("profile") != null)
                tc.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit);
            tc.replace(R.id.container, fragment, "question").commit();
        } else {
            fragment.ask(id); // if fragment existing, refresh it to show question
        }
    }

    protected void putProfileFragment(String id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        fragment.setArguments(bundle);

        FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
        if(getSupportFragmentManager().findFragmentByTag("question") != null)
            tc.setCustomAnimations(R.anim.enter, R.anim.exit);
        tc.replace(R.id.container, fragment, "profile").commit();
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
        } else if(id == android.R.id.home) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            //getSupportActionBar().setTitle("Main");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("profile") != null) {
            putQuestionFragment(lastQId);
        } else {
            finish();
        }
    }
}
