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

import com.digipass.android.helpers.ListUtils;
import com.digipass.android.helpers.OrganisationListAdapter;
import com.digipass.android.helpers.TextListAdapter;
import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.OrganisationDefaultListItem;
import com.digipass.android.objects.TextListItem;
import com.digipass.android.singletons.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Map<String, ArrayList<DefaultListItem>> data;
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
                data = (Map<String, ArrayList<DefaultListItem>>)bundle.getSerializable("data");
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

    private void printListRequests(final ArrayList<DefaultListItem> _data) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_pen_req_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);

            ArrayAdapter<DefaultListItem> adapter;

            if (_data.size() == 0) {
                adapter = new TextListAdapter(c, R.layout.list_row_text, _data, 0);
                adapter.add(new TextListItem(getResources().getString(R.string.no_requests)));
            } else {
                adapter = new OrganisationListAdapter(c, R.layout.list_row_default, _data, 0);
                AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        OrganisationDefaultListItem organisation = (OrganisationDefaultListItem)_data.get(position);
                        final MainActivity ac = ((MainActivity)getActivity());
                        ac.showHomeAsUp = true;
                        Bundle b = new Bundle();
                        b.putSerializable("data", (Serializable) Data.GetInstance(c).GetPreferences(organisation.get_key()));
                        ac.StartFragment(PermissionsFragment.class, b);
                    }
                };
                lv.setOnItemClickListener(onClick);
            }

            lv.setAdapter(adapter);

            ListUtils.setDynamicHeight(lv);
        }
    }

    private void printListActivities(final ArrayList<DefaultListItem> _data, int delay) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_ac_log_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);

            ArrayAdapter<DefaultListItem> adapter;

            if (_data.size() == 0) {
                adapter = new TextListAdapter(c, R.layout.list_row_text, _data, 0);
                adapter.add(new TextListItem(getResources().getString(R.string.no_activities)));
            } else {
                adapter = new OrganisationListAdapter(c, R.layout.list_row_default, _data, 0);
                AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        OrganisationDefaultListItem organisation = (OrganisationDefaultListItem)_data.get(position);
                        final MainActivity ac = ((MainActivity)getActivity());
                        ac.showHomeAsUp = true;
                        Bundle b = new Bundle();
                        b.putSerializable("data", (Serializable) Data.GetInstance(c).GetPreferences(organisation.get_key()));
                        ac.StartFragment(PermissionsFragment.class, b);
                    }
                };
                lv.setOnItemClickListener(onClick);
            }

            lv.setAdapter(adapter);

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
