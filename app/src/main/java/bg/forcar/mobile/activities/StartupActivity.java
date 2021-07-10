package bg.forcar.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aarshinkov.mobile.mycardocs.R;
import bg.forcar.mobile.utils.LoggedUser;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;
import static bg.forcar.mobile.utils.Utils.isLoggedIn;

public class StartupActivity extends AppCompatActivity {

    private TextView appVersionTV;

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        appVersionTV = findViewById(R.id.appVersionTV);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        loggedUser = getLoggedUser(pref);
//        Utils.setAppLocale(getResources(), language);

        CircularProgressIndicator appCPI = findViewById(R.id.appCPI);
        appCPI.setVisibility(View.VISIBLE);

        String version;
        try {
            Context context = getApplicationContext();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = 'v' + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
            version = "v1.0.0";
        }

        appVersionTV.setText(version);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new Handler().postDelayed(() -> {
            if (isLoggedIn(pref)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 2500);

    }
}
