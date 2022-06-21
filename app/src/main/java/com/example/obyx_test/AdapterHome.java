package com.example.obyx_test;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyViewHolder> {

    // Variables for Tile Data
    String data1, data2;
    Context context;
    String TAG = "AdapterHome";

    // Initializing Tile Data
    public AdapterHome(Context ct, String s1, String s2 ) {
        context = ct;
        data1 = s1;
        data2 = s2;
    }

    // Default adapter Methods
    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        // something goes here i think
    }

    @Override
    public int getItemCount() {
        // make dynamic
        // data.length or some shit
        return 20;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout bottomMenu, sideMenu;
        ImageView myImage;
        ImageButton menuToggle;
        ImageView like, comment, gift;

        boolean liked = false;
        boolean toggleState = false;

        // Backend for Tile
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            String TAG = "fugg";

            // Initializing Views
            bottomMenu = itemView.findViewById(R.id.horizontal_list);
            sideMenu = itemView.findViewById(R.id.vertical_list);
            myImage = itemView.findViewById(R.id.myImage);
            menuToggle = itemView.findViewById(R.id.imageButton);
            like = itemView.findViewById(R.id.likeButton);
            comment = itemView.findViewById(R.id.commentButton);
            gift = itemView.findViewById(R.id.giftButton);


            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!liked) {
                        like.setImageResource(R.drawable.like);
                        liked = true;
                    } else {
                        like.setImageResource(R.drawable.heart);
                        liked = false;
                    }
                }
            });

            bottomMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            sideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            itemView.setOnTouchListener(new OnSwipeTouchListener(itemView.getContext()) {
                public void onSwipeRight() {
                    Toast.makeText(itemView.getContext(), "right", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSwipeRight: swiped");
                }
                public void onSwipeLeft() {
                    Log.d(TAG, "onSwipeLeft: swiped");
                    Toast.makeText(itemView.getContext(), "left", Toast.LENGTH_SHORT).show();
                }
                public void DoubleTap() {
                    Log.d(TAG, "DoubleTap: tapped");
                }
            });

            // Image onClickListener to detect when user closes menu or likes content
            myImage.setOnClickListener(new View.OnClickListener() {
                // Close the Menus
                @Override
                public void onClick(View view) {
                    Log.d("Tile", "SingleClick");
                    if(toggleState) {
                        toggleState = false;
                        bottomMenu.setVisibility(View.GONE);
                        sideMenu.setVisibility(View.GONE);

                    }
                }
            });

            //Open Menus if Image button is clicked.
            menuToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!toggleState) {
                        toggleState = true;
                        bottomMenu.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }
}
