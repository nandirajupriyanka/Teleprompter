package com.priyankanandiraju.teleprompter;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;
import com.priyankanandiraju.teleprompter.utils.TeleprompterView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.MainActivity.EXTRA_FILE_DATA;

public class TeleprompterActivity extends AppCompatActivity {

    private static final String TAG = TeleprompterActivity.class.getSimpleName();
    private TeleprompterFile mTeleprompterFile;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
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
        // TODO: 8/11/17 Speed
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefColor = sharedPreferences.getString(getString(R.string.KEY_TEXT_COLOR), getString(R.string.text_color_black_value));
        String prefSize = sharedPreferences.getString(getString(R.string.KEY_TEXT_SIZE), getString(R.string.text_size_small_value));
        String prefSpeed = sharedPreferences.getString(getString(R.string.KEY_SPEED), getString(R.string.speed_10_value));

        TeleprompterView teleprompterView = new TeleprompterView(this);
        int textColor = teleprompterView.generateTextColor(prefColor);
        int textSize = teleprompterView.generateTextSize(prefSize);
        scrollSpeed = teleprompterView.generateTextScrollSpeed(prefSpeed);

        textColor = ContextCompat.getColor(this, textColor);
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
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    private void startScrollEffect() {
        int totalHeight = scrollView.getChildAt(0).getHeight();
        ObjectAnimator.ofInt(scrollView, "scrollY", totalHeight).setDuration(scrollSpeed).start();
    }
}
