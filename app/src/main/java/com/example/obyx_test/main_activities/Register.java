package com.example.obyx_test.main_activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.Edits;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.obyx_test.Data;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    String Name, Email, Password, Confirm;
    EditText name, email, password, confirm;
    Button Register, altSignIn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();

        Register = findViewById(R.id.register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerWithEmailAndPassword();
            }
        });

        altSignIn = findViewById(R.id.altSignIn);
        altSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerWithGoogle();
            }
        });
    }

    public void registerWithEmailAndPassword() {
        Name = name.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        Confirm = confirm.getText().toString();

        // check if Passwords match
        if (Password.equals(Confirm)) {

            InitializeUser();

        } else {
            //if passwords don't match, notify user.
            Log.d("register", "failed");
            Toast.makeText(Register.this, "Passwords did not match.",
                    Toast.LENGTH_SHORT).show();
            // clear passwords
        }
    }

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in

            startActivity(new Intent(Register.this, SignIn.class));

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.

            signInUnsuccessful();
        }
    }

    public void registerWithGoogle() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);

    }


    public void InitializeUser() {
        //if  Create User
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signUserIn", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Set Users Name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Name.replaceAll("\\s+",""))
                                    .build();

                            Map<String, Object> User = new HashMap<>();
                            User.put("DateCreated", System.currentTimeMillis());
                            User.put("Email", user.getEmail());
                            User.put("UserId", user.getUid());
                            User.put("UserLink", Name + "_" + user.getUid().substring(0, 4));
                            User.put("DateOfBirth", 0);
                            User.put("Posts", Arrays.asList());
                            User.put("ProfilePicLink", "");

                            Map<String, Object> userStats = new HashMap<>();
                            userStats.put("Bio", "Im Boring");
                            userStats.put("DateCreated", System.currentTimeMillis());
                            userStats.put("DownVotes", 0);
                            userStats.put("UpVotes", 0);
                            userStats.put("Followers", 0);
                            userStats.put("Following", 0);
                            userStats.put("ProfilePicLink", "");
                            userStats.put("UserLink", Name + "_" + user.getUid().substring(0, 4));
                            userStats.put("UserName", Name.replaceAll("\\s+",""));
                            userStats.put("Posts", Arrays.asList());

                            File root = new File(Environment.getDataDirectory(), "Users");
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            try {

                                File adduser = new File(root, user.getUid());
                                FileWriter writer = new FileWriter(adduser);

                                Data data = new Data(userStats);
                                Gson gson = new Gson();
                                String json = gson.toJson(data);

                                writer.append(json);

                                writer.flush();
                                writer.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                                    db.collection("Users").add(User);
                                    db.collection("UsersStats").document(user.getUid()).set(userStats);

                                    // Success
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "Transaction success!");
                                    String log = "User Created: " + user.getDisplayName();
                                    Log.d("signInSuccessful", log);
                                    Toast.makeText(Register.this, log,
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Transaction failure.", e);
                                        }
                                    });

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w("signUserIn", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            signInUnsuccessful();
                        }
                    }
                });


        // goto sign in
        startActivity(new Intent(Register.this, SignIn.class));
    }

    public void signInUnsuccessful() {
        Log.d("signInUnsuccessful", "Failed");
    }
}