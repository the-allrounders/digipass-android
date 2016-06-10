package com.digipass.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digipass.android.helpers.EditPreferenceDialog;
import com.digipass.android.helpers.ListAdapter_1;
import com.digipass.android.helpers.ListUtils;
import com.digipass.android.objects.ListItem;
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

    private Map<String, ArrayList<ListItem>> data;
    private Context c;

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
        getActivity().setTitle(R.string.title_preferences);
        if (bundle != null) {
            try {
                data = (Map<String, ArrayList<ListItem>>)bundle.getSerializable("data");
                if (data != null) {
                    if (data.get("preferences").size() == 0 || data.get("categories").size() == 0) {
                        getActivity().findViewById(R.id.pref_list).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.cat_list).setVisibility(View.GONE);
                        if (data.get("preferences").size() == 0) {
                            printList(R.id.list_full_list, data.get("categories"));
                        } else if (data.get("categories").size() == 0) {
                            printList(R.id.list_full_list, data.get("preferences"));
                        }
                    }
                    else {
                        printList(R.id.list_pref_list, data.get("preferences"));
                        printList(R.id.list_cat_list, data.get("categories"), data.get("preferences").size());
                        getActivity().findViewById(R.id.full_list).setVisibility(View.GONE);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void printList(int list_id, final ArrayList<ListItem> _data) {
        printList(list_id, _data, 0);
    }

    private void printList(int list_id, final ArrayList<ListItem> _data, int delay) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(list_id);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);
            ArrayAdapter<ListItem> adapter = new ListAdapter_1(c, R.layout.list_row_1, _data, delay);
            AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    ListItem listItem = _data.get(position);
                    if (Objects.equals(listItem.get_row_type(), "preference")) {
                        EditPreferenceDialog dialog = new EditPreferenceDialog();
                        JSONArray _data = listItem.get_values();

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
                        dialog.setData(options, values, listItem.get_key());
                        dialog.setTitle(listItem.get_name());
                        dialog.show(getFragmentManager(), "preference");
                    } else if (Objects.equals(listItem.get_row_type(), "group")) {
                        final MainActivity ac = ((MainActivity)getActivity());
                        ac.showHomeAsUp = true;
                        Bundle b = new Bundle();
                        b.putSerializable("data", (Serializable)Data.GetInstance(c).GetPreferences(_data.get(position).get_key()));
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
}
