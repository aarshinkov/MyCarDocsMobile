package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getStringResource;

public class PolicyActivity extends AppCompatActivity {

    private TextView policyNumberTV;
    private TextView policyTypeTV;
    private TextView policyInsurerNameTV;
    private TextView policyStartDateTV;
    private TextView policyEndDateTV;
    private TextView policyStatusTV;
    private TextView policyCarInfoTV;
    private TextView policyCarLicensePlateTV;
    private TextView policyCarYearTV;
    private TextView policyCarColorTV;

    private SwipeRefreshLayout policyRefresh;

    private CircularProgressIndicator policyProgress;

    private String policyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        getSupportActionBar().setTitle(R.string.policy_view_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        policyId = intent.getStringExtra("policyId");

        policyNumberTV = findViewById(R.id.policyNumberTV);
        policyTypeTV = findViewById(R.id.policyTypeTV);
        policyInsurerNameTV = findViewById(R.id.policyInsurerNameTV);
        policyStartDateTV = findViewById(R.id.policyStartDateTV);
        policyEndDateTV = findViewById(R.id.policyEndDateTV);
        policyStatusTV = findViewById(R.id.policyStatusTV);
        policyCarInfoTV = findViewById(R.id.policyCarInfoTV);
        policyCarLicensePlateTV = findViewById(R.id.policyCarLicensePlateTV);
        policyCarYearTV = findViewById(R.id.policyCarYearTV);
        policyCarColorTV = findViewById(R.id.policyCarColorTV);

        policyRefresh = findViewById(R.id.policyRefresh);

        policyProgress = findViewById(R.id.policyProgress);
        policyProgress.setVisibility(View.VISIBLE);

        getPolicy(policyId);

        policyRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPolicy(policyId);
                policyRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        getPolicy(policyId);
        policyProgress.setVisibility(View.GONE);
        super.onResume();
    }

    private void getPolicy(String policyId) {

        policyProgress.setVisibility(View.VISIBLE);

        Retrofit retrofit = getRetrofit();

        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        policiesApi.getPolicy(policyId).enqueue(new Callback<Policy>() {
            @Override
            public void onResponse(Call<Policy> call, Response<Policy> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.policy_get_error, Toast.LENGTH_LONG).show();
                    policyProgress.setVisibility(View.GONE);
                    return;
                }

                if (response.code() == 200) {
                    Policy policy = response.body();

                    if (policy != null) {

                        policyNumberTV.setText("#" + policy.getNumber());

                        String type = getStringResource(getApplicationContext(), "policy_type_" + policy.getType());
                        policyTypeTV.setText(type);
                        policyInsurerNameTV.setText(policy.getInsName());

                        Date now = new Date();
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                        date.setTime(policy.getStartDate().getTime());
                        policyStartDateTV.setText(sdf.format(date));

                        if (now.before(date)) {
                            policyStatusTV.setTextColor(getApplicationContext().getResources().getColor(R.color.warning));
                            policyStatusTV.setText(getString(R.string.policy_status_pending));
                        }

                        date = new Date();
                        date.setTime(policy.getEndDate().getTime());
                        policyEndDateTV.setText(sdf.format(date));

                        if (now.after(date)) {
                            policyStatusTV.setTextColor(getApplicationContext().getResources().getColor(R.color.danger));
                            policyStatusTV.setText(getString(R.string.policy_status_expired));
                        }

                        Car car = policy.getCar();

                        policyCarInfoTV.setText(getString(R.string.car_info, car.getBrand(), car.getModel()));
                        policyCarLicensePlateTV.setText(car.getLicensePlate());
                        policyCarYearTV.setText(car.getYear().toString());
                        policyCarColorTV.setText(car.getColor());

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.policy_not_found, Toast.LENGTH_LONG).show();
                    }
                }

                policyProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Policy> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                policyProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_operations, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {

            Intent intent = new Intent(getApplicationContext(), PolicyUpdateActivity.class);
            intent.putExtra("policyId", getIntent().getStringExtra("policyId"));
            startActivity(intent);
            return false;

        } else if (item.getItemId() == R.id.action_delete) {

            AlertDialog.Builder builder = new AlertDialog.Builder(PolicyActivity.this);

            builder.setTitle(R.string.policy_delete_title);

            builder.setMessage(R.string.policy_delete_message);

            ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setMessage(getString(R.string.policy_delete_process));

            builder.setPositiveButton(R.string.yes, (dialog, which) -> {

                loadingDialog.show();

                Retrofit retrofit = getRetrofit();

                PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

                String policyId = getIntent().getStringExtra("policyId");

                policiesApi.deletePolicy(policyId).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                        if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(), R.string.policy_not_found, Toast.LENGTH_LONG).show();
                            loadingDialog.hide();
                            return;
                        }

                        Boolean result = response.body();

                        if (result != null) {
                            if (result) {
                                loadingDialog.hide();
                                Toast.makeText(getApplicationContext(), R.string.policy_delete_success, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("fragment", "policies");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                return;
                            }
                        }

                        Toast.makeText(getApplicationContext(), R.string.policy_delete_error, Toast.LENGTH_LONG).show();
                        loadingDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), R.string.policy_delete_error, Toast.LENGTH_LONG).show();
                        loadingDialog.hide();
                    }
                });
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });

            AlertDialog dialog = builder.create();

            dialog.show();
            return false;
        }

        onBackPressed();
        return true;
    }
}