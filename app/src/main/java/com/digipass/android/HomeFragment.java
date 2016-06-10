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

import com.digipass.android.helpers.ListAdapter_1;
import com.digipass.android.helpers.ListUtils;
import com.digipass.android.objects.ListItem;
import com.digipass.android.singletons.Data;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Map<String, ArrayList<ListItem>> data;
    private Context c;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this.getContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        getActivity().setTitle(R.string.title_home);
        if (bundle != null) {
            try {
                data = (Map<String, ArrayList<ListItem>>)bundle.getSerializable("data");
                if (data != null) {
                    printListRequests(data.get("requests"));
                    printListActivities(data.get("activities"), data.get("requests").size());
                }
            } catch (Exception e) {
                e.printStackTrace();
//                getActivity().onBackPressed();
            }
        }
    }

    private void printListRequests(final ArrayList<ListItem> _data) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_pen_req_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);
            ArrayAdapter<ListItem> adapter = new ListAdapter_1(c, R.layout.list_row_1, _data, 0);
            AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    ListItem listItem = _data.get(position);
                    final MainActivity ac = ((MainActivity)getActivity());
                    ac.showHomeAsUp = true;
                    Bundle b = new Bundle();
                    b.putSerializable("data", (Serializable) Data.GetInstance(c).GetPreferences(_data.get(position).get_key()));
                    ac.StartFragment(PreferencesFragment.class, b);
                }
            };

            if (adapter.getCount() == 0) {
                adapter.add(new ListItem("0", getResources().getString(R.string.no_requests), "", new JSONArray(), "preference", ""));
            }

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(onClick);

            ListUtils.setDynamicHeight(lv);
        }
    }

    private void printListActivities(final ArrayList<ListItem> _data, int delay) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_ac_log_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);
            ArrayAdapter<ListItem> adapter = new ListAdapter_1(c, R.layout.list_row_1, _data, delay);
            AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    ListItem listItem = _data.get(position);
                    final MainActivity ac = ((MainActivity)getActivity());
                    ac.showHomeAsUp = true;
                    Bundle b = new Bundle();
                    b.putSerializable("data", (Serializable) Data.GetInstance(c).GetPreferences(_data.get(position).get_key()));
                    ac.StartFragment(PreferencesFragment.class, b);
                }
            };

            if (adapter.getCount() == 0) {
                adapter.add(new ListItem("0", getResources().getString(R.string.no_activities), "", new JSONArray(), "preference", ""));
            }

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
