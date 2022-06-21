package com.example.obyx_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterProfileTile  extends RecyclerView.Adapter<AdapterProfileTile.MyViewHolder> {

    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ArrayList Posts;
    Integer postLen;

    public AdapterProfileTile(Context ct, ArrayList posts, int listLen) {
        context = ct;
        Posts = posts;
        postLen = listLen;
    }

    @NonNull
    @Override
    public AdapterProfileTile.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile, parent, false);
        return new AdapterProfileTile.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProfileTile.MyViewHolder holder, int position) {
        // get content link for each post in post/ in db
        // set icon img scr to that img
        StorageReference gsReference = storage.getReferenceFromUrl("gs://testing-1310c.appspot.com/Posts");
        StorageReference fileRef = gsReference.child(Posts.get(position).toString() + ".jpg");


        final long ONE_MEGABYTE = (1024 * 1024) * 5;
        fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.myImage.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("TAG", "onFailure: fuck" + fileRef);
            }
        });

        holder.loding.setVisibility(View.GONE);

        holder.myImage.setVisibility(View.VISIBLE);

        holder.myImage.setImageResource(R.drawable.camera);

    }

    @Override
    public int getItemCount() {
        return postLen;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout bottomMenu, sideMenu;
        ImageView myImage;
        ProgressBar loding;
        ImageButton menuToggle;
        ImageView like, comment, gift;

        boolean liked = false;
        boolean toggleState = false;

        // Backend for Tile
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            String TAG = "fugg";

            // Initializing Views
            loding = itemView.findViewById(R.id.loding);
            myImage = itemView.findViewById(R.id.myImage);


        }
    }


}
