package com.digipass.android;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digipass.android.helpers.ListAdapter_1;
import com.digipass.android.objects.ParcelableListObject;
import com.digipass.android.objects.Preference;
import com.digipass.android.singletons.Data;

import java.util.ArrayList;

public class GenericListActivity extends android.app.ListActivity {

    private ArrayList data = new ArrayList<>();
    Intent intent;
    public int row_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
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
        printList();
    }



    private void printList() {
        AdapterView.OnItemClickListener onClick;
        ListView lv =
                ((ListView) findViewById(android.R.id.list));
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
                        Intent i = new Intent(v.getContext(), GenericListActivity.class);
                        ArrayList<Preference> data = Data.GetInstance(c).GetPreferences(_data.get(position).get_key());
                        i.putExtra("data", data);
                        i.putExtra("list_type", "preferences");
                        i.putExtra("row_type", R.layout.list_row_1);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(i);
                    }
                };
                lv.setAdapter(adapter);
                AdapterView.OnItemClickListener onClickCallback = onClick;
                lv.setOnItemClickListener(onClickCallback);
                break;
        }
    }
}
