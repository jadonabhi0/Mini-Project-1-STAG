package com.example.stag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stag.Models.Users;
import com.example.stag.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    // Some useful variables

    ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // hiding the action bar
        getSupportActionBar().hide();


        // setting up the progressDialog formalities

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Register");
        progressDialog.setMessage("Wait! we are registering you..");



        // creating the binding object

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        // creating the instance of auth and database

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();




        /**************************************** Register with email and password ***********************************************/



        binding.registerbutton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {



                // Maintain the app while registering email and password are null.

                if(!binding.remail.getText().toString().matches(regex) || binding.rpassword.getText().toString().isEmpty()
                        || binding.username.getText().toString().isEmpty()){
                    Intent intent  = new Intent(RegisterActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }




                // show the progressing
                progressDialog.show();



                // creating the new  user

                auth.createUserWithEmailAndPassword(binding.remail.getText().toString(),
                        binding.rpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // stop the progressing
                        progressDialog.dismiss();




                        // checking the task is successful or not

                        if(task.isSuccessful()){

                            // getting the users credentials

                            Users users = new Users(binding.username.getText().toString(),
                                                    binding.rpassword.getText().toString(),
                                                    binding.remail.getText().toString());


                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);




                            // getting the id of user

                            String id = task.getResult().getUser().getUid();





                            // saving data of user into the database

                            database.getReference().child("User").child(id).setValue(users);





                            // showing the confirmation through toast

                            Toast.makeText(RegisterActivity.this,"User Created Successfully",Toast.LENGTH_SHORT).show();





                        }else{
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        // Move to login activity if already have an account

        binding.alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });



        /*------------------------------------------- Some part of google Sign in API ---------------------------------------------*/

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
        /*----------------------------------------------------------------------------------------------------------------------------*/



    } // End of Oncreate method






    //******************************************************* Google Sign in API ******************************************************




    int RC_SIGN_IN = 75;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                Toast.makeText(RegisterActivity.this, "Sign in with Google", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();



                            // getting the data of User
                            Users users = new Users();
                            users.setUserid(user.getUid());
                            users.setUsername(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());

                            // Saving the data into database
                            database.getReference().child("User").child(user.getUid()).setValue(users);


                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.


                        }
                    }
                });
    }










}