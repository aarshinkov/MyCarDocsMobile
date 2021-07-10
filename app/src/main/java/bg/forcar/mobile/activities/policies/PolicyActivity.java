package bg.forcar.mobile.activities.policies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import bg.forcar.mobile.R;
import bg.forcar.mobile.activities.MainActivity;
import bg.forcar.mobile.api.PoliciesApi;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.responses.policies.Policy;
import bg.forcar.mobile.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;
import static bg.forcar.mobile.utils.Utils.getStringResource;

public class PolicyActivity extends AppCompatActivity {

    private TextView policyNumberTV;
    private TextView policyTypeTV;
    private TextView policyInsurerNameTV;
    private TextView policyStartDateTV;
    private TextView policyEndDateTV;
    private TextView policyStatusTV;
    private ProgressBar policyValidProgress;
    private TextView policyValidProgressTV;
    private TextView policyCarInfoTV;
    private TextView policyCarLicensePlateTV;
    private TextView policyCarYearTV;
    private TextView policyCarColorTV;

    private MaterialButton policyEditBtn;
    private MaterialButton policyDeleteBtn;

    private SwipeRefreshLayout policyRefresh;

    private CircularProgressIndicator policyProgress;

    private LoggedUser loggedUser;
    private SharedPreferences pref;

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
        policyValidProgress = findViewById(R.id.policyValidProgress);
        policyValidProgressTV = findViewById(R.id.policyValidProgressTV);
        policyCarInfoTV = findViewById(R.id.policyCarInfoTV);
        policyCarLicensePlateTV = findViewById(R.id.policyCarLicensePlateTV);
        policyCarYearTV = findViewById(R.id.policyCarYearTV);
        policyCarColorTV = findViewById(R.id.policyCarColorTV);

        policyEditBtn = findViewById(R.id.policyEditBtn);
        policyDeleteBtn = findViewById(R.id.policyDeleteBtn);

        policyRefresh = findViewById(R.id.policyRefresh);

        policyProgress = findViewById(R.id.policyProgress);
        policyProgress.setVisibility(View.VISIBLE);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        getPolicy(policyId);

        policyRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPolicy(policyId);
                policyRefresh.setRefreshing(false);
            }
        });

        policyEditBtn.setOnClickListener(v -> {
            editPolicy();
        });

        policyDeleteBtn.setOnClickListener(v -> {
            deletePolicy();
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

        policiesApi.getPolicy(policyId, loggedUser.getAuthorization()).enqueue(new Callback<Policy>() {
            @Override
            public void onResponse(@NotNull Call<Policy> call, @NotNull Response<Policy> response) {

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
                        Date startDate = new Date();
                        Date endDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                        startDate.setTime(policy.getStartDate().getTime());
                        policyStartDateTV.setText(sdf.format(startDate));

                        endDate.setTime(policy.getEndDate().getTime());
                        policyEndDateTV.setText(sdf.format(endDate));

                        if (now.after(startDate) && now.before(endDate)) {
                            policyStatusTV.setTextColor(getApplicationContext().getResources().getColor(R.color.success));
                            policyStatusTV.setText(getString(R.string.policy_status_active));

                            long diff = endDate.getTime() - startDate.getTime();
                            int allDays = (int) (diff / (1000 * 60 * 60 * 24));

                            diff = endDate.getTime() - new Date().getTime();
                            int remainingDays = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

                            policyValidProgress.setMax(allDays);
                            policyValidProgress.setProgress(allDays - remainingDays);

                            if (remainingDays == 1) {
                                policyValidProgressTV.setText(getString(R.string.policy_days_remaining_1));
                            } else {
                                policyValidProgressTV.setText(getString(R.string.policy_days_remaining, remainingDays));
                            }
                        }

                        if (now.before(startDate)) {
                            policyStatusTV.setTextColor(getApplicationContext().getResources().getColor(R.color.warning));
                            policyStatusTV.setText(getString(R.string.policy_status_pending));

                            policyValidProgress.setMax(1);
                            policyValidProgress.setProgress(0);

                            long diff = endDate.getTime() - startDate.getTime();
                            int allDays = (int) (diff / (1000 * 60 * 60 * 24));

                            if (allDays == 1) {
                                policyValidProgressTV.setText(getString(R.string.policy_total_days_1));
                            } else {
                                policyValidProgressTV.setText(getString(R.string.policy_total_days, allDays));
                            }
                        }

                        if (now.after(endDate)) {
                            policyStatusTV.setTextColor(getApplicationContext().getResources().getColor(R.color.danger));
                            policyStatusTV.setText(getString(R.string.policy_status_expired));

                            policyValidProgress.setMax(1);
                            policyValidProgress.setProgress(2);

                            long diff = endDate.getTime() - startDate.getTime();
                            int allDays = (int) (diff / (1000 * 60 * 60 * 24));

                            if (allDays == 1) {
                                policyValidProgressTV.setText(getString(R.string.policy_days_completed_1));
                            } else {
                                policyValidProgressTV.setText(getString(R.string.policy_days_completed, allDays));
                            }
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
            public void onFailure(@NotNull Call<Policy> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                policyProgress.setVisibility(View.GONE);
//                getPolicy(policyId);
            }
        });
    }

    private void editPolicy() {

        Intent intent = new Intent(getApplicationContext(), PolicyUpdateActivity.class);
        intent.putExtra("policyId", getIntent().getStringExtra("policyId"));
        startActivity(intent);
    }

    private void deletePolicy() {

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

            policiesApi.deletePolicy(policyId, loggedUser.getAuthorization()).enqueue(new Callback<Boolean>() {
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
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.policy_delete_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    loadingDialog.hide();
                }

                @Override
                public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.policy_delete_error, Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}