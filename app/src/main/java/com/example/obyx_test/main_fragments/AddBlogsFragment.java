package com.example.obyx_test.main_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.obyx_test.R;
import com.example.obyx_test.main_activities.Home;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBlogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBlogsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;
    private static final int CAMERA_REQUEST_CODE = 2;

    FrameLayout frameLayout;
    PreviewView previewView;

    EditText postTitle, description;
    ImageView PostContent, captureButton, exit, flash;
    ProgressBar uploadLoading;
    ImageView flipCamera, cameraSelect;
    ImageView chevron, imagePreview, done;
    ImageView togglemode;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final int REQUEST_IMAGE_CAPTURE = 69;
    LinearLayout cameraSelectMenu;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ADDBLOG";

    // Other Variables
    boolean flashMode;
    String currentPhotoPath;
    FirebaseUser user;
    boolean isRecording;
    boolean captureMode;
    String cameraMode;
    int lensFacing;

    // CameraX
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    CameraControl cameraControl;
    Camera camera;
    ImageCapture imageCapture;
    ExecutorService cameraExecutor;
    CameraSelector cameraSelector;
    Bitmap tempImg;
    Handler handler;

    Bitmap bmp;

    ConstraintLayout overlayBefore, overlayAfter;

    final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddBlogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBlogsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBlogsFragment newInstance(String param1, String param2) {
        AddBlogsFragment fragment = new AddBlogsFragment();
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

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this.getContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this.getActivity(),
                PERMISSIONS,
                CAMERA_REQUEST_CODE
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_blogs, container, false);

        handler = new Handler();
        flashMode = false;

        frameLayout = view.findViewById(R.id.frameLayout);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // initialize all views
        uploadLoading = view.findViewById(R.id.uploadLoading);
        overlayBefore = view.findViewById(R.id.overlayBefore);
        overlayAfter = view.findViewById(R.id.overlayAfter);
        previewView = view.findViewById(R.id.previewView);
        imagePreview = view.findViewById(R.id.imagePreview);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.getContext());

        //bottom section
        togglemode = view.findViewById(R.id.togglemode);
        captureButton = view.findViewById(R.id.captureButton);

        // default capture mode
        // true for camera
        // false for video
        captureMode = true;

        // left section
        exit = view.findViewById(R.id.exit);

        //right section
        flash = view.findViewById(R.id.flash);
        flipCamera = view.findViewById(R.id.flip);
        chevron = view.findViewById(R.id.more);

        //After Capture layout
        done = view.findViewById(R.id.done);

        // variables
        lensFacing = CameraSelector.LENS_FACING_BACK;

        // initialize camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.getContext());
        cameraExecutor = Executors.newSingleThreadExecutor();

        //start camera if permission has been granted by user
        if(!hasCameraPermission()){
            ActivityCompat.requestPermissions(this.getActivity(), PERMISSIONS, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        imagePreview.setVisibility(View.GONE);

        //cameraSelectMenu.setVisibility(View.GONE);


        // init cameraProvider
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this.getContext()));

        // set all onclick listeners

        togglemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureMode) {
                    captureButton.setImageResource(R.drawable.capturevideowhite);
                    togglemode.setImageResource(R.drawable.camera);
                } else {
                    captureButton.setImageResource(R.drawable.captureimg);
                    togglemode.setImageResource(R.drawable.video);
                }
            captureMode = !captureMode;
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                overlayAfter.setVisibility(View.GONE);
                uploadLoading.setVisibility(View.VISIBLE);
                submitpost();
            }
        });

        previewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // focus
                focusOnClick();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // exit to home
                imagePreview.setImageResource(R.color.black);
                imagePreview.setVisibility(View.GONE);
                previewView.setVisibility(View.VISIBLE);
                overlayAfter.setVisibility(View.GONE);
                overlayBefore.setVisibility(View.VISIBLE);

            }
        });

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change lens facing
                if ( lensFacing == CameraSelector.LENS_FACING_FRONT ) {
                    lensFacing = CameraSelector.LENS_FACING_BACK;
                }else  {
                    lensFacing = CameraSelector.LENS_FACING_FRONT;
                }
                // rebind previewView
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        });

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashMode){
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                    flash.setImageResource(R.drawable.flash);
                    flashMode = false;
                } else {
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                    flash.setImageResource(R.drawable.flashon);
                    flashMode = true;
                }

            }
        });

        chevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // more or something
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    toggleCapture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    void focusOnClick() {
        // do something here

    }

    void toggleCapture() throws IOException {

        if (captureMode) {
            // take a picture
            takePicture();


        } else {
            // take a video

        }


    }

    private void takePicture() throws IOException {
        if (flashMode) {
            camera.getCameraControl().enableTorch(true);
        }

        //use this code later to create localfile.
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(createImageFile()).build();

        imageCapture.takePicture(cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    public void onCaptureSuccess(ImageProxy image) {

                        tempImg = convertImageProxyToBitmap(image);
                        image.close();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imagePreview.setImageBitmap(tempImg);
                                overlayBefore.setVisibility(View.GONE);
                                overlayAfter.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                }

        );

        previewView.setVisibility(View.GONE);

        imagePreview.setVisibility(View.VISIBLE);

    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        imageCapture =
                new ImageCapture.Builder()
                        .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1080, 1920))
                        .build();


        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, imageAnalysis, preview);
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy image) {

        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        bmp = BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
        return bmp;
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    public void submitpost() {

        String Title = "test";
                //postTitle.getText().toString();
        String desc = "Test";
                //description.getText().toString();

        StorageReference storageRef = storage.getReference();

        StorageReference mountainsRef = storageRef.child("Posts");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileRef =  user.getUid() + "_" + timeStamp ;
        StorageReference mountainImagesRef = storageRef.child("Posts/" + FileRef + ".jpg");

        // While the file names are the same, the references point to different files
        mountainImagesRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        // Create a new posts entry
        Map<String, Object> Post = new HashMap<>();
        Post.put("Comments", 0);
        Post.put("ContentLink", "Posts/" + FileRef);
        Post.put("DateCreated", timeStamp);
        Post.put("Likes", 0);
        Post.put("PostID", FileRef);
        Post.put("Public", true);
        Post.put("UserID", user.getUid());
        Post.put("ViewCount", 0);

        // Update users profile

        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                        db.collection("Posts").document(FileRef).set(Post);
                        db.collection("Users").document(user.getUid()).update("Posts", FieldValue.arrayUnion(FileRef));
                        db.collection("UsersStats").document(user.getUid()).update("Posts", FieldValue.arrayUnion(FileRef));
                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("TAG", "Transaction success!");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                uploadLoading.setVisibility(View.GONE);
                                imagePreview.setImageResource(R.color.black);
                                imagePreview.setVisibility(View.GONE);
                                previewView.setVisibility(View.VISIBLE);
                                overlayBefore.setVisibility(View.VISIBLE);

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Transaction failure.", e);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == Activity.RESULT_OK){

                File imgFile = new  File(currentPhotoPath);

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    PostContent.setImageBitmap(myBitmap);

                };
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}

