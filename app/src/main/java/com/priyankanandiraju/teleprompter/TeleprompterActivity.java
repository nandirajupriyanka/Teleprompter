package com.priyankanandiraju.teleprompter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.utils.SharedPref;
import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;
import com.priyankanandiraju.teleprompter.utils.TeleprompterView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.MainActivity.EXTRA_FILE_DATA;

public class TeleprompterActivity extends AppCompatActivity {

    private static final String TAG = TeleprompterActivity.class.getSimpleName();
    private TeleprompterFile mTeleprompterFile;
    @BindView(R.id.tv_teleprompter_content)
    TextView tvContent;
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
        String prefSpeed = sharedPreferences.getString(getString(R.string.KEY_SPEED), getString(R.string.speed10));

        TeleprompterView teleprompterView = new TeleprompterView(this);
        int textColor = teleprompterView.generateTextColor(prefColor);
        int textSize = teleprompterView.generateTextSize(prefSize);

        textColor = ContextCompat.getColor(this, textColor);
        tvContent.setTextColor(textColor);
        tvContent.setTextSize(textSize);
        tvContent.setText(mTeleprompterFile.getContent());

    }

}
