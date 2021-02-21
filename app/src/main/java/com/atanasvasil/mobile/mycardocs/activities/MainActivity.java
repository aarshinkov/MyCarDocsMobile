package com.atanasvasil.mobile.mycardocs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.atanasvasil.mobile.mycardocs.BuildConfig;
import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        protected boolean checkUserStatus()
    {
            boolean isLoggedIn  ;
            Context context = getApplicationContext();
            SharedPreferences pref = context.getSharedPreferences("Session Data", MODE_PRIVATE);
            isLoggedIn = pref.getBoolean("isLoggedIn", false);
            return isLoggedIn ;
        }
        SharedPreferences pref = context.getSharedPreferences(
                "Session Data", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("LAST_VERSION_CODE", BuildConfig.VERSION_CODE);
        edit.putBoolean("isLoggedIn", true);// or false if you log out
        edit.commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //public void updateNavHeader() {

      //  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
       // View headerView = navigationView.getHeaderView(0);
       // TextView navUsername = headerView.findViewById(R.id.navNameTV);
       // TextView navUserMail = headerView.findViewById(R.id.navEmailTV);
      //  ImageView navUserPhot = headerView.findViewById(R.id.navImageTV);

       // navNameTV.setText(User.getDisplayName());
        //navEmailTV.setText(User.getEmail());}


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}