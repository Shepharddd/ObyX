package com.example.obyx_test.main_fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obyx_test.Data;
import com.example.obyx_test.R;
import com.example.obyx_test.UserData;
import com.example.obyx_test.porfile_fragments.iconViewFragment;
import com.example.obyx_test.porfile_fragments.tileViewFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SNIHostName;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String TAG = "PF";

    Integer postsLength;
    boolean connected;

    ArrayList<String> postsList = new ArrayList<String>();;

    BottomNavigationView navigationView;
    FrameLayout profileContent;
    DocumentSnapshot document;

    TextView userName, Posts, Followers, Following, Positive, Negative, Bio;
    Button edit;
    String username, followers, following, positive, negative, bio;
    ProgressBar loading;
    LinearLayout mainProfile;
    String UID;

    Map<String, Object> updatedData;

    LinearLayout mainContent;

    File root;
    ProgressBar progressBarContent;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    // TODO: Rename and change types of parameters
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Log.d(TAG, "onCreateView: conected to internet");
        }
        else{
            connected = false;
            Log.d(TAG, "onCreateView: disconected to internet");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        navigationView = view.findViewById(R.id.profileNavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        profileContent = view.findViewById(R.id.profileContent);
        progressBarContent = view.findViewById(R.id.progressBarContent);

        userName = (TextView) view.findViewById(R.id.UserName);
        Followers =  (TextView) view.findViewById(R.id.followers);
        Following =  (TextView) view.findViewById(R.id.following);
        Positive =  (TextView) view.findViewById(R.id.positive);
        Negative =  (TextView) view.findViewById(R.id.negative);
        Bio =  (TextView) view.findViewById(R.id.bio);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        mainProfile = (LinearLayout) view.findViewById(R.id.mainProfile);
        Posts = (TextView) view.findViewById(R.id.Posts);
        mainContent = (LinearLayout) view.findViewById(R.id.mainContent);

        Gson gson = new Gson();
        UID = requireArguments().getString("UID");

        Log.d(TAG, "onCreate: " + UID);

        root = new File(Environment.getDataDirectory(), "Users/" + UID);

        if (connected) {
            // connected from internet
            // most reliable way is to get data from db and update local cache
            // this makes sure whenever internet is disconnected local cache is most recent version
            

            db.collection("UsersStats")
                    .document(UID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "onComplete: exits");
                                    Log.d(TAG, "onComplete: "+ document.get("UserName").toString());

                                    username = document.get("UserName").toString();
                                    followers = document.get("Followers").toString();
                                    following = document.get("Following").toString();
                                    positive = document.get("UpVotes").toString();
                                    negative = document.get("DownVotes").toString();
                                    bio = document.get("Bio").toString();
                                    postsList = ( ArrayList<String> )  document.get("Posts");

                                    postsLength = postsList.size();
                                    Log.d(TAG, "onComplete: " + postsList);

                                    updateCache();

                                    userName.setText(username);
                                    Followers.setText(followers);
                                    Following.setText(following);
                                    Positive.setText(positive);
                                    Negative.setText(negative);
                                    Bio.setText(bio);
                                    Posts.setText(postsLength.toString());

                                    loading.setVisibility(View.GONE);
                                    mainProfile.setVisibility(View.VISIBLE);

                                    Bundle postInfo = new Bundle();
                                    postInfo.putInt("PostsLen", postsLength);
                                    postInfo.putStringArrayList("posts", postsList);

                                    iconViewFragment fragment = new iconViewFragment();
                                    fragment.setArguments(postInfo);
                                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.profileContent, fragment, "");
                                    fragmentTransaction.commit();

                                    mainContent.setVisibility(View.VISIBLE);

                                } else {
                                    Log.d("dbGetData", "No such document");
                                }
                            } else {
                                Log.d("dbGetData", "get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            //  disconnected from internet

            if (root.exists()) {

                // search internal files for cached data

                Data data = gson.fromJson(String.valueOf(root), Data.class);

                username = data.getMap().get("UserName").toString();
                followers = data.getMap().get("Followers").toString();
                following = Objects.requireNonNull(data.getMap().get("Following")).toString();
                positive = data.getMap().get("UpVotes").toString();
                negative = data.getMap().get("DownVotes").toString();
                bio = data.getMap().get("Bio").toString();

                userName.setText(username);
                Followers.setText(followers);
                Following.setText(following);
                Positive.setText(positive);
                Negative.setText(negative);
                Bio.setText(bio);


                loading.setVisibility(View.GONE);
                mainProfile.setVisibility(View.VISIBLE);

            } else {

                // theoretically we should never get to this part
                // so i guess just display an error msg

                String log = "An Error Occurred: ";
                Toast.makeText(this.getContext(), log,
                        Toast.LENGTH_SHORT).show();

            }
        }

        Log.d(TAG, "onCreateView: " + username);



        return view;

    }


    private void updateCache() {
        // update cache with most recent db data
        // update involves deleting old cash and
        // replacing it with new one
        // not the most efficient but fuck it


        if (root.exists()) {

            root.delete();

        } else {

            try {

                updatedData = document.getData();

                File adduser = new File(root, user.getUid());
                FileWriter writer = new FileWriter(adduser);

                Data data = new Data(updatedData);
                Gson gson = new Gson();
                String json = gson.toJson(data);

                writer.append(json);

                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Log.d(TAG, "onNavigationItemSelecteded: " + postsList);

            Bundle postInfo = new Bundle();
            postInfo.putInt("PostsLen", postsLength);
            postInfo.putStringArrayList("posts", postsList);


            switch (menuItem.getItemId()) {

                case R.id.icon_view:
                    iconViewFragment fragment = new iconViewFragment();
                    fragment.setArguments(postInfo);
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.profileContent, fragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.tile_view:
                    tileViewFragment fragment1 = new tileViewFragment();
                    fragment1.setArguments(postInfo);
                    FragmentTransaction fragmentTransaction1 = getParentFragmentManager().beginTransaction();
                    fragmentTransaction1.setReorderingAllowed(true);
                    fragmentTransaction1.replace(R.id.profileContent, fragment1);
                    fragmentTransaction1.commit();
                    return true;
            }
            return false;
        }
    };

}






