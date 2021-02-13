package com.atanasvasil.mobile.mycardocs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import android.content.Intent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    static final int TAKE_PICTURE = 666;

    private EditText registerEmailЕТ;
    private EditText registerPasswordЕТ;
    private EditText registerConfirmPasswordЕТ;
    private EditText registerFirstNameЕТ;
    private EditText registerLastNameЕT;
    private Button registerCancelBtn;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.registration);

        //Initialize fields
        //...

        registerCancelBtn = findViewById(R.id.registerCancelBtn);
        registerBtn = findViewById(R.id.registerBtn);

        registerCancelBtn.setOnClickListener(v -> {
            // Cancel button
        });

        registerBtn.setOnClickListener(v -> {
                    if (v.getId() == R.id.registerBtn) {
                        if (registerEmailЕТ.getText().length() > 0
                                && registerPasswordЕТ.getText().length() > 0
                                && registerPasswordЕТ.getText().toString().equals(registerConfirmPasswordЕТ.getText().toString())) {

                            String password = registerPasswordЕТ.getText().toString();
                            String firstName = registerFirstNameЕТ.getText().toString();
                            String lastName = registerLastNameЕT.getText().toString();

                            User user = new User();
                            user.setPassword(password);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);

                            new RegisterAsyncTask(user).execute();

                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Please provide all the necessary information!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                };
            View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(pictureIntent, TAKE_PICTURE);
                    return true;
                }
            };

            @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap avatarImage = (Bitmap) extras.get("data");
                }
            }

            private class RegisterAsyncTask extends AsyncTask<Void, Void, Void> {

                User user;
                boolean isSuccess = false;

                ProgressDialog dialog;

                RegisterAsyncTask(User user) {
                    this.user = user;
                    dialog = new ProgressDialog(RegisterActivity.this);
                }

                @Override
                protected void onPreExecute() {
                    dialog.setTitle("Registration in progress...");
                    dialog.show();
                }
                // Registration button

                @Override
                protected Void doInBackground(Void... voids) {

                    String urlString = String.format("http://94.72.129.171:9003/MyCarDocs/RegisterUser?" +
                                    "password=%s&fname=%s&lname=%s",
                            user.getPassword(), user.getFirstName(), user.getLastName());

                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                        String result = reader.readLine();

                        if (result != null) {
                            if (result.contains("true")) {
                                isSuccess = true;
                            }
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null)
                            urlConnection.disconnect();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    dialog.hide();

                    if (isSuccess) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }