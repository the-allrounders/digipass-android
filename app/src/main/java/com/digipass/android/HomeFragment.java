package com.digipass.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.digipass.android.helpers.ActivityListAdapter;
import com.digipass.android.helpers.ListUtils;
import com.digipass.android.helpers.OrganisationListAdapter;
import com.digipass.android.helpers.TextListAdapter;
import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.StatusListItem;
import com.digipass.android.objects.TextListItem;
import com.digipass.android.singletons.API;
import com.digipass.android.singletons.Data;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Map<String, ArrayList<DefaultListItem>> data;
    private Context c;
    private boolean hasReqData = true;
    private boolean hasAcData = true;

    SwipeRefreshLayout swipeContainer;
    String key = "0";

    ArrayAdapter<DefaultListItem> adapter;

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
        ((MainActivity)getActivity()).resetDrawerToggle();
        if (bundle != null) {
            try {
                data = (Map<String, ArrayList<DefaultListItem>>)bundle.getSerializable("data");
                if (data != null) {
                    key = bundle.getString("key");
                    printListRequests(data.get("requests"));
                    printListActivities(data.get("activities"), data.get("requests").size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        titleOnClick(R.id.pending_requests_title_wrapper, 2);
        titleOnClick(R.id.activity_log_title_wrapper, 3);

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

    private void titleOnClick(int title, final int menu_item) {
        final MainActivity ac = (MainActivity)getActivity();
        getActivity().findViewById(title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.animateDrawerToggle = false;
                ac.onNavigationItemSelected(ac.navigationView.getMenu().getItem(menu_item));
            }
        });
    }

    private void printListRequests(ArrayList<DefaultListItem> _data) {
        View v = getView();
        final ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_pen_req_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);

            if (_data.size() == 0 || !hasReqData) {
                _data = new ArrayList<>();
                adapter = new TextListAdapter(c, R.layout.list_row_text, _data, 0);
                adapter.add(new TextListItem(getResources().getString(R.string.no_requests)));
                lv.setAdapter(adapter);
                hasReqData = false;
            } else //noinspection ConstantConditions
                if (hasReqData) {
                adapter = new OrganisationListAdapter(c, R.layout.list_row_permission, _data, 0);
                final ArrayList<DefaultListItem> _d = _data;
                AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        DefaultListItem organisation = _d.get(position);
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
                        b.putSerializable("data", (Serializable)Data.GetInstance(c).GetRequestsList(organisation.get_key()));
                        b.putString("key", organisation.get_key());
                        b.putString("title", c.getResources().getString(R.string.title_permissions) + " - " + organisation.get_name());
                        ac.StartFragment(PermissionsFragment.class, b);
                    }
                };
                lv.setOnItemClickListener(onClick);

                SwipeActionAdapter.SwipeActionListener listener = new SwipeActionAdapter.SwipeActionListener() {
                    @Override
                    public boolean hasActions(int position, SwipeDirection direction) {
                        return direction.isLeft() || direction.isRight();
                    }

                    @Override
                    public boolean shouldDismiss(int position, SwipeDirection direction) {
                        return false;
                    }

                    @Override
                    public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                        View v = getView();
                        for(int i=0;i<positionList.length;i++) {
                            SwipeDirection direction = directionList[i];
                            final int position = positionList[i];
                            String status = "";
                            StatusListItem organisation = (StatusListItem)_d.get(position);
                            switch (direction) {
                                case DIRECTION_FAR_LEFT:
                                case DIRECTION_NORMAL_LEFT:
                                    status = "denied";
                                    break;
                                case DIRECTION_FAR_RIGHT:
                                case DIRECTION_NORMAL_RIGHT:
                                    status = "approved";
                                    break;
                            }
                            if (!Objects.equals(status, "")) {
                                ImageView status_icon = (ImageView)ListUtils.getViewByPosition(position, lv).findViewById(R.id.row_1_status_icon);
                                status_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_loading));
                                RotateAnimation r;
                                r = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                r.setDuration((long) 1000);
                                r.setRepeatCount(Animation.INFINITE);
                                status_icon.startAnimation(r);
                                Data.GetInstance(getContext()).PrePermissionsPost(organisation.get_children(), status, new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshList();
                                    }
                                }, getActivity());
                            }
                        }
                    }
                };

                SwipeActionAdapter swipeAdapter = PermissionsFragment.GetSwipeAdapter(adapter, lv, listener);

                lv.setAdapter(swipeAdapter);
            }

            ListUtils.setDynamicHeight(lv);
        }
    }

    private void printListActivities(ArrayList<DefaultListItem> _data, int delay) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(R.id.list_ac_log_list);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);

            ArrayAdapter<DefaultListItem> adapter;

            if (_data.size() == 0 || !hasAcData) {
                _data = new ArrayList<>();
                adapter = new TextListAdapter(c, R.layout.list_row_text, _data, 0);
                adapter.add(new TextListItem(getResources().getString(R.string.no_activities)));
                lv.setAdapter(adapter);
                hasAcData = false;
            } else //noinspection ConstantConditions
                if (hasAcData) {
                adapter = new ActivityListAdapter(c, R.layout.list_row_activity, _data, 0);
                lv.setAdapter(adapter);
            }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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

    private void refreshList() {
        swipeContainer.setRefreshing(true);
        API.getInstance(c).GetJSONResult(new Runnable() {
            @Override
            public void run() {
                ArrayList<DefaultListItem> data = Data.GetInstance(getContext()).GetHomeLists().get("requests");
                if (data.size() == 0) {
                    data.add(new TextListItem(getResources().getString(R.string.no_requests)));
                }
                DefaultListItem.RefreshList(adapter, data, swipeContainer);
            }
        }, "req");
    }
}
