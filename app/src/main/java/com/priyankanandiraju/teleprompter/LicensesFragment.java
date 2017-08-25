package com.priyankanandiraju.teleprompter;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

import static com.priyankanandiraju.teleprompter.utils.Constants.PATH_LICENSES_FILE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LicensesFragment extends DialogFragment {

    public static LicensesFragment newInstance() {
        return new LicensesFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_licenses, null);
        view.loadUrl(PATH_LICENSES_FILE);
        return new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
