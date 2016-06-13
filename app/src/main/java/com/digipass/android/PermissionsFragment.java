package com.digipass.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.digipass.android.helpers.DefaultListAdapter;
import com.digipass.android.helpers.ListUtils;
import com.digipass.android.helpers.OrganisationListAdapter;
import com.digipass.android.objects.DefaultListItem;
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
                    if (Objects.equals(bundle.getString("key"), "0")) {
                        getActivity().findViewById(R.id.pref_list).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.cat_list).setVisibility(View.GONE);
                        printList(R.id.list_full_list, data.get("organisations"));
                    } else {
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
                }
            } catch (Exception ignored) {}
        }
    }

    private void printList(int list_id, final ArrayList<DefaultListItem> _data) {
        printList(list_id, _data, 0);
    }

    private void printList(int list_id, final ArrayList<DefaultListItem> _data, int delay) {
        View v = getView();
        ListView lv;
        if (v != null) {
            lv = (ListView) v.findViewById(list_id);
            lv.setFadingEdgeLength(0);
            lv.setDividerHeight(0);
            ArrayAdapter<DefaultListItem> adapter;
            if (Objects.equals(this.getArguments().getString("key"), "0")) {
                adapter = new OrganisationListAdapter(c, R.layout.list_row_organisation, _data, delay);
            } else {
                adapter = new DefaultListAdapter(c, R.layout.list_row_permission, _data, delay);
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
                    for(int i=0;i<positionList.length;i++) {
                        SwipeDirection direction = directionList[i];
                        final int position = positionList[i];
                        DefaultListItem defaultListItem = _data.get(position);
                        switch (direction) {
                            case DIRECTION_FAR_LEFT:
                            case DIRECTION_NORMAL_LEFT:
                                Log.d("swipe", "Deny all from " + defaultListItem.get_name() + " (" + defaultListItem.get_key() + ")");
                                break;
                            case DIRECTION_FAR_RIGHT:
                            case DIRECTION_NORMAL_RIGHT:
                                Log.d("swipe", "Accept all from " + defaultListItem.get_name() + " (" + defaultListItem.get_key() + ")");
                                break;
                        }
                    }
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

    public static SwipeActionAdapter GetSwipeAdapter(ArrayAdapter adapter, ListView lv, SwipeActionAdapter.SwipeActionListener listener) {
        final SwipeActionAdapter swipeAdapter = new SwipeActionAdapter(adapter);
        swipeAdapter.setListView(lv);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.list_row_permission_row_bg_left).addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.list_row_permission_row_bg_left).addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.list_row_permission_row_bg_right).addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.list_row_permission_row_bg_right);

        swipeAdapter.setSwipeActionListener(listener);
        return swipeAdapter;
    }
}
