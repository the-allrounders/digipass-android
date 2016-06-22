package com.digipass.android.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.digipass.android.PreferencesFragment;
import com.digipass.android.R;
import com.digipass.android.singletons.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditPreferenceDialog extends DialogFragment {
    ArrayList<Integer> mSelectedItems;

    String[] options = {};
    boolean[] values = {};
    String preference_id;

    String title;

    PreferencesFragment fragment;

    public void setData(String[] o, boolean[] v, String pref_id, PreferencesFragment fragment) {
        options = o;
        values = v;
        preference_id = pref_id;
        this.fragment = fragment;
    }

    public void setTitle(String t) {
        title = t;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<>();  // Where we track the selected items
        for (int i = 0; i < values.length; i++) {
            if (values[i]) {
                mSelectedItems.add(i);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(options, values,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        Log.d("dialog", "ok");
                        Log.d("dialog", mSelectedItems.toString());
                        JSONArray values = new JSONArray();
                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            JSONObject value = new JSONObject();
                            try {
                                value.put("title", options[mSelectedItems.get(i)]);
                                value.put("value", "true");
                                values.put(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        API.getInstance(getActivity().getApplicationContext()).PostPreferenceTask(values, preference_id, new Runnable() {
                            @Override
                            public void run() {
                                fragment.refreshList();
                            }
                        }, getActivity());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("dialog", "cancel");
                    }
                });

        return builder.create();
    }
}
