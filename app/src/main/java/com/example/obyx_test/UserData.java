package com.example.obyx_test;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserData {

    String TAG = "UserData";
    DocumentSnapshot document;

    public UserData(String uid) {
    }


    private void newUser(String UID) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                            } else {
                                Log.d("dbGetData", "No such document");
                            }
                        } else {
                            Log.d("dbGetData", "get failed with ", task.getException());
                        }
                    }
                });

    }

    public String getUserName()
    {
        return document.get("UserName").toString();
    }

    public String getFollowers()
    {
        return document.get("Followers").toString();
    }
    public String getFollowing()
    {
        return document.get("Following").toString();
    }
    public String getUpVotes()
    {
        return document.get("UpVotes").toString();
    }
    public String getDownVotes()
    {
        return document.get("DownVotes").toString();
    }
    public String getBio()
    {
        return document.get("Bio").toString();
    }
    public ArrayList<String> getPosts()
    {
        return (ArrayList<String>)  document.get("Posts");
    }





//    followers = document.get("Followers").toString();
//    following = document.get("Following").toString();
//    positive = document.get("UpVotes").toString();
//    negative = document.get("DownVotes").toString();
//    bio = document.get("Bio").toString();
//    postsList = (ArrayList<String>)  document.get("Posts");
//
//    postsLength = postsList.size();
//                                Log.d(TAG, "onComplete: " + postsList);
//
//    updateCache();
//
//                                userName.setText(username);
//                                Followers.setText(followers);
//                                Following.setText(following);
//                                Positive.setText(positive);
//                                Negative.setText(negative);
//                                Bio.setText(bio);
//                                Posts.setText(postsLength.toString());
//
//                                loading.setVisibility(View.GONE);
//                                mainProfile.setVisibility(View.VISIBLE);





}
