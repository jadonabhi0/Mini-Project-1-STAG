package com.example.stag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.example.stag.Models.Users;
import com.example.stag.databinding.ActivityLoginBinding;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    // some useful variables

    ActivityLoginBinding binding;
    FirebaseDatabase database;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        // hiding the Action bar
        getSupportActionBar().hide();


        // creating the object of binding

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // creating the instances

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mCallbackManager = CallbackManager.Factory.create();





        // creating the progressDialog

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in ....");
        progressDialog.setTitle("Login");


        /************************************** Login with email and password ***************************************************************
         * *********************************************************************************************************************************/



        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Maintain the app while logging email and password are null.

                if (binding.loginemail.getText().toString().isEmpty()|| !binding.loginemail.getText().toString().matches(regex) ) {

                    binding.loginemail.setError("Enter your email");
                    return;

                }

                if (binding.loginpassword.getText().toString().isEmpty()){
                    binding.loginpassword.setError("Enter password");
                    return;
                }

                // showing the progress
                progressDialog.show();

                auth.signInWithEmailAndPassword(binding.loginemail.getText().toString(),
                        binding.loginpassword.getText().toString()).addOnCompleteListener
                        (new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // stopping the progress
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // move to sign up activity if don't have an account

        binding.signuptextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        // if the user is already sign in then not show login activity repeatedly
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }



        // ------------------------------ Some code of google Sign in API inside oncreate method ---------------

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        binding.buttongoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //------------------------------------ Some Code facebook login API ------------------------------------

        binding.buttonfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("abhi", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("abhi", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("abhi", "facebook:onError", error);
                    }
                });
            }
        });



        //------------------------------------ Some Code Github login API ------------------------------------


        binding.buttongithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,GithubAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });







    } // end of oncreate method




    /**************************************************** Login with Google API **************************************************************
     * ***************************************************************************************************************************************/


        int RC_SIGN_IN = 65;
        private void signIn() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase


                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());




                } catch (ApiException e) {
                    // Google Sign In failed
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                 }
             }
        }



            private void firebaseAuthWithGoogle(String idToken) {
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, shows message

                            Toast.makeText(LoginActivity.this, "Sign in with google", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();


                            // Getting the data of user
                            Users users = new Users();
                            users.setUserid(user.getUid());
                            users.setUsername(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());

                            // Saving the users data into database
                            database.getReference().child("User").child(user.getUid()).setValue(users);


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }





/******************************************************** Facebook Sign in API ****************************************************************
 * ********************************************************************************************************************************************/






    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("abhi1", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);

                            // Getting the data of user
                            Users users = new Users();
                            users.setUserid(user.getUid());
                            users.setUsername(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            users.setEmail(user.getEmail());

                            // Saving the users data into database
                            database.getReference().child("User").child(user.getUid()).setValue(users);



                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }




/******************************************************** Github Sign in API ****************************************************************
 * *******************************************************************************************************************************************/






}

