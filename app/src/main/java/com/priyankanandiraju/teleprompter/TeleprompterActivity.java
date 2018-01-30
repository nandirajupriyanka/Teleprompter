package com.priyankanandiraju.teleprompter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.helper.CustomScrollView;
import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;
import com.priyankanandiraju.teleprompter.utils.TeleprompterView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.utils.Constants.EXTRA_FILE_DATA;

public class TeleprompterActivity extends AppCompatActivity {

    private static final String TAG = TeleprompterActivity.class.getSimpleName();
    private TeleprompterFile mTeleprompterFile;
    @BindView(R.id.scrollView)
    CustomScrollView scrollView;
    @BindView(R.id.tv_teleprompter_content)
    TextView tvContent;
    private int scrollSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleprompter);

        ButterKnife.bind(this);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            mTeleprompterFile = getIntent().getParcelableExtra(EXTRA_FILE_DATA);
            setTitle(mTeleprompterFile.getTitle());
            setupSharedPreferences();
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefBgColor = sharedPreferences.getString(getString(R.string.KEY_BG_COLOR), getString(R.string.bg_color_white_value));
        String prefColor = sharedPreferences.getString(getString(R.string.KEY_TEXT_COLOR), getString(R.string.text_color_black_value));
        String prefSize = sharedPreferences.getString(getString(R.string.KEY_TEXT_SIZE), getString(R.string.text_size_small_value));
        String prefSpeed = sharedPreferences.getString(getString(R.string.KEY_SPEED), getString(R.string.speed_10_value));

        TeleprompterView teleprompterView = new TeleprompterView(this);
        int bgColor = teleprompterView.generateBgColor(prefBgColor);
        int textColor = teleprompterView.generateTextColor(prefColor);
        int textSize = teleprompterView.generateTextSize(prefSize);
        scrollSpeed = teleprompterView.generateTextScrollSpeed(prefSpeed);

        bgColor = ContextCompat.getColor(this, bgColor);
        textColor = ContextCompat.getColor(this, textColor);

        scrollView.setBackgroundColor(bgColor);
        tvContent.setTextColor(textColor);
        tvContent.setTextSize(textSize);
        tvContent.setText(mTeleprompterFile.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_teleprompter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_play:
                startScrollEffect();
                break;
            case android.R.id.home:
                resetScrollEffect();
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }


    private void startScrollEffect() {
        scrollView.setEnabledScrolling(true);
        int totalHeight = scrollView.getChildAt(0).getHeight();
        ObjectAnimator animations = ObjectAnimator.ofInt(scrollView, "scrollY", totalHeight);
        animations.setDuration(scrollSpeed);
        animations.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(TAG, "onAnimationEnd()");
                resetScrollEffect();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animations.start();
    }

    private void resetScrollEffect() {
        scrollView.setEnabledScrolling(false);
    }
}
