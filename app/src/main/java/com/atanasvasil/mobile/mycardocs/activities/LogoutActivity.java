package com.atanasvasil.mobile.mycardocs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;

import com.atanasvasil.mobile.mycardocs.R;

public class LogoutActivity extends  AppCompatActivity{

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    Button logout=null;
    Button close=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logout = (Button) findViewById(R.id.logoutBtn);
        close = (Button)findViewById(R.id.closeBtn);
    }

    public  void logoutUser(View view){
        editor.clear();
        editor.commit();
        // След излизане пренасочваме потребителя към Login Activity
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Затваряне на всички дейности
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void close(View view){
        finish();
    }
}