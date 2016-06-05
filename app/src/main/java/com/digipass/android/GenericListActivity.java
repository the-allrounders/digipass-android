package com.digipass.android;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digipass.android.helpers.ListAdapter_1;
import com.digipass.android.helpers.EditPreferenceDialog;
import com.digipass.android.objects.Preference;
import com.digipass.android.singletons.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class GenericListActivity extends AppCompatActivity {

    private ArrayList data = new ArrayList<>();
    Intent intent;
    public int row_type = 0;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(getResources().getString(R.string.preferences));
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();
        Bundle b = getIntent().getExtras();
        data = b.getParcelableArrayList("data");
        row_type = intent.getIntExtra("row_type", 0);
        String group_title = intent.getStringExtra("group_title");
        if (!Objects.equals(group_title, "")) {
            toolbar.setTitle(getResources().getString(R.string.preferences) + " - " + group_title);
            setSupportActionBar(toolbar);
        }
        printList();
    }



    private void printList() {
        AdapterView.OnItemClickListener onClick;
        ListView lv =
                ((ListView) findViewById(R.id.list));
        switch (getIntent().getStringExtra("list_type")) {
            case "preferences":
                final ArrayList<Preference> _data = new ArrayList<>();
                for (Object o : data) {
                    _data.add((Preference) o);
                }
                final Context c = this;
                ArrayAdapter<Preference> adapter = new ListAdapter_1(c, row_type, _data);
                onClick = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Preference preference = _data.get(position);
                        if (Objects.equals(preference.get_row_type(), "preference")) {
                            EditPreferenceDialog dialog = new EditPreferenceDialog();
                            FragmentManager fragmentManager = getFragmentManager();
                            JSONArray _data = preference.get_values();

                            String[] options = new String[_data.length()];
                            boolean[] values = new boolean[_data.length()];

                            for(int i = 0; i < _data.length(); i++) {
                                try {
                                    JSONObject d = (JSONObject)_data.get(i);
                                    options[i] = d.getString("title");
                                    values[i] = Boolean.valueOf(d.getString("value"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            dialog.setData(options, values);
                            dialog.setTitle(preference.get_name());
                            dialog.show(fragmentManager, "preference");
                        } else if (Objects.equals(preference.get_row_type(), "group")) {
                            Intent i = new Intent(v.getContext(), GenericListActivity.class);
                            ArrayList<Preference> data = Data.GetInstance(c).GetPreferences(_data.get(position).get_key());
                            i.putExtra("data", data);
                            i.putExtra("list_type", "preferences");
                            i.putExtra("row_type", row_type);
                            i.putExtra("group_title", preference.get_name());
                            Log.d("qwerrt", preference.get_name());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(i);
                        }
                    }
                };
                lv.setAdapter(adapter);
                AdapterView.OnItemClickListener onClickCallback = onClick;
                lv.setOnItemClickListener(onClickCallback);
                break;
        }
    }
}
