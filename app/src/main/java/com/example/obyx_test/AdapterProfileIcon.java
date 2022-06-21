package com.example.obyx_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class AdapterProfileIcon  extends RecyclerView.Adapter<AdapterProfileIcon.MyViewHolder> {

    Context context;
    ArrayList Posts;
    Integer postLen;
    String TAG = "API";

    // Create a storage reference from our app
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public AdapterProfileIcon(Context ct, ArrayList posts, int listLen) {

        context = ct;
        Posts = posts;
        postLen = listLen;

    }

    // Default adapter Methods
    @NonNull
    @NotNull
    @Override
    public AdapterProfileIcon.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.icon, parent, false);


        return new AdapterProfileIcon.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProfileIcon.MyViewHolder holder, int position) {
        // get content link for each post in post/ in db
        // set icon img scr to that img
        StorageReference gsReference = storage.getReferenceFromUrl("gs://testing-1310c.appspot.com/Posts");
        StorageReference fileRef = gsReference.child(Posts.get(position).toString() + ".jpg");


        final long ONE_MEGABYTE = (1024 * 1024) * 10;
        fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.icon.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("TAG", "onFailure: fuck" + fileRef);
            }
        });

        holder.icon.setImageResource(R.drawable.camera);

    }

    @Override
    public int getItemCount() {
        return postLen;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);



            icon = itemView.findViewById(R.id.icon);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            // set image to personal posts
            //icon.setImageResource();


        }
    }

}
