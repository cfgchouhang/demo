package tw.meowdev.cfg.askdemo;


import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.tools.ant.Main;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowView;
import org.robolectric.util.ActivityController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;

import tw.meowdev.cfg.askdemo.managers.Database;
import tw.meowdev.cfg.askdemo.utils.Time;
import tw.meowdev.cfg.askdemo.views.ProfileFragment;
import tw.meowdev.cfg.askdemo.views.QuestionFragment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class UnitTest {
    private MainActivity activity;
    private Button button;
    private EditText editText;
    private View askLayout, answerLayout;
    private TextView subject;

    private ActivityController<MainActivity> controller;
    private SQLiteOpenHelper openHelper;

    @Before
    public void setup() {
        controller = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void validateDeeplink() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("snapask-interview://question/1qzf3"));
        activity = controller.withIntent(intent).create().resume().visible().get();
        openHelper = Database.getHelper(activity);

        editText = (EditText)activity.findViewById(R.id.key);
        assertNotNull(editText);

        assertEquals("1qzf3", editText.getText().toString());

        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("snapask-interview://user/mosv"));
        activity.onNewIntent(intent);

        assertNotNull(activity.getSupportFragmentManager().findFragmentByTag("profile"));
        assertNull(activity.getSupportFragmentManager().findFragmentByTag("question"));

        openHelper.close();
    }

    @Test
    public void validateQuestionViewComponents() {
        activity = controller.create().resume().visible().get();
        openHelper = Database.getHelper(activity);

        assertNotNull(activity.findViewById(R.id.toolbar));
        assertNotNull(activity.findViewById(R.id.key));
        assertNotNull(activity.findViewById(R.id.askLayout));
        assertNotNull(activity.findViewById(R.id.answerLayout));
        assertNotNull(activity.findViewById(R.id.textAskName));
        assertNotNull(activity.findViewById(R.id.textAnswerName));
        assertNotNull(activity.findViewById(R.id.textSubject));
        assertNotNull(activity.findViewById(R.id.textDescription));
        assertNotNull(activity.findViewById(R.id.textDate));
        assertNotNull(activity.findViewById(R.id.rating));
        assertNotNull(activity.findViewById(R.id.imgAskAvatar));
        assertNotNull(activity.findViewById(R.id.imgAnswerAvatar));
        assertNotNull(activity.findViewById(R.id.button));
        assertNotNull(activity.findViewById(R.id.imgRes));

        openHelper.close();
    }

    @Test
    public void validateQuestionFeatures() {
        String jsonStr = "{\"status\":\"success\",\"data\":{\"id\":1002,\"asked_by\":{\"id\":182,\"email\":\"isobel.evans@example.com\",\"username\":\"orangeladybug329\",\"role\":{\"id\":4,\"name\":\"Student\"},\"profile_pic_url\":\"https://randomuser.me/api/portraits/women/87.jpg\"},\"description\":\"Error = More Code ^ 2\",\"status\":\"Finish\",\"user_id\":182,\"answer_tutor_id\":183,\"answered_by\":{\"id\":183,\"email\":\"manuela.velasco50@example.com\",\"username\":\"heavybutterfly920\",\"role\":{\"id\":3,\"name\":\"Tutor\"},\"profile_pic_url\":\"http://api.randomuser.me/portraits/women/39.jpg\"},\"created_at\":\"2016-01-28 16:51:21 +0800\",\"subject\":{\"id\":12,\"abbr\":\"Geography\",\"description\":\"Geography\",\"region\":{\"id\":1,\"name\":\"hk\",\"full_name\":\"Hong Kong\"}},\"picture_url\":\"http://image.slidesharecdn.com/cxcpastpapersforsocialstudies-140525101810-phpapp02/95/cxc-past-papers-for-social-studies-with-multiple-choice-questions-1-638.jpg\",\"user_rating\":5}}";
        activity = controller.create().resume().visible().get();
        openHelper = Database.getHelper(activity);

        QuestionFragment fragment = (QuestionFragment)activity.getSupportFragmentManager().findFragmentByTag("question");
        try {
            fragment.setData(new JSONObject(jsonStr));
        } catch (Exception e) {}

        assertTrue(activity.findViewById(R.id.askLayout).isShown());
        openHelper.close();
    }

    @Test
    public void validateProfileViewComponents() {
        activity = controller.create().resume().visible().get();
        activity.putProfileFragment("mosv");
        openHelper = Database.getHelper(activity);

        assertNotNull(activity.findViewById(R.id.listView));
        assertNotNull(activity.findViewById(R.id.nameText));
        assertNotNull(activity.findViewById(R.id.imgAvatar));

        openHelper.close();
    }

    @Test
    public void validateProfileFeatures() {
        String jsonStr = "{\"id\":723,\"gender\":\"male\",\"name\":{\"first\":\"adrian\",\"last\":\"martinez\"},\"email\":\"adrian.martinez@example.com\",\"username\":\"greenbear131\",\"registered\":1153910367,\"dob\":1290592394,\"school\":\"HKU\",\"country_code\":\"852\",\"phone\":\"12345678\",\"profile_pic_url\":\"https://randomuser.me/api/portraits/men/5.jpg\",\"role\":{\"id\":3,\"name\":\"Tutor\"},\"rating\":4.3,\"rating_total\":5.0}";
        activity = controller.create().resume().visible().get();
        activity.putProfileFragment("mosv");
        openHelper = Database.getHelper(activity);

        ProfileFragment fragment = (ProfileFragment)activity.getSupportFragmentManager().findFragmentByTag("profile");
        try {
            fragment.setData(new JSONObject(jsonStr));
        } catch (Exception e) {}

        assertEquals("adrian martinez", ((TextView)activity.findViewById(R.id.nameText)).getText().toString());
        openHelper.close();
    }
}
