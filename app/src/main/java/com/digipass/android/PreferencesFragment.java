package com.digipass.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digipass.android.helpers.DefaultListAdapter;
import com.digipass.android.helpers.EditPreferenceDialog;
import com.digipass.android.helpers.ListUtils;
import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.singletons.API;
import com.digipass.android.singletons.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class PreferencesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Map<String, ArrayList<DefaultListItem>> data;
    private Context c;

    SwipeRefreshLayout swipeContainer;
    String key = "0";

    ArrayAdapter<DefaultListItem> adapter_preferences;
    ArrayAdapter<DefaultListItem> adapter_groups;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    public static PreferencesFragment newInstance() {
        return new PreferencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this.getContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            key = bundle.getString("key");
            getActivity().setTitle(bundle.getString("title"));
            if (Objects.equals(bundle.getString("key"), "0")) {
                ((MainActivity)getActivity()).resetDrawerToggle();
            }
            try {
                data = (Map<String, ArrayList<DefaultListItem>>)bundle.getSerializable("data");
                if (data != null) {
                    if (data.get("preferences").size() == 0 || data.get("categories").size() == 0) {
                        getActivity().findViewById(R.id.pref_list).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.cat_list).setVisibility(View.GONE);
                        if (data.get("preferences").size() == 0) {
                            printList(R.id.list_full_list, data.get("categories"), "groups");
                        } else if (data.get("categories").size() == 0) {
                            printList(R.id.list_full_list, data.get("preferences"), "preferences");
                        }
                    }
                    else {
                        printList(R.id.list_pref_list, data.get("preferences"), "preferences");
                        printList(R.id.list_cat_list, data.get("categories"), data.get("preferences").size(), "groups");
                        getActivity().findViewById(R.id.full_list).setVisibility(View.GONE);
                    }
                }
            } catch (Exception ignored) {}
            swipeContainer = (SwipeRefreshLayout)getActivity().findViewById(R.id.swiperefresh);
            swipeContainer.setColorSchemeResources(R.color.colorPrimary);

            swipeContainer.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refreshList();
                        }
                    }
            );
        }
    }

    private void printList(int list_id, final ArrayList<DefaultListItem> _data, String adapter_type) {
        printList(list_id, _data, 0, adapter_type);
    }

    private void printList(int list_id, final ArrayList<DefaultListItem> _data, int delay, String adapter_type) {
        View v = getView();
        final ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(list_id);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);
            ArrayAdapter<DefaultListItem> adapter = new DefaultListAdapter(c, R.layout.list_row_default, _data, delay);
            if (Objects.equals(adapter_type, "preferences")) {
                adapter_preferences = adapter;
            } else if (Objects.equals(adapter_type, "groups")) {
                adapter_groups = adapter;
            }
            final PreferencesFragment fragment = this;
            boolean userSelect = false;
            AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    DefaultListItem defaultListItem = _data.get(position);
                    if (Objects.equals(defaultListItem.get_row_type(), "preference")) {
                        EditPreferenceDialog dialog = new EditPreferenceDialog();
                        JSONArray _value_data = defaultListItem.get_values();

                        String[] options = new String[_value_data.length()];
                        boolean[] values = new boolean[_value_data.length()];

                        for(int i = 0; i < _value_data.length(); i++) {
                            try {
                                JSONObject d = (JSONObject)_value_data.get(i);
                                options[i] = d.has("title") ? d.getString("title") : defaultListItem.get_name();
                                values[i] = Boolean.valueOf(d.getString("value"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.setData(options, values, defaultListItem.get_key(), fragment);
                        dialog.setTitle(defaultListItem.get_name());
                        dialog.show(getFragmentManager(), "preference");
                    } else if (Objects.equals(defaultListItem.get_row_type(), "group")) {
                        final MainActivity ac = ((MainActivity)getActivity());
                        if (ac.showHomeAsUp) {
                            ac.animateDrawerToggle = false;
                            ac.animDrawerToggle(0, 1, 0);
                        } else {
                            ac.animateDrawerToggle = true;
                            ac.animDrawerToggle(0, 1);
                        }
                        ac.showHomeAsUp = true;
                        Bundle b = new Bundle();
                        b.putSerializable("data", (Serializable)Data.GetInstance(c).GetPreferences(defaultListItem.get_key()));
                        b.putString("key", defaultListItem.get_key());
                        b.putString("title", c.getResources().getString(R.string.title_preferences) + " - " + defaultListItem.get_name());
                        ac.StartFragment(PreferencesFragment.class, b);
                    }
                }
            };

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(onClick);

            ListUtils.setDynamicHeight(lv);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                refreshList();
                return true;
        }

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {
        swipeContainer.setRefreshing(true);
        API.getInstance(c).GetJSONResult(new Runnable() {
            @Override
            public void run() {
                if (adapter_groups != null) {
                    DefaultListItem.RefreshList(adapter_groups, Data.GetInstance(getContext()).GetPreferences(key).get("categories"), swipeContainer);
                }
                if (adapter_preferences != null) {
                    DefaultListItem.RefreshList(adapter_preferences, Data.GetInstance(getContext()).GetPreferences(key).get("preferences"), swipeContainer);
                }
            }
        }, "pref");
    }
}
