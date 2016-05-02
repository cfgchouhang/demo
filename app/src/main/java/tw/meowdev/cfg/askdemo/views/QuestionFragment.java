package tw.meowdev.cfg.askdemo.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tw.meowdev.cfg.askdemo.MainActivity;
import tw.meowdev.cfg.askdemo.R;
import tw.meowdev.cfg.askdemo.models.CacheData;
import tw.meowdev.cfg.askdemo.managers.Database;
import tw.meowdev.cfg.askdemo.network.HttpClient;
import tw.meowdev.cfg.askdemo.utils.CircleTransformation;
import tw.meowdev.cfg.askdemo.utils.Time;

public class QuestionFragment extends Fragment {

    private RelativeLayout askLayout, answerLayout;
    private TextView askName, answerName, subject, description, date, rating;
    private ImageView askAvatar, answerAvatar;

    private EditText editText;
    private Button button;
    private Toolbar toolbar;

    private Drawable originalImage;
    private ImageButton imgButton;
    private Animator currentAnimator;
    private int shortAnimationDuration;

    private SQLiteDatabase db;
    private HttpClient client;
    private String api = "https://api.myjson.com/bins/%s";



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.button) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getData(editText.getText().toString());
            } else if(view.getId() == R.id.imgButton) {
                zoomImageFromThumb(imgButton, originalImage);
            }
        }
    };

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        client = new HttpClient();
        db = Database.getWritableDatabase(getActivity());
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        editText = (EditText)view.findViewById(R.id.key);

        askLayout = (RelativeLayout)view.findViewById(R.id.askLayout);
        askLayout.setVisibility(View.INVISIBLE);
        answerLayout = (RelativeLayout)view.findViewById(R.id.answerLayout);
        answerLayout.setVisibility(View.INVISIBLE);

        askName = (TextView)view.findViewById(R.id.textAskName);
        answerName = (TextView)view.findViewById(R.id.textAnswerName);
        subject = (TextView)view.findViewById(R.id.textSubject);
        description = (TextView)view.findViewById(R.id.textDescription);
        date = (TextView)view.findViewById(R.id.textDate);
        rating = (TextView)view.findViewById(R.id.rating);

        askAvatar = (ImageView)view.findViewById(R.id.imgAskAvatar);
        answerAvatar = (ImageView)view.findViewById(R.id.imgAnswerAvatar);

        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(onClickListener);

        imgButton = (ImageButton)view.findViewById(R.id.imgButton);
        imgButton.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Question");
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ask(getArguments().getString("id"));

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public void ask(String id) {
        if(id != null) {
            if(id.startsWith("/")) id = id.substring(1);
            editText.setText(id);
            button.callOnClick();
        }
    }

    private void getData(String id) {
        String data = CacheData.getData(db, id, HttpClient.isOnline(getActivity()));
        try {
            if (data == null) { // has no cache data
                String url = String.format(api, id);
                client.get(url, new MyCallback(id));
            } else {
                JSONObject json = new JSONObject(data);
                setData(json);
                ((MainActivity)getActivity()).lastQId = id;
            }
        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    private void setData(JSONObject json) {
        //textView.setText(String.format("data: %s\n\nfrom: %s\n", json.toString(), from));
        JSONObject asked, answered;
        try {
            json = json.getJSONObject("data");
            asked = json.getJSONObject("asked_by");
            answered = json.getJSONObject("answered_by");

            askName.setText(asked.getString("username"));
            answerName.setText(answered.getString("username"));

            subject.setText(json.getJSONObject("subject").getString("description"));
            description.setText(json.getString("description"));
            date.setText(Time.shortStr(json.getString("created_at"), "yyyy-MM-dd HH:mm:ss Z"));
            rating.setText(String.format("Rating: %s", json.get("user_rating")));

            CircleTransformation tf = new CircleTransformation();
            Picasso.with(getActivity()).load(asked.getString("profile_pic_url")).transform(tf).into(askAvatar);
            Picasso.with(getActivity()).load(answered.getString("profile_pic_url")).transform(tf).into(answerAvatar);

            askLayout.setVisibility(View.VISIBLE);
            answerLayout.setVisibility(View.VISIBLE);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imgButton.setImageBitmap(bitmap);
                    originalImage = imgButton.getDrawable();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(getActivity()).load(json.getString("picture_url")).into(target);

        } catch (JSONException e) {
        }
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
        public void onResponse(Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                ((MainActivity)getActivity()).lastQId = this.key;
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
                    Toast.makeText(getActivity(), "JSON Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), String.format("Response Error: %s", response.code()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };



    private void zoomImageFromThumb(final View thumbView, Drawable drawable) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView)getActivity().findViewById(R.id.expanded_image);
        expandedImageView.setImageDrawable(drawable);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getActivity().findViewById(R.id.rootLayout).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
