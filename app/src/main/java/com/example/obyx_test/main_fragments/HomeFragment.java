package com.example.obyx_test.main_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.obyx_test.AdapterHome;
import com.example.obyx_test.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    String s1, s2;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    //StorageReference storageRef = storage.getReference();

    // Create a reference with an initial file path and name
    //StorageReference pathReference = storageRef.child("Posts/stars.jpg");

    // Create a reference to a file from a Google Cloud Storage URI
    //StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/Posts/stars.jpg");

    // Create a reference from an HTTPS URL
    // Note that in the URL, characters are URL escaped!
    //StorageReference httpsReference = storage.getReferenceFromUrl("gs://testing-1310c.appspot.com");

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    // only if needing to pass arguments to fragment for some reason
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView);

        s1 = "hello";
        s2 = "There";

        AdapterHome adapterHome = new AdapterHome(this.getContext(), s1, s2);
        // TODO retrieve data from Firestore and update it with data in scroll view

        recyclerView.setAdapter(adapterHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        return view;

    }
}