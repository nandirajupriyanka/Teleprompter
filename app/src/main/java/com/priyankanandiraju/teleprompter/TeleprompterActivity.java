package com.priyankanandiraju.teleprompter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

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
            tvContent.setText(mTeleprompterFile.getContent());
        }
    }
}
