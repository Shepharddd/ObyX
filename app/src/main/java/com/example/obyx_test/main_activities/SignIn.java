package com.example.obyx_test.main_activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.obyx_test.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    // Add appropriate Views
    Button signIn, altSignIn;
    EditText email, password;
    String Email, Password;

    // Firebase things
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserIn();
            }
        });

        altSignIn = findViewById(R.id.altSignIn);
        altSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    public void signUserIn() {



        // Get text off page and sign user in
        Email = email.getText().toString();
        Password = password.getText().toString();

        if (Email.equals("") || Password.equals("")) {
            Toast.makeText(this, "Please fill in appropriate fields", Toast.LENGTH_SHORT).show();
            return;
        } else {

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("signUserIn", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                signInSuccessful(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("signUserIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignIn.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                signInUnsuccessful(-3);
                            }
                        }
                    });
        }
    }

    // onActivityResult
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    public void googleSignIn() {

        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
                // Add FaceBook
                // Add Twitter

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_create_intent]
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {

        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {

            //check weather user is signing in for the first time
            boolean newUser = result.getIdpResponse().isNewUser();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (newUser) {
                // If new user register New User
                newUserSignIn(user);

            } else {
                // If logged in before then sign in user
                signInSuccessful(user);
            }

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            signInUnsuccessful(response.getError().getErrorCode());
        }
    }

    public void newUserSignIn(FirebaseUser user) {

        String Name = user.getDisplayName().replaceAll("\\s+","");

        Map<String, Object> User = new HashMap<>();
        User.put("DateCreated", System.currentTimeMillis());
        User.put("Email", user.getEmail());
        User.put("UserId", user.getUid());
        User.put("UserLink", Name + "_" + user.getUid().substring(0, 4));
        User.put("DateOfBirth", 0);
        User.put("ProfilePicLink", "");

        Map<String, Object> userStats = new HashMap<>();
        userStats.put("Bio", "Im Boring");
        userStats.put("DateCreated", System.currentTimeMillis() / 1000L);
        userStats.put("DownVotes", 0);
        userStats.put("UpVotes", 0);
        userStats.put("Followers", 0);
        userStats.put("Following", 0);
        userStats.put("ProfilePicLink", "");
        userStats.put("UserLink", Name + "_" + user.getUid().substring(0, 4));
        userStats.put("UserName", Name);

        //add them to Firestore
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                db.collection("Users").document(user.getUid()).set(User);
                db.collection("UsersStats").document(user.getUid()).set(userStats);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "Transaction success!");
                signInSuccessful(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Transaction failure.", e);
            }
        });
    }


    public void signInSuccessful(FirebaseUser user) {
        // Handle Successful Sign in
        String log = "Welcome: " + user.getDisplayName();
        Log.d("signInSuccessful", log);
        Toast.makeText(SignIn.this, "Welcome: " + user.getDisplayName() ,
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignIn.this, Home.class));
    }

    public void signInUnsuccessful(int err) {
        // Handle unSuccessful Sign in
        Log.d("signInUnsuccessful", Integer.toString(err) );
        Toast.makeText(SignIn.this, "Authentication failed: " + Integer.toString(err) ,
                Toast.LENGTH_SHORT).show();
        //close or back?
    }
}