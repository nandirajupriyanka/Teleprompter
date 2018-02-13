package com.priyankanandiraju.teleprompter;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.gson.Gson;
import com.priyankanandiraju.teleprompter.data.TeleprompterFileContract.TeleprompterFileEvent;
import com.priyankanandiraju.teleprompter.helper.DeviceConfig;
import com.priyankanandiraju.teleprompter.utils.QueryDeleteHandler;
import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.priyankanandiraju.teleprompter.utils.Constants.DIALOG_TAG_LICENSES;
import static com.priyankanandiraju.teleprompter.utils.Constants.EXTRA_FILE_DATA;
import static com.priyankanandiraju.teleprompter.utils.Constants.INTENT_EXTRA_CONTENT;
import static com.priyankanandiraju.teleprompter.utils.Constants.SHARED_PREF_FILE;

public class MainActivity extends AppCompatActivity implements TeleprompterFilesAdapter.OnFileClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, PopupMenu.OnMenuItemClickListener, QueryDeleteHandler.onQueryHandlerDeleteComplete {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int RC_OCR_CAPTURE = 9003;


    @BindView(R.id.fab)
    FloatingActionButton fabButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.recycler_view)
    RecyclerView rvTeleprompterFiles;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private TeleprompterFilesAdapter mAdapter;
    private InterstitialAd mInterstitialAd;
    private TeleprompterFile mDownloadedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.white));
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        progressBar.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager layoutManager;
        if (DeviceConfig.getInstance().isTablet()) {
            layoutManager = new GridLayoutManager(this, 3);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }

        rvTeleprompterFiles.setLayoutManager(layoutManager);

        mAdapter = new TeleprompterFilesAdapter(new ArrayList<TeleprompterFile>(), this);
        rvTeleprompterFiles.setAdapter(mAdapter);

        // Admob banner
        AdView adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        // Admob InterstitialAd
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        loadNewInterstitialAd();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsPopup(view);
            }
        });

        getLoaderManager().initLoader(0, null, MainActivity.this);
    }

    private void loadNewInterstitialAd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_licenses) {
            LicensesFragment dialog = LicensesFragment.newInstance();
            dialog.show(getSupportFragmentManager(), DIALOG_TAG_LICENSES);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        progressBar.setVisibility(View.VISIBLE);

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
            tvEmpty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            rvTeleprompterFiles.setVisibility(View.GONE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        rvTeleprompterFiles.setVisibility(View.VISIBLE);
        List<TeleprompterFile> teleprompterFileList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int idColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent._ID);
            int titleColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent.COLUMN_FILE_TITLE);
            int contentColumnIndex = cursor.getColumnIndex(TeleprompterFileEvent.COLUMN_FILE_CONTENT);

            // Extract out the value from the Cursor for the given column index
            String id = cursor.getString(idColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String content = cursor.getString(contentColumnIndex);

            TeleprompterFile teleprompterFile = new TeleprompterFile();
            teleprompterFile.setId(id);
            teleprompterFile.setTitle(title);
            teleprompterFile.setContent(content);
            teleprompterFileList.add(teleprompterFile);
        }
        progressBar.setVisibility(View.INVISIBLE);
        mAdapter.setFileData(teleprompterFileList);

        getInitialItemForWidget(teleprompterFileList);
    }

    private void getInitialItemForWidget(@Nullable List<TeleprompterFile> teleprompterFileList) {
        if (teleprompterFileList != null && !teleprompterFileList.isEmpty()) {
            TeleprompterFile teleprompterFile = teleprompterFileList.get(0);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String teleprompterFileData = gson.toJson(teleprompterFile);
            editor.putString(SHARED_PREF_FILE, teleprompterFileData);
            editor.commit();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onFileClick(final TeleprompterFile teleprompterFile) {
        Log.v(TAG, "onFileClick() " + teleprompterFile.toString());
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.w(TAG, "The interstitial wasn't loaded yet.");
            startTeleprompterActivity(teleprompterFile);
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                loadNewInterstitialAd();
                startTeleprompterActivity(teleprompterFile);
            }
        });
    }

    @Override
    public void onDownloadClick(TeleprompterFile teleprompterFile) {
        Log.v(TAG, "onFileClick() " + teleprompterFile.toString());
        mDownloadedFile = teleprompterFile;
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            // Download file
            DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(this, teleprompterFile);
            downloadAsyncTask.execute();
        }
    }

    @Override
    public void onDeleteClick(int position, TeleprompterFile teleprompterFile) {
        Log.v(TAG, "onDeleteClick " + teleprompterFile.toString());
        showDeleteConfirmationDialog(position, teleprompterFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, download file
                    // Download file
                    DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(this, mDownloadedFile);
                    downloadAsyncTask.execute();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }

    private void startTeleprompterActivity(final TeleprompterFile teleprompterFile) {
        Intent intent = new Intent(this, TeleprompterActivity.class);
        intent.putExtra(EXTRA_FILE_DATA, teleprompterFile);
        startActivity(intent);
    }

    public void showOptionsPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.floating_button_menu_options);
        popup.show();
    }

    private void showDeleteConfirmationDialog(final int position, final TeleprompterFile teleprompterFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_item);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem(position, teleprompterFile);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(int position, TeleprompterFile teleprompterFile) {
        QueryDeleteHandler queryDeleteHandler = new QueryDeleteHandler(getContentResolver(), MainActivity.this);
        Uri uriToDelete = TeleprompterFileEvent.CONTENT_URI.buildUpon().appendPath(teleprompterFile.getId()).build();
        queryDeleteHandler.startDelete(position, null, uriToDelete, null, null);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_capture_image:
                // Text Recognition.
                intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                startActivityForResult(intent, RC_OCR_CAPTURE);
                return true;
            case R.id.menu_add_file:
                // Open AddFileActivity
                intent = new Intent(MainActivity.this, AddFileActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                Intent intent = new Intent(MainActivity.this, AddFileActivity.class);
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Toast.makeText(this, R.string.ocr_success, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Text read: " + text);
                    intent.putExtra(INTENT_EXTRA_CONTENT, text);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.ocr_failure, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        Log.v(TAG, "onDeleteComplete result" + result);
        if (result == 0) {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
        }
    }
}
