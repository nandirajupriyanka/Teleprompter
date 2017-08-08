package com.priyankanandiraju.teleprompter;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.priyankanandiraju.teleprompter.data.TeleprompterFileContract.TeleprompterFileEvent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    @BindView(R.id.iv_file_icon)
    ImageView ivFileIcon;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        ButterKnife.bind(this);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        etTitle.addTextChangedListener(this);
        etContent.addTextChangedListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        checkFieldsForEmptyValues();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                if (btnSave.isEnabled()) {
                    saveDataToDb();
                }
                break;
            case R.id.btn_cancel:
                etTitle.setText("");
                etContent.setText("");
                // TODO: 8/8/17 Set ImageView
                break;
        }
    }

    private void saveDataToDb() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                if (uri == null) {
                    Toast.makeText(AddFileActivity.this, R.string.failed_to_save_data, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddFileActivity.this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Save this movie as favourite
        //int movieId = Integer.parseInt(mId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TeleprompterFileEvent.COLUMN_FILE_TITLE, title);
        contentValues.put(TeleprompterFileEvent.COLUMN_FILE_CONTENT, content);
        // TODO: 8/8/17 Save Image

        asyncQueryHandler.startInsert(1, null, TeleprompterFileEvent.CONTENT_URI, contentValues);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        checkFieldsForEmptyValues();

    }

    private void checkFieldsForEmptyValues() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        if (title.equals("") || content.equals("")) {
            btnSave.setEnabled(false);
        } else {
            btnSave.setEnabled(true);
        }
    }
}
