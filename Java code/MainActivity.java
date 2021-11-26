package com.example.stag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import com.example.stag.Adapters.FragmentAdapters;
import com.example.stag.databinding.ActivityMainBinding;
import com.facebook.login.LoginManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;


    ViewPager viewPager;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // creating the binding object

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // getting the instances

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        // setting up the viewholder and tablayout
        binding.viewpager.setAdapter(new FragmentAdapters(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewpager);



    } // end of oncreate method


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);





    }


    //----------------------------------------- Logot functionality code -------------------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.settings:
                Intent i = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.logout:
                auth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent  = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }

    // -------------------------------------------------------------------------------------------------
}