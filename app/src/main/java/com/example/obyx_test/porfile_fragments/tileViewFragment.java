package com.example.obyx_test.porfile_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.obyx_test.AdapterHome;
import com.example.obyx_test.AdapterProfileTile;
import com.example.obyx_test.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tileViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tileViewFragment extends Fragment {

    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public tileViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tileViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static tileViewFragment newInstance(String param1, String param2) {
        tileViewFragment fragment = new tileViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tile_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        Bundle args = getArguments();
        ArrayList posts = args.getCharSequenceArrayList("posts");
        int listLen = args.getInt("PostsLen");


        AdapterProfileTile adapterProfileTile = new AdapterProfileTile(this.getContext(), posts, listLen);
        // TODO retrieve data from Firestore and update it with data in scroll view

        recyclerView.setAdapter(adapterProfileTile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}