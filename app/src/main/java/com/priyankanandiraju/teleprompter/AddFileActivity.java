package com.priyankanandiraju.teleprompter;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.priyankanandiraju.teleprompter.analytics.Analytics;
import com.priyankanandiraju.teleprompter.data.TeleprompterFileContract.TeleprompterFileEvent;
import com.priyankanandiraju.teleprompter.utils.QueryHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.FAIL;
import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.FAILED_TO_UPLOAD_IMAGE;
import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.SUCCESS;
import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.SUCCESSFULLY_UPLOADED_IMAGE;
import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.USER_DIDN_T_PICK_IMAGE;
import static com.priyankanandiraju.teleprompter.utils.Constants.IMAGE_DATA;
import static com.priyankanandiraju.teleprompter.utils.Constants.INTENT_EXTRA_CONTENT;

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, QueryHandler.onQueryHandlerInsertComplete, LoaderManager.LoaderCallbacks<Cursor> {

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
    @Nullable
    private Bitmap bitmap = null;
    private Uri mCurrentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        ButterKnife.bind(this);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // read parameters from the intent used to launch the activity.
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(INTENT_EXTRA_CONTENT)) {
                String content = intent.getStringExtra(INTENT_EXTRA_CONTENT);
                etContent.setText(content);
            }
            mCurrentUri = intent.getData();
        }

        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_a_file));
        } else {
            setTitle(R.string.edit_a_file);
            getLoaderManager().initLoader(0, null, this);
        }


        etTitle.addTextChangedListener(this);
        etContent.addTextChangedListener(this);
        ivFileIcon.setOnClickListener(this);
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
            case R.id.iv_file_icon:
                uploadImage();
                break;
            case R.id.btn_save:
                if (btnSave.isEnabled()) {
                    saveDataToDb();
                    finish();
                }
                break;
            case R.id.btn_cancel:
                etTitle.setText("");
                etContent.setText("");
                ivFileIcon.setImageResource(android.R.drawable.ic_menu_camera);
                break;
        }
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 101 && resultCode == RESULT_OK && null != data) {
                Analytics.logEventGetImageFromDevice(this, SUCCESSFULLY_UPLOADED_IMAGE);
                Uri selectedImage = data.getData();
                bitmap = getBitmapFromUri(selectedImage);
                ivFileIcon.setImageBitmap(bitmap);
            } else {
                Analytics.logEventGetImageFromDevice(this, USER_DIDN_T_PICK_IMAGE);
                Toast.makeText(this, R.string.havent_picked_image,
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Analytics.logEventGetImageFromDevice(this, FAILED_TO_UPLOAD_IMAGE);
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void saveDataToDb() {
        final String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        QueryHandler queryHandler = new QueryHandler(getContentResolver(), AddFileActivity.this);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TeleprompterFileEvent.COLUMN_FILE_TITLE, title);
        contentValues.put(TeleprompterFileEvent.COLUMN_FILE_CONTENT, content);
        contentValues.put(TeleprompterFileEvent.COLUMN_FILE_IMAGE, android.R.drawable.ic_menu_camera);

        if (mCurrentUri == null) {
            queryHandler.startInsert(1, null, TeleprompterFileEvent.CONTENT_URI, contentValues);
        } else {
            queryHandler.startUpdate(1, null, mCurrentUri, contentValues, null, null);
        }
    }

    private Bitmap saveImageBitmap(@NonNull Bitmap thumbnail, String nameString) {
        // Removing image saved earlier in shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.remove(IMAGE_DATA + nameString);
        edit.commit();


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = bytes.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //saving image to shared preferences
        edit.putString(IMAGE_DATA + nameString, encodedImage);
        edit.commit();
        return thumbnail;
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

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        final String title = etTitle.getText().toString();
        if (uri == null) {
            Analytics.logEventAddFileToDb(this, FAIL);
            Toast.makeText(AddFileActivity.this, R.string.failed_to_save_data, Toast.LENGTH_SHORT).show();
        } else {
            if (bitmap != null) {
                saveImageBitmap(bitmap, title);
            }
            Analytics.logEventAddFileToDb(this, SUCCESS);
            Toast.makeText(AddFileActivity.this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        final String title = etTitle.getText().toString();
        if (result == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, R.string.update_failed,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            if (bitmap != null) {
                saveImageBitmap(bitmap, title);
            }
            Toast.makeText(this, R.string.update_successful,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String projection[] = {
                TeleprompterFileEvent._ID,
                TeleprompterFileEvent.COLUMN_FILE_TITLE,
                TeleprompterFileEvent.COLUMN_FILE_CONTENT,
                TeleprompterFileEvent.COLUMN_FILE_IMAGE,
                TeleprompterFileEvent.COLUMN_FILE_IS_FAV,
        };
        return new CursorLoader(this,
                TeleprompterFileEvent.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int idColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent._ID);
            int titleColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent.COLUMN_FILE_TITLE);
            int contentColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent.COLUMN_FILE_CONTENT);

            // Extract out the value from the Cursor for the given column index
            String id = cursor.getString(idColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String content = cursor.getString(contentColumnIndex);

            etTitle.setText(title);
            etContent.setText(content);
            setImage();
        }
    }

    private void setImage() {
        if (mCurrentUri != null) {
            String nameString = etTitle.getText().toString().trim();
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            String previouslyEncodedImage = shre.getString(IMAGE_DATA + nameString, "");
            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                ivFileIcon.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etTitle.setText("");
        etContent.setText("");
        ivFileIcon.setImageBitmap(null);
        bitmap = null;
    }
}
