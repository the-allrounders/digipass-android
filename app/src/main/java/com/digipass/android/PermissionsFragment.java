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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.digipass.android.helpers.ListUtils;
import com.digipass.android.helpers.OrganisationListAdapter;
import com.digipass.android.helpers.StatusListAdapter;
import com.digipass.android.objects.DefaultListItem;
import com.digipass.android.objects.StatusListItem;
import com.digipass.android.singletons.API;
import com.digipass.android.singletons.Data;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class PermissionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Map<String, ArrayList<DefaultListItem>> data;
    private Context c;

    SwipeRefreshLayout swipeContainer;
    String key = "0";

    ArrayAdapter<DefaultListItem> adapter_preferences;
    ArrayAdapter<DefaultListItem> adapter_groups;
    ArrayAdapter<DefaultListItem> adapter_organisations;

    public PermissionsFragment() {
        // Required empty public constructor
    }

    public static PermissionsFragment newInstance() {
        return new PermissionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this.getContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permissions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (!Objects.equals(bundle.getString("title"), "§")) {
                getActivity().setTitle(bundle.getString("title"));
            }
            if (Objects.equals(bundle.getString("key"), "0")) {
                ((MainActivity)getActivity()).resetDrawerToggle();
            }
            try {
                data = (Map<String, ArrayList<DefaultListItem>>)bundle.getSerializable("data");
                if (data != null) {
                    key = bundle.getString("key");
                    if (Objects.equals(bundle.getString("key"), "0")) {
                        getActivity().findViewById(R.id.pref_list).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.cat_list).setVisibility(View.GONE);
                        printList(R.id.list_full_list, data.get("organisations"), "organisations");
                    } else {
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
                }
            } catch (Exception ignored) {}
        }
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

        swipeContainer.setNestedScrollingEnabled(false);
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
            ArrayAdapter<DefaultListItem> adapter;
            if (Objects.equals(this.getArguments().getString("key"), "0")) {
                adapter = adapter_organisations = new OrganisationListAdapter(c, R.layout.list_row_permission, _data, delay);
            } else {
                adapter = new StatusListAdapter(c, R.layout.list_row_status, _data, delay);
                if (Objects.equals(adapter_type, "preferences")) {
                    adapter_preferences = adapter;
                } else if (Objects.equals(adapter_type, "groups")) {
                    adapter_groups = adapter;
                }
            }
            AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    DefaultListItem defaultListItem = _data.get(position);
                    if (Objects.equals(defaultListItem.get_row_type(), "group") || Objects.equals(defaultListItem.get_row_type(), "organisation")) {
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
                        b.putSerializable("data", (Serializable)Data.GetInstance(c).GetRequestsList(defaultListItem.get_key()));
                        b.putString("key", defaultListItem.get_key());
                        if (Objects.equals(defaultListItem.get_row_type(), "organisation")) {
                            b.putString("title", c.getResources().getString(R.string.title_permissions) + " - " + defaultListItem.get_name());
                        } else {
                            b.putString("title", "§");
                        }
                        ac.StartFragment(PermissionsFragment.class, b);
                    }
                }
            };

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
                    swipeContainer.setEnabled(false);
                    for(int i=0;i<positionList.length;i++) {
                        SwipeDirection direction = directionList[i];
                        final int position = positionList[i];
                        String status = "";
                        StatusListItem listItem = (StatusListItem)_data.get(position);
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
                            ArrayList<String> permissions;
                            if (Objects.equals(listItem.get_row_type(), "preference")) {
                                permissions = new ArrayList<>();
                                permissions.add(listItem.get_key());
                            } else {
                                permissions = listItem.get_children();
                            }
                            ImageView status_icon = (ImageView)ListUtils.getViewByPosition(position, lv).findViewById(R.id.row_1_status_icon);
                            status_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_loading));
                            RotateAnimation r;
                            r = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            r.setDuration((long) 1000);
                            r.setRepeatCount(Animation.INFINITE);
                            status_icon.startAnimation(r);
                            Data.GetInstance(getContext()).PrePermissionsPost(permissions, status, new Runnable() {
                                @Override
                                public void run() {
                                    refreshList();
                                }
                            }, getActivity());
                        }
                    }
                    swipeContainer.setEnabled(true);
                }
            };

            SwipeActionAdapter swipeAdapter = PermissionsFragment.GetSwipeAdapter(adapter, lv, listener);

            lv.setAdapter(swipeAdapter);

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


    public static SwipeActionAdapter GetSwipeAdapter(ArrayAdapter adapter, ListView lv, SwipeActionAdapter.SwipeActionListener listener) {
        final SwipeActionAdapter swipeAdapter = new SwipeActionAdapter(adapter);
        swipeAdapter.setListView(lv);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.list_row_permission_row_bg_left).addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.list_row_permission_row_bg_left).addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.list_row_permission_row_bg_right).addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.list_row_permission_row_bg_right);

        swipeAdapter.setSwipeActionListener(listener);
        return swipeAdapter;
    }

    private void refreshList() {
        swipeContainer.setRefreshing(true);
        API.getInstance(c).GetJSONResult(new Runnable() {
            @Override
            public void run() {
                if (adapter_groups != null) {
                    DefaultListItem.RefreshList(adapter_groups, Data.GetInstance(getContext()).GetRequestsList(key).get("categories"), swipeContainer);
                }
                if (adapter_preferences != null) {
                    DefaultListItem.RefreshList(adapter_preferences, Data.GetInstance(getContext()).GetRequestsList(key).get("preferences"), swipeContainer);
                }
                if (adapter_organisations != null) {
                    DefaultListItem.RefreshList(adapter_organisations, Data.GetInstance(getContext()).GetRequestsList(key).get("organisations"), swipeContainer);
                }
            }
        }, "req");
    }
}
