package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.atanasvasil.mobile.mycardocs.R;

public class PolicyUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_update);

        getSupportActionBar().setTitle("Edit a policy");
        
    }
}