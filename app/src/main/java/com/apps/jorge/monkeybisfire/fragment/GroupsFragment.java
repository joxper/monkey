package com.apps.jorge.monkeybisfire.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.apps.jorge.monkeybisfire.ActivityGroupDetails;
import com.apps.jorge.monkeybisfire.ActivityMain;
import com.apps.jorge.monkeybisfire.R;
import com.apps.jorge.monkeybisfire.adapter.GroupsGridAdapter;
import com.apps.jorge.monkeybisfire.data.Constant;
import com.apps.jorge.monkeybisfire.data.Tools;
import com.apps.jorge.monkeybisfire.model.Group;

public class GroupsFragment extends Fragment {

    RecyclerView recyclerView;
    public GroupsGridAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private LinearLayout lyt_not_found;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar  = (ProgressBar) view.findViewById(R.id.progressBar);
        lyt_not_found   = (LinearLayout) view.findViewById(R.id.lyt_not_found);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (Constant.getGroupData(getActivity()).size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        }else{
            lyt_not_found.setVisibility(View.GONE);
        }

        // specify an adapter (see also next example)
        mAdapter = new GroupsGridAdapter( getActivity(), Constant.getGroupData(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new GroupsGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Group obj, int position) {
                ActivityGroupDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj);
            }
        });

        return view;
    }

    public void onRefreshLoading(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

}
